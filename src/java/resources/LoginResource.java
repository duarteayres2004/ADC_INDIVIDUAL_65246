
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

import resources.data.general.*;
import resources.data.LoginData;

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




@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {
	

    public static final long TOKEN_EXPIRATION_MS = 3600000 * 2;
    
	private static final Logger LOG = Logger.getLogger(CreateAccountResource.class.getName()); 
	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory userKeyFactory = datastore.getKeyFactory().setKind("User");
	
	public LoginResource() {}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
        try{

		LOG.fine("Login attempt by user: " + data.username);
		
        if(data == null || !data.validLogin()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ErrorResponse(ErrorCodes.INVALID_INPUT, ErrorCodes.MSG_INVALID_INPUT)).build();
        }

        username = data.username.trim().toLowerCase();

        Key userKey = userKeyFactory.newKey(username);
		Entity userEntity = datastore.get(userKey);

        if (userEntity == null) {
            return Response.status(Status.FORBIDDEN)
                    .entity(new ErrorResponse(ErrorCodes.USER_NOT_FOUND, ErrorCodes.MSG_USER_NOT_FOUND)).build();
        }

        String realPassword = userEntity.getString("password");
		if(realPassword == null || !realPassword.equals(data.password)) {
            return Response.status(Status.FORBIDDEN)
                    .entity(new ErrorResponse(ErrorCodes.INVALID_CREDENTIALS, ErrorCodes.MSG_INVALID_CREDENTIALS)).build();
        }

        String username = userEntity.getKey().getName();
        String role = userEntity.getString("role");


        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + TOKEN_EXPIRATION_MS;
        String tokenId = UUID.randomUUID().toString();
		
		
		AuthToken token = new AuthToken(tokenId, username, role, issuedAt, expiresAt);
        Entity SessionEntity = Entity.newBuilder(datastore.newKeyFactory().setKind("Session").newKey(tokenId))
                .set("userId", username)
                .set("role", role)
                .set("issuedAt", issuedAt)
                .set("expiresAt", expiresAt)
                .build();
        
        datastore.put(SessionEntity);

        return Response.ok(new SuccessResponse(new LoginResult(token))).build();

        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("500", "An unexpected error occurred.")).build();
        }
    }
    
}