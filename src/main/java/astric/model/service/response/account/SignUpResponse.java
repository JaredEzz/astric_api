package astric.model.service.response.account;

import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.Response;

/**
 * A response for a {@link SignUpRequest}.
 */
public class SignUpResponse extends Response {
    public String getAuthToken() {
        return authToken;
    }

    private String authToken;

    public SignUpResponse(boolean success, String authToken) {
        super(success);
        this.authToken = authToken;
    }

    public SignUpResponse(boolean success, String authToken, String message) {
        super(success, message);
        this.authToken = authToken;
    }
}
