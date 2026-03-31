package resources;

import resources.data.general.ErrorResponse;
import resources.data.general.SuccessResponse;
import resources.data.general.ErrorCodes;
import resources.data.general.VerifyToken;
import resources.data.ModAccountData;
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

@Path("/modaccount")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModAccountResource {

  // private static final Logger LOG =
  // Logger.getLogger(LoginResource.class.getName());
  private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
  private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

  public ModAccountResource() {
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response modUsers(ModAccountData data) {
    try {
      if (data == null || !data.validInput()) {
        return Response.ok(new ErrorResponse(ErrorCodes.INVALID_INPUT, ErrorCodes.MSG_INVALID_INPUT)).build();
      }

      Entity sessionEntity = VerifyToken.getValidSession(data.token);
      if (sessionEntity == null) {
        return Response.ok(new ErrorResponse(ErrorCodes.INVALID_TOKEN, ErrorCodes.MSG_INVALID_TOKEN)).build();
      }

      if (VerifyToken.isTokenExpired(sessionEntity)) {
        return Response.ok(new ErrorResponse(ErrorCodes.TOKEN_EXPIRED, ErrorCodes.MSG_TOKEN_EXPIRED)).build();
      }
      String Realusername = sessionEntity.getString("username");
      String Realrole = sessionEntity.getString("role");
      String targetUsername = data.input.username.trim().toLowerCase();

      Key targetKey = userKeyFactory.newKey(targetUsername);
      Entity targetUser = datastore.get(targetKey);

      if (targetUser == null) {
        return Response.ok(new ErrorResponse(ErrorCodes.USER_NOT_FOUND, ErrorCodes.MSG_USER_NOT_FOUND)).build();
      }

      String targetRole = targetUser.getString("role");

      boolean allowed = false;

      if ("ADMIN".equals(Realrole)) {
        allowed = true;
      } else if ("BOFFICER".equals(Realrole)) {
        allowed = Realusername.equals(targetUsername) || "USER".equals(targetRole);

      } else if ("USER".equals(Realrole)) {
        allowed = Realusername.equals(targetUsername);
      }

      if (!allowed) {
        return Response.ok(new ErrorResponse(ErrorCodes.UNAUTHORIZED, ErrorCodes.MSG_UNAUTHORIZED)).build();
      }

      Entity.Builder updated = Entity.newBuilder(targetUser);

      if (data.input.attributes.phone != null && !data.input.attributes.phone.isBlank()) {
        updated.set("phone", data.input.attributes.phone);
      }

      if (data.input.attributes.address != null && !data.input.attributes.address.isBlank()) {
        updated.set("address", data.input.attributes.address);
      }

      datastore.put(updated.build());

      return Response.ok(new SuccessResponse(new MessageResult("Updated successfully"))).build();

    } catch (Exception e) {
      return Response.ok(new ErrorResponse(ErrorCodes.INTERNAL_ERROR, ErrorCodes.MSG_INTERNAL_ERROR)).build();
    }

  }
}