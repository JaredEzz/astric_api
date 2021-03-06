package astric.model.dao;

import astric.model.domain.Post;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.request.post.StoryRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;
import astric.model.service.response.post.StoryResponse;

import java.util.List;

public interface PostDAO {
    MakePostResponse makePost(MakePostRequest request);

    FeedResponse getFeed(FeedRequest request);

    StoryResponse getStory(StoryRequest request);

    MakePostResponse enqueuePost(MakePostRequest request);

    void batchWritePostToFeedTable(Post post, List<String> followerUsernames);
}
