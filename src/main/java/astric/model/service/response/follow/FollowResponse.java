package astric.model.service.response.follow;

import astric.model.service.response.Response;

public class FollowResponse extends Response {
    public FollowResponse(boolean success) {
        super(success);
    }

    public FollowResponse(boolean success, String message) {
        super(success, message);
    }
}
