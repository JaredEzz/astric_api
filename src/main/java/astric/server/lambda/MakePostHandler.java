package astric.server.lambda;


import astric.model.service.PostService;
import astric.model.service.post.MakePostRequest;
import astric.model.service.response.post.MakePostResponse;
import astric.server.service.PostServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class MakePostHandler {
    public MakePostResponse handleRequest(MakePostRequest request, Context context) {
        PostService service = new PostServiceImpl();
        return service.makePost(request);
    }
}
