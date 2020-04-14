package astric.server.service;

import astric.model.dao.PostDAO;
import astric.model.service.PostService;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.request.post.StoryRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;
import astric.model.service.response.post.StoryResponse;
import astric.server.dao.PostDAOImpl;

public class PostServiceImpl implements PostService {
    @Override
    public MakePostResponse makePost(MakePostRequest request) {
        PostDAO dao = new PostDAOImpl();
        return dao.makePost(request);
    }

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        PostDAO dao = new PostDAOImpl();
        return dao.getFeed(request);
    }

    @Override
    public StoryResponse getStory(StoryRequest request) {
        PostDAO dao = new PostDAOImpl();
        return dao.getStory(request);
    }

    @Override
    public MakePostResponse enqueuePost(MakePostRequest request) {
        PostDAO dao = new PostDAOImpl();
        return dao.enqueuePost(request);
    }


}
