package resources.data;

public class LoginData {
    public LoginInput input;

    public LoginData() {
    }

    public boolean validLogin() {
        return input != null && input.validInput();
    }

}