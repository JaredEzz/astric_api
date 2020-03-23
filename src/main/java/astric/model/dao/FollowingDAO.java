package astric.model.dao;

import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.response.follow.FollowingResponse;

public interface FollowingDAO {


    FollowingResponse getFollowing(FollowingRequest request);
}
