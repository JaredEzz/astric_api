//package follow;
//
//import astric.model.dao.FollowingDAO;
//import astric.model.service.request.follow.FollowingRequest;
//import astric.model.service.response.follow.FollowingResponse;
//import astric.server.dao.FollowingDAOImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//class GetFollowingTest {
//
//    FollowingDAOImpl dao;
//
//    @BeforeEach
//    void setUp() {
//        dao = new FollowingDAOImpl();
//    }
//
//    @Test
//    public void getFollowingTest(){
//
//        int limitSize = 10;
//        FollowingRequest request =
//                new FollowingRequest("Alene", limitSize, null, "0c4193eb-dfc3-49b2-9a75-9549a5acd13b");
//        FollowingResponse followingResponse = dao.getFollowing(request);
////        assertNotNull(followingResponse);
////        assertEquals(followingResponse.getFollowees().size(), limitSize);
//    }
//
//}
