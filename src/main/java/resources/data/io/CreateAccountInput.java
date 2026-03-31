package resources.data.io;

public class CreateAccountInput {
    public String username;
    public String password;
    public String confirmation;
    public String phone;
    public String address;
    public String role;

    public CreateAccountInput() {
    }

    public boolean validInput() {
        return username != null && !username.isBlank() &&
                password != null && !password.isBlank() &&
                confirmation != null && password.equals(confirmation) &&
                role != null && !role.isBlank();
    }

}