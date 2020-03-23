package astric.model.service.response.account;

import astric.model.service.request.account.LoginRequest;
import astric.model.service.response.Response;

/**
 * A response for a {@link LoginRequest}.
 */
public class LoginResponse extends Response {
    public LoginResponse(boolean success) {
        super(success);
    }

    public LoginResponse(boolean success, String message) {
        super(success, message);
    }
}
