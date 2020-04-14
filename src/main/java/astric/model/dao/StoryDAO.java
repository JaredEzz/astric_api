package astric.model.dao;

import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.request.post.StoryRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;
import astric.model.service.response.post.StoryResponse;

public interface StoryDAO {
    MakePostResponse writePostToStory(MakePostRequest request);
    StoryResponse getStory(StoryRequest request);
}
