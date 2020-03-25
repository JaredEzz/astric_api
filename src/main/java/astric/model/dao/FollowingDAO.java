package astric.model.dao;

import astric.model.service.request.follow.FollowRequest;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.request.follow.IsFollowingRequest;
import astric.model.service.response.follow.FollowResponse;
import astric.model.service.response.follow.FollowersResponse;
import astric.model.service.response.follow.FollowingResponse;
import astric.model.service.response.follow.IsFollowingResponse;

public interface FollowingDAO {


    FollowingResponse getFollowing(FollowingRequest request);

    FollowersResponse getFollowers(FollowersRequest request);

    FollowResponse doFollow(FollowRequest request);

    IsFollowingResponse isFollowing(IsFollowingRequest request);
}
