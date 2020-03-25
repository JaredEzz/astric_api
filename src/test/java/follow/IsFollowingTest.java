package follow;

import astric.model.dao.FollowingDAO;
import astric.model.service.request.follow.FollowRequest;
import astric.model.service.request.follow.IsFollowingRequest;
import astric.model.service.response.follow.FollowResponse;
import astric.model.service.response.follow.IsFollowingResponse;
import astric.server.dao.FollowingDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsFollowingTest {

    @Test
    void isFollowingTest(){
        FollowingDAO followingDAO = new FollowingDAOImpl();

        IsFollowingRequest isFollowingRequest =
                new IsFollowingRequest("jaredhasson", "wendyw", "ae04c02a-bc73-4b58-984d-e5038c6f7c02");


        IsFollowingResponse isFollowingResponse = followingDAO.isFollowing(isFollowingRequest);
        assertNotNull(isFollowingResponse);
        assertTrue(isFollowingResponse.isSuccess());
        assertTrue(isFollowingResponse.isFollowing());
    }

}
