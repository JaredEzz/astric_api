package post;

import astric.model.dao.PostDAO;
import astric.model.service.request.post.StoryRequest;
import astric.model.service.response.post.StoryResponse;
import astric.server.dao.PostDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetStoryTest {
    @Test
    void getStoryTest(){
        PostDAO postDAO = new PostDAOImpl();

        StoryRequest storyRequest = new StoryRequest(10, "jaredhasson", null, "ae04c02a-bc73-4b58-984d-e5038c6f7c02");

        StoryResponse storyResponse = postDAO.getStory(storyRequest);

        assertNotNull(storyResponse);
        assertEquals(2, storyResponse.getPosts().size());
    }
}
