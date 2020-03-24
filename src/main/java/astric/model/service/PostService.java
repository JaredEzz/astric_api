package astric.model.service;

import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;

/**
 * Defines the interface for the 'account' service.
 */
public interface PostService {
    /**
     * Returns success if the post was made.
     *
     * @param request contains the data required to fulfill the request.
     * @return on success, true, else error.
     */
    MakePostResponse makePost(MakePostRequest request);

    /**
     * Returns the posts for the user specified in the request. Uses information in
     * the request object to limit the number of posts returned and to return the next set of
     * posts after any that were returned in a previous request.
     *
     * @param request contains the data required to fulfill the request.
     * @return the posts.
     */
    FeedResponse getFeed(FeedRequest request);
}
