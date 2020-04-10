package astric.server.lambda.follow;

import astric.model.service.FollowService;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.response.follow.FollowersResponse;
import astric.server.service.FollowServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static astric.server.lambda.AuthenticationHandler.authenticateRequest;

public class GetFollowersHandler implements RequestHandler<FollowersRequest, FollowersResponse> {
    @Override
    public FollowersResponse handleRequest(FollowersRequest request, Context context) throws RuntimeException {
        FollowService service = new FollowServiceImpl();
        authenticateRequest(request.getAuthToken());
        try {
            return service.getFollowers(request);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Something went wrong on our end. Please try again.");
        }
    }
}
