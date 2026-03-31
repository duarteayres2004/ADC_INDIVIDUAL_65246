package resources.data.io;

public class LogoutInput {

  public String username;

  public LogoutInput() {
  }

  public boolean validInput() {
    return username != null && !username.isBlank();
  }

}
