package resources.data.io;

public class ModAccountAttributes {
    public String phone;
    public String address;

    public ModAccountAttributes() {
    }

    public boolean hasAtLeastOneField() {
        return (phone != null && !phone.isBlank()) ||
                (address != null && !address.isBlank());
    }

}