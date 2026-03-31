package resources.data;

import auth.AuthToken;
import resources.data.io.UserRoleInput;

public class UserRoleData {

  public AuthToken token;
  public UserRoleInput input;

  public UserRoleData() {
  }

  public boolean validInput() {
    return token != null && input != null && input.validInput();
  }
}
