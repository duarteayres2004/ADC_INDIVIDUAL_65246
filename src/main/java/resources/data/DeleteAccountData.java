package resources.data;

import auth.AuthToken;
import resources.data.io.DeleteAccountInput;

public class DeleteAccountData {

    public DeleteAccountInput input;
    public AuthToken token;

    public DeleteAccountData() {
    }

    public boolean validInput() {
        return input != null && input.validInput() && token != null;
    }
}
