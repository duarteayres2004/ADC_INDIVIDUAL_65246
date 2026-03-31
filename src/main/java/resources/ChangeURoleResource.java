package resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import resources.data.general.ErrorResponse;
import resources.data.general.SuccessResponse;
import resources.data.general.ErrorCodes;
import resources.data.general.VerifyToken;
import resources.data.ChangeURoleData;
import resources.data.general.MessageResult;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

@Path("/changeuserrole")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChangeURoleResource {

  private static final Logger LOG = Logger.getLogger(ChangeURoleResource.class.getName());
  private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
  private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

  public ChangeURoleResource() {
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response changeUserRole(ChangeURoleData data) {
    try {
      if (data == null || !data.validInput()) {
        return Response.ok(new ErrorResponse(ErrorCodes.FORBIDDEN, ErrorCodes.MSG_FORBIDDEN)).build();
      }

      Entity sessionEntity = VerifyToken.getValidSession(data.token);
      if (sessionEntity == null) {
        return Response.ok(new ErrorResponse(ErrorCodes.INVALID_TOKEN, ErrorCodes.MSG_INVALID_TOKEN)).build();
      }

      if (VerifyToken.isTokenExpired(sessionEntity)) {
        return Response.ok(new ErrorResponse(ErrorCodes.TOKEN_EXPIRED, ErrorCodes.MSG_TOKEN_EXPIRED)).build();
      }

      String expectedRole = sessionEntity.getString("role");
      if (!VerifyToken.isAllowedRole(expectedRole, new String[] { "ADMIN" })) {
        return Response.ok(new ErrorResponse(ErrorCodes.UNAUTHORIZED, ErrorCodes.MSG_UNAUTHORIZED)).build();
      }

      String username = data.input.username.trim().toLowerCase();
      String newRole = data.input.newRole.trim().toUpperCase();

      if (!newRole.equals("ADMIN") && !newRole.equals("BOFFICER") && !newRole.equals("USER")) {
        return Response.ok(new ErrorResponse(ErrorCodes.FORBIDDEN, ErrorCodes.MSG_FORBIDDEN)).build();
      }

      Key userKey = userKeyFactory.newKey(username);
      Entity userEntity = datastore.get(userKey);

      if (userEntity == null) {
        return Response.ok(new ErrorResponse(ErrorCodes.USER_NOT_FOUND, ErrorCodes.MSG_USER_NOT_FOUND)).build();
      }

      Entity updatedUser = Entity.newBuilder(userEntity).set("role", newRole).build();
      datastore.update(updatedUser);

      return Response.ok(new SuccessResponse(new MessageResult("role updated successfully"))).build();

    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error changing user role", e);
      return Response.ok(new ErrorResponse(ErrorCodes.INTERNAL_ERROR, ErrorCodes.MSG_INTERNAL_ERROR)).build();
    }

  }
}
