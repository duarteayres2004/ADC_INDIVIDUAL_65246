package resources;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

import auth.AuthToken;

import resources.data.general.*;
import resources.data.LoginData;
import resources.data.LoginResult;

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

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

    public static final long TOKEN_EXPIRATION_MS = 15 * 60 * 1000;

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

    public LoginResource() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doLogin(LoginData data) {
        try {

            LOG.fine("Login attempt by user: " + data.username);

            if (data == null || !data.validLogin()) {
                return Response.ok(new ErrorResponse(ErrorCodes.INVALID_INPUT, ErrorCodes.MSG_INVALID_INPUT)).build();
            }

            String username = data.username.trim().toLowerCase();

            Key userKey = userKeyFactory.newKey(username);
            Entity userEntity = datastore.get(userKey);

            if (userEntity == null) {
                return Response.ok(new ErrorResponse(ErrorCodes.USER_NOT_FOUND, ErrorCodes.MSG_USER_NOT_FOUND)).build();
            }

            String realPassword = userEntity.getString("password");
            if (realPassword == null || !realPassword.equals(data.password)) {
                return Response
                        .ok(new ErrorResponse(ErrorCodes.INVALID_CREDENTIALS, ErrorCodes.MSG_INVALID_CREDENTIALS))
                        .build();
            }

            String role = userEntity.getString("role");

            long issuedAt = System.currentTimeMillis();
            long expiresAt = issuedAt + TOKEN_EXPIRATION_MS;
            String tokenId = UUID.randomUUID().toString();

            AuthToken token = new AuthToken(tokenId, username, role, issuedAt, expiresAt);
            Entity SessionEntity = Entity.newBuilder(datastore.newKeyFactory().setKind("Session").newKey(tokenId))
                    .set("username", username)
                    .set("role", role)
                    .set("issuedAt", issuedAt)
                    .set("expiresAt", expiresAt)
                    .set("tokenId", tokenId)
                    .build();

            datastore.put(SessionEntity);

            return Response.ok(new SuccessResponse(new LoginResult(token))).build();

        } catch (Exception e) {
            return Response.ok(new ErrorResponse("500", "An unexpected error occurred.")).build();
        }
    }

}