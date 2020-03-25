package astric.server.lambda.post;


import astric.model.service.PostService;
import astric.model.service.request.post.StoryRequest;
import astric.model.service.response.post.StoryResponse;
import astric.server.service.PostServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class GetStoryHandler {

    public StoryResponse handleRequest(StoryRequest request, Context context) {
        PostService service = new PostServiceImpl();
        return service.getStory(request);
    }
}
