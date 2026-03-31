package resources.data.io;

import java.util.List;
import resources.data.general.UserInfo;

public class ShowUsersResult {
    public List<UserInfo> users;

    public ShowUsersResult(List<UserInfo> users) {
        this.users = users;
    }
}
