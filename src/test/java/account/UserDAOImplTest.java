package account;

import astric.model.domain.User;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.response.account.LogoutResponse;
import astric.server.dao.UserDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOImplTest {
    UserDAOImpl userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
    }

    @Test
    void testUploadS3(){
        userDAO.uploadProfileImage("jaredhasson1215","image");
    }

    //
//    @Test
//    public void testWriteToUserTable(){
//        userDAO.writeToUserTable(new User("jared hasson", "@jaredezz", "imageurl", "jaredhasson"), "passHash");
//        assertTrue(true);
//    }
//
//    @Test
//    public void testUserExistsWithUsername(){
//        boolean exists = userDAO.userExistsWithUsername("jaredhasson");
//        assertTrue(exists);
//        exists = userDAO.userExistsWithUsername(Integer.toString(new Random().nextInt()));
//        assertFalse(exists);
//    }
//
//    @Test
//    public void testUserExistsWithHandle(){
//        boolean exists = userDAO.userExistsWithHandle("@jaredezz");
//        assertTrue(exists);
//        exists = userDAO.userExistsWithHandle(Integer.toString(new Random().nextInt()));
//        assertFalse(exists);
//    }
//    @Test
//    public void testLogout() {
//        LogoutResponse response = userDAO.logout(new LogoutRequest("testuser1132", "b7a63211-d8c7-408d-be9c-5cd2df1a1e4b"));
//        System.out.println(response.getMessage());
//        System.out.println(response.isSuccess());
//        assertNotNull(response);
//    }
}
