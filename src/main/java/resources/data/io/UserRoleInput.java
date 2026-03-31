package resources.data.io;

public class UserRoleInput {

  public String username;

  public UserRoleInput() {
  }

  public boolean validInput() {
    return username != null && !username.isBlank();
  }

}
