package resources;

import resources.data.general.ErrorCodes;
import resources.data.general.ErrorResponse;
import resources.data.general.SuccessResponse;
import resources.data.io.CreateAccountResult;
import resources.data.CreateAccountData;

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

@Path("/createaccount")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CreateAccountResource {

	// private static final Logger LOG =
	// Logger.getLogger(LoginResource.class.getName());
	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

	public CreateAccountResource() {
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAccount(CreateAccountData data) {

		try {
			if (data == null || !data.validInput()) {
				return Response.ok(new ErrorResponse(ErrorCodes.INVALID_INPUT, ErrorCodes.MSG_INVALID_INPUT)).build();
			}

			String username = data.input.username.trim().toLowerCase();
			String role = data.input.role.trim().toUpperCase();

			if (!role.equals("ADMIN") && !role.equals("BOFFICER") && !role.equals("USER")) {
				return Response.ok(new ErrorResponse(ErrorCodes.INVALID_INPUT, ErrorCodes.MSG_INVALID_INPUT)).build();
			}

			Key userKey = userKeyFactory.newKey(username);
			Entity userEntity = datastore.get(userKey);

			if (userEntity != null) {
				return Response.ok(new ErrorResponse(ErrorCodes.USER_ALREADY_EXISTS, ErrorCodes.MSG_USER_ALREADY_EXISTS))
						.build();
			}

			Entity newUser = Entity.newBuilder(userKey)
					.set("username", username)
					.set("password", data.input.password)
					.set("phone", data.input.phone != null ? data.input.phone.trim() : "")
					.set("address", data.input.address != null ? data.input.address.trim() : "")
					.set("role", role)
					.build();

			datastore.put(newUser);

			return Response.ok(new SuccessResponse(new CreateAccountResult(username, role))).build();

		} catch (Exception e) {
			return Response.ok(new ErrorResponse(ErrorCodes.INTERNAL_ERROR, ErrorCodes.MSG_INTERNAL_ERROR)).build();

		}

	}

}