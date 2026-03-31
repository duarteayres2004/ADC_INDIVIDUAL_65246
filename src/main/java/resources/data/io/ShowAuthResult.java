package resources.data;

import java.util.List;
import resources.data.general.SessionInfo;

public class ShowAuthResult {

  public List<SessionInfo> sessions;

  public ShowAuthResult(List<SessionInfo> sessions) {
    this.sessions = sessions;
  }
}
