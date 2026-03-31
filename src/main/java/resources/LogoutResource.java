package resources;

import resources.data.general.*;
import resources.data.LogoutData;

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

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {

  private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
  private static final KeyFactory sessionKeyFactory = datastore.newKeyFactory().setKind("Session");

  public LogoutResource() {
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response doLogout(LogoutData data) {
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

      String callerUsername = sessionEntity.getString("username");
      String realUsername = data.input.username.trim().toLowerCase();
      String callerRole = sessionEntity.getString("role");
      boolean allowed = false;

      if (callerRole.equals("ADMIN")) {
        allowed = true;
      } else if (callerUsername.equals(realUsername)) {
        allowed = true;
      }

      if (!allowed) {
        return Response.ok(new ErrorResponse(ErrorCodes.UNAUTHORIZED, ErrorCodes.MSG_UNAUTHORIZED)).build();
      }

      Key sessionKey = sessionKeyFactory.newKey(data.token.tokenId);
      datastore.delete(sessionKey);

      return Response.ok(new SuccessResponse(new MessageResult("Logout successful"))).build();
    } catch (Exception e) {
      return Response.ok(new ErrorResponse(ErrorCodes.INTERNAL_ERROR, ErrorCodes.MSG_INTERNAL_ERROR)).build();
    }

  }
}
