package resources.data;

public class CreateAccountData {
    public String username;
    public String password;
    public String confirmation;
    public String phone;
    public String address;
    public String role;

    public CreateAccountData() {
    }

    public boolean validRegistration() {
        return username != null && !username.isBlank() &&
                password != null && !password.isBlank() &&
                confirmation != null && password.equals(confirmation) &&
                role != null && !role.isBlank();
    }

}