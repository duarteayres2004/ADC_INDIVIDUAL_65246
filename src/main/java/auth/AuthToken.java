package auth;

public class AuthToken {
    public String tokenId;
    public String username;
    public String role;
    public long issuedAt;
    public long expiresAt;

    public AuthToken() {
    }

    public AuthToken(String tokenId, String username, String role, long issuedAt, long expiresAt) {
        this.tokenId = tokenId;
        this.username = username;
        this.role = role;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }
}