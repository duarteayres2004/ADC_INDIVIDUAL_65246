package resources;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

import resources.data.general.ErrorResponse;
import resources.data.general.SuccessResponse;
import resources.data.general.ErrorCodes;
import resources.data.general.VerifyToken;
import resources.data.io.ShowAuthResult;
import resources.data.ShowAuthData;
import resources.data.general.SessionInfo;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Consumes;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

@Path("/showauthsessions")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ShowAuthResource {

  private static final Logger LOG = Logger.getLogger(ShowAuthResource.class.getName());
  private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

  public ShowAuthResource() {
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response showAuth(ShowAuthData data) {

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

      Query<Entity> query = Query.newEntityQueryBuilder().setKind("Session").build();
      QueryResults<Entity> sessions = datastore.run(query);

      List<SessionInfo> result = new ArrayList<>();
      while (sessions.hasNext()) {
        Entity session = sessions.next();
        SessionInfo sessionInfo = new SessionInfo(session.getString("tokenId"), session.getString("username"),
            session.getString("role"),
            session.getLong("expiresAt"));

        result.add(sessionInfo);
      }

      return Response.ok(new SuccessResponse(new ShowAuthResult(result))).build();

    } catch (Exception e) {
      return Response.ok(new ErrorResponse(ErrorCodes.INTERNAL_ERROR, ErrorCodes.MSG_INTERNAL_ERROR)).build();
    }
  }
}
