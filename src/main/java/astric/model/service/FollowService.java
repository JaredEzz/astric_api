package astric.model.service;

import astric.model.service.request.follow.FollowRequest;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.response.follow.FollowResponse;
import astric.model.service.response.follow.FollowersResponse;
import astric.model.service.response.follow.FollowingResponse;

/**
 * Defines the interface for the 'following' service.
 */
public interface FollowService {
    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    FollowingResponse getFollowing(FollowingRequest request);

    FollowersResponse getFollowers(FollowersRequest request);

    FollowResponse doFollow(FollowRequest request);
}
