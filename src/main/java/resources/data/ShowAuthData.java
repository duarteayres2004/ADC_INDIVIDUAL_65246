package resources.data;

import auth.AuthToken;

public class ShowAuthData {

  public Object input;

  public AuthToken token;

  public ShowAuthData() {
  }

  public boolean validInput() {
    return input != null && token != null;
  }
}
