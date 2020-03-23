package astric.server.service;

import astric.model.service.FollowService;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.response.follow.FollowingResponse;
import astric.model.dao.FollowingDAO;
import astric.server.dao.FollowingDAOImpl;

public class FollowServiceImpl implements FollowService {

    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {
        FollowingDAO dao = new FollowingDAOImpl();
        return dao.getFollowing(request);
    }
}
