package post;

import astric.model.dao.PostDAO;
import astric.model.domain.Post;
import astric.model.domain.User;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.response.post.MakePostResponse;
import astric.server.dao.PostDAOImpl;
import org.junit.jupiter.api.Test;

import static astric.server.dao.UserDAOImpl.hardCodedUsers;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MakePostTest {
    @Test
    void testMakePost(){
        User originatingUser = hardCodedUsers.get(0);
        Post newPost = new Post(originatingUser, "20120920", "This is a post");
        MakePostRequest makePostRequest = new MakePostRequest(newPost, "ae04c02a-bc73-4b58-984d-e5038c6f7c02");

        PostDAO postDAO = new PostDAOImpl();
        MakePostResponse makePostResponse = postDAO.makePost(makePostRequest);

        assertNotNull(makePostResponse);
        assertTrue(makePostResponse.isSuccess());
    }
}
