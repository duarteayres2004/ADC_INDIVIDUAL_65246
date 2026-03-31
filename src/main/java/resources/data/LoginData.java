package resources.data;

import resources.data.io.LoginInput;

public class LoginData {
    public LoginInput input;

    public LoginData() {
    }

    public boolean validLogin() {
        return input != null && input.validInput();
    }

}