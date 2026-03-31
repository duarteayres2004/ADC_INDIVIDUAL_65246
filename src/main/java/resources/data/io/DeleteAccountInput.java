package resources.data.io;

public class DeleteAccountInput {
  public String username;

  public DeleteAccountInput() {
  }

  public boolean validInput() {
    return username != null && !username.isBlank();

  }

}
