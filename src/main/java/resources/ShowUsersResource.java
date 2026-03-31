package resources;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import resources.data.general.ErrorResponse;
import resources.data.general.SuccessResponse;
import resources.data.general.ErrorCodes;
import resources.data.general.VerifyToken;
import resources.data.ShowUsersResult;
import resources.data.ShowUsersData;

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
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

public class ShowUsersResource {

  private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
  private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
  private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

}

  public ShowUsersResource() {
  }

  @POST
  @Path("/showusers")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
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
        return Response.ok(new ErrorResponse(ErrorCodes.INVALID_TOKEN, ErrorCodes.MSG_INVALID_TOKEN)).build();
      }

      Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").build();
      QueryResults<Entity> results = datastore.run(query);

      List<ShowUsersResult> users = new java.util.ArrayList<>();
      while (results.hasNext()) {
        Entity userEntity = results.next();
        String username = userEntity.getKey().getName();
        String role = userEntity.getString("role");

        users.add(new ShowUsersResult(username, role));
      }

      return Response.ok(users).build();

    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error showing users", e);
      return Response.ok(new ErrorResponse(ErrorCodes.SERVER_ERROR, ErrorCodes.MSG_SERVER_ERROR)).build();
    }
  }
}