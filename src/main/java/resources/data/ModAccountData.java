package resources.data;

import auth.AuthToken;
import resources.data.io.ModAccountInput;

public class ModAccountData {

    public ModAccountInput input;
    public AuthToken token;

    public ModAccountData() {
    }

    public boolean validInput() {
        return input != null && input.validInput() && token != null;
    }

}
