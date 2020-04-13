//package account;
//
//import astric.model.service.request.follow.FollowersRequest;
//import astric.model.service.request.follow.FollowingRequest;
//import astric.model.service.response.follow.FollowersResponse;
//import astric.server.dao.FollowingDAOImpl;
//import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
//import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
//import com.amazonaws.services.dynamodbv2.model.AttributeValue;
////import com.github.javafaker.Faker;
//import com.sun.tools.javac.util.Pair;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class FollowDAOImplTest {
//    FollowingDAOImpl followingDAO;
//
//    @BeforeEach
//    void setUp() {
//        followingDAO = new FollowingDAOImpl();
//    }
//
//    @Test
//    public void testWriteToFollowTable(){
////        Faker faker = new Faker();
////        for (int i = 0; i < 24; i++) {
////            String followee = faker.name().firstName().toLowerCase();
//            followingDAO.writeToFollowTable("jaredhasson","Beatriz");
////        }
//    }
//
//    @Test
//    public void testRemoveFromFollowTable(){
//        followingDAO.removeFromFollowTable("Beatriz", "Alene");
//    }
//
//    @Test
//    public void testCheckFollowTable(){
//        boolean result = followingDAO.checkFollowTable("jaredhasson", "Beatriz");
//        assertTrue(result);
//    }
//
////    @Test
////    public void testGetAllFollowingPaginated(){
////        boolean hasMorePages;
////        String lastFollowee = null;
////        int i = 0;
////        do{
////            System.out.println("--- Page "+(++i)+" ---");
////            Pair<List<String>, Boolean> result = followingDAO.getAllFollowingPaginated("jared", 10, lastFollowee);
////            lastFollowee = result.fst.get(result.fst.size()-1);
////            hasMorePages = result.snd;
////        } while(hasMorePages);
////    }
//
////    @Test
////    public void testGetAllFollowersPaginated(){
////        boolean hasMorePages;
////        String lastFollowee = null;
////        int i = 0;
////        do{
////            System.out.println("--- Page "+(++i)+" ---");
////            Pair<List<String>, Boolean> result = followingDAO.getAllFollowersPaginated("jaredhasson", 10, lastFollowee);
////            lastFollowee = result.fst.get(result.fst.size()-1);
////            hasMorePages = result.snd;
////        } while(hasMorePages);
////    }
//
//    @Test
//    public void testGetFollowers() {
//        FollowersRequest request = new FollowersRequest("jaredhasson",10,null, "34ea206c-2fb1-4e55-b501-d860060fa6bc");
//        FollowersResponse response = followingDAO.getFollowers(request);
//        System.out.println(response.toString());
//    }
//}
