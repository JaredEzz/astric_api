package astric.server.lambda.post;

import astric.model.service.PostService;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.response.post.FeedResponse;
import astric.server.service.PostServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class GetFeedHandler {

    public FeedResponse handleRequest(FeedRequest request, Context context) {
        PostService service = new PostServiceImpl();
        return service.getFeed(request);
    }
}
