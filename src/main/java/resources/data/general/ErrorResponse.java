
package resources.data.general;

public class ErrorResponse {

    public String status;
    public String data;

    public ErrorResponse() {
    }

    public ErrorResponse(String status, String data) {
        this.status = status;
        this.data = data;
    }
}