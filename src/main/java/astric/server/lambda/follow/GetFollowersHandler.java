package astric.server.lambda.follow;

import astric.model.service.FollowService;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.response.follow.FollowersResponse;
import astric.server.service.FollowServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class GetFollowersHandler {

    public FollowersResponse handleRequest(FollowersRequest request, Context context) {
        FollowService service = new FollowServiceImpl();
        return service.getFollowers(request);
    }
}
