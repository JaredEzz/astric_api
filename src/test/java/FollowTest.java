import astric.model.dao.FollowingDAO;
import astric.model.service.request.follow.FollowRequest;
import astric.model.service.response.follow.FollowResponse;
import astric.server.dao.FollowingDAOImpl;
import org.junit.jupiter.api.Test;

import static astric.server.dao.UserDAOImpl.hardCodedUsers;
import static org.junit.jupiter.api.Assertions.*;

class FollowTest {

    @Test
    void getFollowersTest(){
        FollowingDAO followingDAO = new FollowingDAOImpl();

        FollowRequest followRequest =
                new FollowRequest(true, "jaredhasson", "wendyw", "ae04c02a-bc73-4b58-984d-e5038c6f7c02");


        FollowResponse followResponse = followingDAO.doFollow(followRequest);
        assertNotNull(followResponse);
        assertTrue(followResponse.isSuccess());
    }

}
