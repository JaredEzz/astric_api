//package account;
//
//import astric.model.domain.Post;
//import astric.model.domain.User;
//import astric.server.dao.PostDAOImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.Instant;
//
//public class PostDAOImplTest {
//    PostDAOImpl postDAO;
//
//    @BeforeEach
//    void setUp() {
//        postDAO = new PostDAOImpl();
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
//        postDAO.writePostToFeedTable(post, null);
//    }
//}
