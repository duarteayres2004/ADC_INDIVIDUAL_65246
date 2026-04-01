package resources;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import resources.data.general.ErrorResponse;
import resources.data.general.SuccessResponse;
import resources.data.general.ErrorCodes;
import resources.data.general.VerifyToken;
import resources.data.io.ShowUsersResult;
import resources.data.ShowUsersData;
import resources.data.general.UserInfo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

@Path("/showusers")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ShowUsersResource {

  private static final Logger LOG = Logger.getLogger(ShowUsersResource.class.getName());
  private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

  public ShowUsersResource() {
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response showUsers(ShowUsersData data) {
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
      if (!VerifyToken.isAllowedRole(expectedRole, new String[] { "ADMIN", "BOFFICER" })) {
        return Response.ok(new ErrorResponse(ErrorCodes.UNAUTHORIZED, ErrorCodes.MSG_UNAUTHORIZED)).build();
      }

      Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").build();

      QueryResults<Entity> results = datastore.run(query);

      List<UserInfo> users = new ArrayList<>();
      while (results.hasNext()) {
        Entity userEntity = results.next();
        String username = userEntity.getString("username");
        String role = userEntity.getString("role");
        users.add(new UserInfo(username, role));
      }

      return Response.ok(new SuccessResponse(new ShowUsersResult(users))).build();

    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error showing users", e);
      return Response.ok(new ErrorResponse(ErrorCodes.INTERNAL_ERROR, ErrorCodes.MSG_INTERNAL_ERROR)).build();
    }
  }
}
