package java.auth;

public class AuthToken {
    public String tokenId;
    public String userId;
    public String role;
    public long issuedAt;
    public long expiresAt;

    public AuthToken() {
    }

    public AuthToken(String tokenId, String userId, String role, long issuedAt, long expiresAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.role = role;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }
}