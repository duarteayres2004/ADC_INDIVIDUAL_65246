package resources.data.io;

public class LoginInput {

    public String username;
    public String password;

    public LoginInput() {
    }

    public boolean validInput() {
        return username != null && !username.isBlank() &&
                password != null && !password.isBlank();
    }
}
