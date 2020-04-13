//package account;
//
//import astric.model.domain.User;
//import astric.server.dao.FollowingDAOImpl;
//import astric.server.dao.UserDAOImpl;
//import com.github.javafaker.Faker;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//public class FillDataTest {
//    FollowingDAOImpl followingDAO;
//    UserDAOImpl userDAO;
//    Faker faker;
//
//    @BeforeEach
//    void setUp() {
//        followingDAO = new FollowingDAOImpl();
//        userDAO = new UserDAOImpl();
//        faker = new Faker();
//    }
//
//    @Test
//    public void fillTestUsers(){
//        for (int i = 0; i < 24; i++) {
//            String name = faker.name().firstName();
//            userDAO.writeToUserTable(
//                    new User(
//                            name,
//                            '@'+faker.animal().name(),
//                            "https://lh3.googleusercontent.com/proxy/smRpWELAtNZJNbKcYp4c8XnlN47p5qQ8jDr2UEJweSwqHwRnJLqAXhrIwL4gckZMdavBKc2cnUSscpVC2alRnVg0XK_2-8MQSxD1LlheVFVnt4hw7f756gGSvVFnYzo",
//                            name
//                    ), "emptyPassHash"
//            );
//        }
//    }
//
//    @Test
//    public void addFollowers(){
//        //userdao.get all users
//        List<User> users = userDAO.getAllUsers();
//        //for each user, make them follow jaredhasson
//        for(User u : users){
//            followingDAO.writeToFollowTable(u.getUsername(),"jaredhasson");
//        }
//    }
//}
