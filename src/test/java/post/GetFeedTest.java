package post;

import astric.model.dao.PostDAO;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.response.post.FeedResponse;
import astric.server.dao.PostDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetFeedTest {
    @Test
    void testGetFeed(){
        int limitSize = 5;
        FeedRequest feedRequest = new FeedRequest(limitSize, "jaredhasson", null, "ae04c02a-bc73-4b58-984d-e5038c6f7c02");

        PostDAO postDAO = new PostDAOImpl();
        FeedResponse feedResponse = postDAO.getFeed(feedRequest);

        assertNotNull(feedResponse);
        assertTrue(feedResponse.isSuccess());
        assertEquals(limitSize, feedResponse.getPosts().size());
    }
}
