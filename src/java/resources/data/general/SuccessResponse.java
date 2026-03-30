package resources.data.general;

public class SuccessResponse {

    public String status;
    public Object data;

    public SuccessResponse(Object data) {
        this.status = "success";
        this.data = data;
    }
}