package astric.server.lambda.follow;

import astric.model.service.FollowService;
import astric.model.service.request.follow.FollowRequest;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.response.follow.FollowResponse;
import astric.model.service.response.follow.FollowersResponse;
import astric.server.service.FollowServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class FollowHandler {

    public FollowResponse handleRequest(FollowRequest request, Context context) {
        FollowService service = new FollowServiceImpl();
        return service.doFollow(request);
    }
}
