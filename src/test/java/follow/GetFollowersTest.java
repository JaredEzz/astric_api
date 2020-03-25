package follow;

import astric.model.dao.FollowingDAO;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.response.follow.FollowersResponse;
import astric.server.dao.FollowingDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static astric.server.dao.UserDAOImpl.hardCodedUsers;

class GetFollowersTest {

    @Test
    void getFollowersTest(){
        FollowingDAO followingDAO = new FollowingDAOImpl();

        int limitSize = 2;
        FollowersRequest followersRequest =
                new FollowersRequest(hardCodedUsers.get(0).getUsername(), limitSize, null, "ae04c02a-bc73-4b58-984d-e5038c6f7c02");

        FollowersResponse followersResponse = followingDAO.getFollowers(followersRequest);
        assertNotNull(followersResponse);
        assertEquals(followersResponse.getFollowers().size(), limitSize);
    }

}
