package resources.data.io;

public class ChangePassInput {

  public String username;
  public String oldPassword;
  public String newPassword;

  public ChangePassInput() {
  }

  public boolean validInput() {
    return username != null && !username.isBlank() &&
        oldPassword != null && !oldPassword.isBlank() &&
        newPassword != null && !newPassword.isBlank();
  }
}
