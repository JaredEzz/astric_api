package astric.model.service.response.account;

import astric.model.service.request.account.LoginRequest;
import astric.model.service.response.Response;

/**
 * A response for a {@link LoginRequest}.
 */
public class LoginResponse extends Response {
    public String getAuthToken() {
        return authToken;
    }

    private final String authToken;

    public LoginResponse(boolean success, String authToken) {
        super(success);
        this.authToken = authToken;
    }

    public LoginResponse(boolean success, String message, String authToken) {
        super(success, message);
        this.authToken = authToken;
    }
}
