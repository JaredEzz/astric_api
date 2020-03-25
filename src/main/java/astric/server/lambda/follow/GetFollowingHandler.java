package astric.server.lambda.follow;

import astric.model.service.FollowService;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.response.follow.FollowingResponse;
import astric.server.service.FollowServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static astric.server.lambda.AuthenticationHandler.authenticateRequest;

public class GetFollowingHandler implements RequestHandler<FollowingRequest, FollowingResponse> {

    public FollowingResponse handleRequest(FollowingRequest request, Context context) {
        FollowService service = new FollowServiceImpl();
        authenticateRequest(request.getAuthToken());
        try {
            return service.getFollowing(request);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Something went wrong on our end. Please try again.");
        }
    }
}
