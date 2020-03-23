package astric.model.service.response.account;

import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.Response;

/**
 * A response for a {@link SignUpRequest}.
 */
public class SignUpResponse extends Response {
    public SignUpResponse(boolean success) {
        super(success);
    }

    public SignUpResponse(boolean success, String message) {
        super(success, message);
    }
}
