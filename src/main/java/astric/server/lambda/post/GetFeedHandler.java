package astric.server.lambda.post;

import astric.model.service.PostService;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.response.post.FeedResponse;
import astric.server.service.PostServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static astric.server.lambda.AuthenticationHandler.authenticateRequest;

public class GetFeedHandler implements RequestHandler<FeedRequest, FeedResponse> {

    public FeedResponse handleRequest(FeedRequest request, Context context) {
        PostService service = new PostServiceImpl();
        authenticateRequest(request.getAuthToken());
        try {
            return service.getFeed(request);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Something went wrong on our end. Please try again.");
        }
    }
}
