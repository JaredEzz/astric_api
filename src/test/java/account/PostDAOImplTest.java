//package account;
//
//import astric.model.domain.Post;
//import astric.model.domain.User;
//import astric.model.service.request.post.MakePostRequest;
//import astric.model.service.response.post.MakePostResponse;
//import astric.server.dao.FollowingDAOImpl;
//import astric.server.dao.PostDAOImpl;
//import com.google.gson.Gson;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//public class PostDAOImplTest {
//    PostDAOImpl postDAO;
//    FollowingDAOImpl followingDAO;
//
//    @BeforeEach
//    void setUp() {
//        postDAO = new PostDAOImpl();
//        followingDAO = new FollowingDAOImpl();
//    }
//
//    @Test
//    public void testWritePostToFeedTable(){
//        Post post = new Post(
//                new User(
//                        "Jared Hasson",
//                        "jaredhasson",
//                        "imageUrl",
//                        "jaredhasson"),
//                Instant.now().toString(),
//                "New Status Test"
//        );
////        postDAO.writePostToFeedTable(post, "jaredhasson");
//
//        for (int i = 0; i < 50; i++) {
//            post = new Post(
//                    new User(
//                            "Jared Hasson",
//                            "jaredhasson",
//                            "imageUrl",
//                            "jaredhasson"),
//                    Instant.now().minus(i, ChronoUnit.MINUTES).toString(),
//                    "New Status Test "+i
//            );
//            postDAO.writePostToFeedTable(post, "jaredhasson");
//        }
//    }
//    @Test
//    public void testWritePostToStoryTable(){
//        Post post = new Post(
//                new User(
//                        "Jared Hasson",
//                        "jaredhasson",
//                        "imageUrl",
//                        "jaredhasson"),
//                Instant.now().toString(),
//                "New Status Test"
//        );
////        postDAO.writePostToFeedTable(post, "jaredhasson");
//
//        for (int i = 0; i < 50; i++) {
//            post = new Post(
//                    new User(
//                            "Jared Hasson",
//                            "jaredhasson",
//                            "imageUrl",
//                            "jaredhasson"),
//                    Instant.now().minus(i, ChronoUnit.MINUTES).toString(),
//                    "New Status Test "+i
//            );
//            postDAO.writePostToStoryTable(post, "jaredhasson");
//        }
//    }
//
//    @Test
//    public void testBatchWritePost(){
//        Post post = new Post(
//                new User(
//                        "Jared Hasson",
//                        "jaredhasson",
//                        "imageUrl",
//                        "jaredhasson"),
//                Instant.now().toString(),
//                "New Status Test"
//        );
//        List<String> allFollowers = followingDAO.getAllFollowerUsernames("jaredhasson");
//        postDAO.batchWritePostToFeedTable(post, allFollowers);
//    }
//
//    @Test
//    public void testGetFeedPaginated(){
//        boolean hasMorePages;
//        Post lastPost = null;
//
//        int i = 0;
//        do {
//            System.out.println("--- Page " + (++i) + " ---");
//            Map<String, Object> result = postDAO.getFeedPaginated("jaredhasson", 10, lastPost);
//            List<Post> feedPostList = (List<Post>) result.get("feedPostList");
//            lastPost = feedPostList.get(feedPostList.size() - 1);
//            hasMorePages = (boolean) result.get("hasMorePages");
//        } while (hasMorePages);
//    }
//
//    @Test
//    public void testGetStoryPaginated(){
//        boolean hasMorePages;
//        Post lastPost = null;
//
//        int i = 0;
//        do {
//            System.out.println("--- Page " + (++i) + " ---");
//            Map<String, Object> result = postDAO.getStoryPaginated("jaredhasson", 10, lastPost);
//            List<Post> feedPostList = (List<Post>) result.get("storyPostList");
//            lastPost = feedPostList.get(feedPostList.size() - 1);
//            hasMorePages = (boolean) result.get("hasMorePages");
//        } while (hasMorePages);
//    }
//
//    @Test
//    public void testMakePost(){
//        MakePostRequest request = new Gson().fromJson("{\n" +
//                "  \"post\": {\n" +
//                "    \"originatingUser\": {\n" +
//                "      \"username\": \"jaredhasson\",\n" +
//                "      \"alias\": \"@jaredezz\",\n" +
//                "      \"imageUrl\": \"imageURL\",\n" +
//                "      \"name\": \"Jared Hasson\"\n" +
//                "    },\n" +
//                "    \"timestamp\": \"2020-03-24T10:19:43.234776\",\n" +
//                "    \"message\": \"test1011\"\n" +
//                "  },\n" +
//                "  \"authToken\": \"75087575-a4b9-40fd-b098-9ccb20327f91\"\n" +
//                "}", MakePostRequest.class);
//        assertNotNull(request);
//        postDAO.makePost(request);
//    }
//}
