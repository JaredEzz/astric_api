import astric.model.dao.FollowingDAO;
import astric.model.domain.User;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.response.follow.FollowingResponse;
import astric.server.dao.FollowingDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static astric.server.dao.UserDAOImpl.hardCodedUsers;

class GetFollowersTest {

    @Test
    void getFollowersTest(){
        FollowingDAO followingDAO = new FollowingDAOImpl();

        int limitSize = 2;
        FollowingRequest followingRequest =
                new FollowingRequest(hardCodedUsers.get(0).getUsername(), limitSize, null, "ae04c02a-bc73-4b58-984d-e5038c6f7c02");


        FollowingResponse followingResponse = followingDAO.getFollowing(followingRequest);
        assertNotNull(followingResponse);
        assertEquals(followingResponse.getFollowees().size(), limitSize);
    }

}
