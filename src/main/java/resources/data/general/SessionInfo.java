package resources.data.general;

public class SessionInfo {
  public String tokenId;
  public String username;
  public String role;
  public long ExpiresAt;

  public SessionInfo(String tokenId, String username, String role, long expiresAt) {
    this.tokenId = tokenId;
    this.username = username;
    this.role = role;
    this.ExpiresAt = expiresAt;
  }

}
