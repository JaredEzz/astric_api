package astric.server.lambda.follow;

import astric.model.service.FollowService;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.response.follow.FollowingResponse;
import astric.server.service.FollowServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class GetFollowingHandler {

    public FollowingResponse handleRequest(FollowingRequest request, Context context) {
        FollowService service = new FollowServiceImpl();
        return service.getFollowing(request);
    }
}
