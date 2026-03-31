package resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import auth.AuthToken;

import resources.data.general.ErrorResponse;
import resources.data.general.SuccessResponse;
import resources.data.general.ErrorCodes;
import resources.data.general.VerifyToken;
import resources.data.CreateAccountResult;
import resources.data.DeleteAccountData;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

@Path("/deleteaccount")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DeleteAccountResource {

  private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
  private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
  private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
  private static final String message = "Account deleted successfully";

  public DeleteAccountResource() {
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteAccount(DeleteAccountData data) {
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

      String expectedRole = sessionEntity.getString("role");
      if (!VerifyToken.isAllowedRole(expectedRole, new String[] { "ADMIN" })) {
        return Response.ok(new ErrorResponse(ErrorCodes.TOKEN_EXPIRED, ErrorCodes.MSG_TOKEN_EXPIRED)).build();
      }

      String username = data.username.trim().toLowerCase();
      Key userKey = userKeyFactory.newKey(username);
      Entity userEntity = datastore.get(userKey);

      if (userEntity == null) {
        return Response.ok(new ErrorResponse(ErrorCodes.USER_NOT_FOUND, ErrorCodes.MSG_USER_NOT_FOUND)).build();
      }

      datastore.delete(userKey);
      return Response.ok(new SuccessResponse(message)).build();

    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error in deleteAccount", e);
      return Response.ok(new ErrorResponse(ErrorCodes.FORBIDDEN, ErrorCodes.MSG_FORBIDDEN)).build();

    }
  }
}
