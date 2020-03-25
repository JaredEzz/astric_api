package astric.server.service;

import astric.model.service.FollowService;
import astric.model.service.request.follow.FollowRequest;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.request.follow.IsFollowingRequest;
import astric.model.service.response.follow.FollowResponse;
import astric.model.service.response.follow.FollowersResponse;
import astric.model.service.response.follow.FollowingResponse;
import astric.model.dao.FollowingDAO;
import astric.model.service.response.follow.IsFollowingResponse;
import astric.server.dao.FollowingDAOImpl;

public class FollowServiceImpl implements FollowService {

    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {
        FollowingDAO dao = new FollowingDAOImpl();
        return dao.getFollowing(request);
    }

    @Override
    public FollowersResponse getFollowers(FollowersRequest request) {
        FollowingDAO dao = new FollowingDAOImpl();
        return dao.getFollowers(request);
    }

    @Override
    public FollowResponse doFollow(FollowRequest request) {
        FollowingDAO dao = new FollowingDAOImpl();
        return dao.doFollow(request);
    }

    @Override
    public IsFollowingResponse isFollowing(IsFollowingRequest request) {
        FollowingDAO dao = new FollowingDAOImpl();
        return dao.isFollowing(request);
    }
}
