package astric.model.service.response.account;

import astric.model.service.request.account.LogoutRequest;
import astric.model.service.response.Response;

/**
 * A response for a {@link LogoutRequest}.
 */
public class LogoutResponse extends Response {
    public LogoutResponse(boolean success) {
        super(success);
    }

    public LogoutResponse(boolean success, String message) {
        super(success, message);
    }
}
