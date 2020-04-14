package account;

import astric.model.domain.Post;
import astric.model.domain.User;
import astric.server.dao.FollowingDAOImpl;
import astric.server.dao.PostDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class PostDAOImplTest {
    PostDAOImpl postDAO;
    FollowingDAOImpl followingDAO;

    @BeforeEach
    void setUp() {
        postDAO = new PostDAOImpl();
        followingDAO = new FollowingDAOImpl();
    }

    @Test
    public void testWritePostToFeedTable(){
        Post post = new Post(
                new User(
                        "Jared Hasson",
                        "jaredhasson",
                        "imageUrl",
                        "jaredhasson"),
                Instant.now().toString(),
                "New Status Test"
        );
//        postDAO.writePostToFeedTable(post, "jaredhasson");

        for (int i = 0; i < 50; i++) {
            post = new Post(
                    new User(
                            "Jared Hasson",
                            "jaredhasson",
                            "imageUrl",
                            "jaredhasson"),
                    Instant.now().minus(i, ChronoUnit.MINUTES).toString(),
                    "New Status Test "+i
            );
            postDAO.writePostToFeedTable(post, "jaredhasson");
        }
    }

    @Test
    public void testBatchWritePost(){
        Post post = new Post(
                new User(
                        "Jared Hasson",
                        "jaredhasson",
                        "imageUrl",
                        "jaredhasson"),
                Instant.now().toString(),
                "New Status Test"
        );
        List<String> allFollowers = followingDAO.getAllFollowerUsernames("jaredhasson");
        postDAO.batchWritePostToFeedTable(post, allFollowers);
    }

    @Test
    public void testGetFeedPaginated(){
        boolean hasMorePages;
        Post lastPost = null;

        int i = 0;
        do {
            System.out.println("--- Page " + (++i) + " ---");
            Map<String, Object> result = postDAO.getFeedPaginated("jaredhasson", 10, lastPost);
            List<Post> feedPostList = (List<Post>) result.get("feedPostList");
            lastPost = feedPostList.get(feedPostList.size() - 1);
            hasMorePages = (boolean) result.get("hasMorePages");
        } while (hasMorePages);
    }
}
