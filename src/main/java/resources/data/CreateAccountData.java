package resources.data;

import resources.data.io.CreateAccountInput;

public class CreateAccountData {

  public CreateAccountInput input;

  public CreateAccountData() {
  }

  public boolean validInput() {
    return input != null && input.validInput();
  }

}
