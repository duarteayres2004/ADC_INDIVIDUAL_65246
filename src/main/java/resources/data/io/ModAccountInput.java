package resources.data;

public class ModifyAccountInput {
    public String username;
    public ModifyAttributes attributes;

    public ModifyAccountInput() {}

    public boolean validInput() {
        return username != null && !username.isBlank()
            && attributes != null
            && attributes.hasAtLeastOneField();
    }
}