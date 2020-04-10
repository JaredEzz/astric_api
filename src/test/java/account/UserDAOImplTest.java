package account;

import astric.model.domain.User;
import astric.server.dao.UserDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDAOImplTest {
//    UserDAOImpl userDAO;
//
//    @BeforeEach
//    void setUp() {
//        userDAO = new UserDAOImpl();
//    }
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
}
