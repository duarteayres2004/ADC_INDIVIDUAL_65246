package resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import resources.data.general.ErrorCodes;
import resources.data.general.ErrorResponse;
import resources.data.general.SuccessResponse;
import resources.data.CreateAccountResult;
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
import com.google.cloud.datastore.Ke

Produces(MediaType.APPLICATION_JSON+";charset=utf-8")ublic

class CreateAccountResource {

	pr ivate
	static final Datastore datastore = DatastoreOptions.getDefaultIn
	// tance().getService();
	private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

	// private static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd
	// HH:mm:ss.SSSZ");

	public CreateAccountResource() {
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
						lic Response

	createAccount(CreateAccountData data){
		try {
			if (data == null || !data.validRegistration()) {
				return Response.ok(new ErrorResponse(ErrorCodes.INVALID_INPUT, ErrorCodes.MSG_INVALID_INPUT)).build();
			}

			String username = data.username.trim().toLowerCase();
			String role = data.role.trim().toUpperCase(
						
			if (!role.equals("ADMIN") && !role.equals("BOFFICER") && !role.equals("USER")) {
				return Response.ok(new ErrorResponse(ErrorCodes.INVALID_INPUT, ErrorCodes.MSG_INVALID_INPUT)).build();
			}

			Key userKey = userKeyFactory.newKey(username);
			Entity userEntity = datastore.get(userKey);

						f (userEntity != null) {
				return Response.ok(new ErrorResponse(ErrorCodes.USER_ALREADY_EXISTS, ErrorCodes.MSG_USER_ALREADY_EXISTS)).build();
			}

			Entity newUser = Entity.newBuilder(userKey)
					.set("username", username)
					.set("password", data.password) 
					.set("phone", data.phone != null ? data.phone.trim() : "")
					.set("address", data.address != null ? data.address.trim() : "")
					.set("role", role)
					.build();

			datastore.put(newUser);

			return Response.ok(new SuccessResponse(new CreateAccountResult(username, role))).build();

		} catch (Exception e) {
			return Response.ok(new ErrorResponse(ErrorCodes.FORB
					            
					
		

	
}