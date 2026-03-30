package resources.data;

import java.auth.AuthToken;

public class LoginResult {
    public AuthToken token;

    public LoginResult(AuthToken token) {
        this.token = token;
    }
}