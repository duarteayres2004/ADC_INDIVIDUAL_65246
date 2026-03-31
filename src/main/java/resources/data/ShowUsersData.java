package resources.data;

import auth.AuthToken;

public class ShowUsersData {

    public Object input;
    public AuthToken token;

    public ShowUsersData() {
    }

    public boolean validInput() {
        return token != null;
    }

}
