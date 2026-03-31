package resources.data;

import auth.AuthToken;
import resources.data.io.ChangeURoleInput;

public class ChangeURoleData {

  public ChangeURoleInput input;
  public AuthToken token;

  public ChangeURoleData() {
  }

  public boolean validInput() {
    return input != null && input.validInput() && token != null;
  }
}