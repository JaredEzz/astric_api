package astric.server.service;

import astric.model.dao.PostDAO;
import astric.model.dao.StoryDAO;
import astric.model.domain.Post;
import astric.model.service.PostService;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.request.post.StoryRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;
import astric.model.service.response.post.StoryResponse;
import astric.server.dao.PostDAOImpl;
import astric.server.dao.StoryDAOImpl;

import java.util.List;

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
        StoryDAO dao = new StoryDAOImpl();
        return dao.getStory(request);
    }

    @Override
    public MakePostResponse enqueuePost(MakePostRequest request) {
        PostDAO postDAO = new PostDAOImpl();
        StoryDAO storyDAO = new StoryDAOImpl();
        MakePostResponse storyResponse = storyDAO.writePostToStory(request);
        MakePostResponse postResponse = postDAO.enqueuePost(request);
        return new MakePostResponse(
                storyResponse.isSuccess() && postResponse.isSuccess(),
                storyResponse.getMessage() + " " + postResponse.getMessage()
        );
    }

    @Override
    public void updateFeeds(List<String> followerUsernames, Post post) {
        PostDAO postDAO = new PostDAOImpl();
        System.out.println("PostServiceImpl starting batch write");
        postDAO.batchWritePostToFeedTable(post, followerUsernames);
    }


}
