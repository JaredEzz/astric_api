package astric.server.lambda.follow;

import astric.model.service.FollowService;
import astric.model.service.request.follow.FollowRequest;
import astric.model.service.request.follow.IsFollowingRequest;
import astric.model.service.response.follow.FollowResponse;
import astric.model.service.response.follow.IsFollowingResponse;
import astric.server.service.FollowServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class IsFollowingHandler {

    public IsFollowingResponse handleRequest(IsFollowingRequest request, Context context) {
        FollowService service = new FollowServiceImpl();
        return service.isFollowing(request);
    }
}
