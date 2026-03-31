package resources.data.io;

public class ModAccountInput {
    public String username;
    public ModAccountAttributes attributes;

    public ModAccountInput() {
    }

    public boolean validInput() {
        return username != null && !username.isBlank()
                && attributes != null
                && attributes.hasAtLeastOneField();
    }
}