package astric.model.dao;

import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;

public interface PostDAO {
    MakePostResponse makePost(MakePostRequest request);

    FeedResponse getFeed(FeedRequest request);
}
