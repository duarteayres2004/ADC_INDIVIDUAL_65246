package resources.data;

public class LoginData {
    public String username;
    public String password;

    public LoginData() {
    }

    public boolean validLogin() {
        return username != null && !username.isBlank() &&
                password != null && !password.isBlank();
    }

}