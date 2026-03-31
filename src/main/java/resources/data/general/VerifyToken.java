package resources.data.general;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import auth.AuthToken;

public class VerifyToken {

    private static final Datastore datastore = com.google.cloud.datastore.DatastoreOptions.getDefaultInstance()
            .getService();
    private static final KeyFactory sessionKeyFactory = datastore.newKeyFactory().setKind("Session");

    public static Entity getValidSession(AuthToken token) {
        if (token == null || token.tokenId == null || token.username == null || token.role == null) {
            return null;
        }

        Key sessionKey = sessionKeyFactory.newKey(token.tokenId);
        Entity sessionEntity = datastore.get(sessionKey);

        if (sessionEntity == null) {
            return null;
        }

        String realUsername = sessionEntity.getString("username");
        String realRole = sessionEntity.getString("role");

        if (!realUsername.equals(token.username) || !realRole.equals(token.role)) {
            return null;
        }

        return sessionEntity;
    }

    public static boolean isAllowedRole(String role, String[] allowedRoles) {
        if (role == null) {
            return false;
        }
        for (String allowed : allowedRoles) {
            if (role.equals(allowed)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTokenExpired(Entity sessionEntity) {
        long expiresAt = sessionEntity.getLong("expiresAt");
        return System.currentTimeMillis() > expiresAt;
    }
}