package astric.model.service.response.post;

import astric.model.service.response.Response;

public class MakePostResponse extends Response {
    public MakePostResponse(boolean success) {
        super(success);
    }

    public MakePostResponse(boolean success, String message) {
        super(success, message);
    }
}
