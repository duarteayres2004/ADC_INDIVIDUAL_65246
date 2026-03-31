package resources.data.io;

public class ChangeURoleInput {

    public String username;
    public String newRole;

    public ChangeURoleInput() {

    }

    public boolean validInput() {
        return username != null && !username.isBlank() &&
                newRole != null && !newRole.isBlank();
    }
}