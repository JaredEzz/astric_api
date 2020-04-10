package astric.server.lambda.follow;

import astric.model.service.FollowService;
import astric.model.service.request.follow.IsFollowingRequest;
import astric.model.service.response.follow.IsFollowingResponse;
import astric.server.service.FollowServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static astric.server.lambda.AuthenticationHandler.authenticateRequest;

public class IsFollowingHandler implements RequestHandler<IsFollowingRequest, IsFollowingResponse> {

    public IsFollowingResponse handleRequest(IsFollowingRequest request, Context context) {
        FollowService service = new FollowServiceImpl();
        authenticateRequest(request.getAuthToken());
        try {
            return service.isFollowing(request);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Something went wrong on our end. Please try again.");
        }
    }
}
