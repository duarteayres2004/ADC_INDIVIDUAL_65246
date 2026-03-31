package resources.data;

import auth.AuthToken;
import resources.data.io.ChangePassInput;

public class ChangePassData {

  public ChangePassInput input;
  public AuthToken token;

  public ChangePassData() {
  }

  public boolean validInput() {
    return input != null && input.validInput() && token != null;
  }

}
