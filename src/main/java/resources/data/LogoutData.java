package resources.data;

import auth.AuthToken;
import resources.data.io.LogoutInput;

public class LogoutData {

  public LogoutInput input;
  public AuthToken token;

  public LogoutData() {
  }

  public boolean validInput() {
    return input != null && input.validInput() && token != null;

  }

}
