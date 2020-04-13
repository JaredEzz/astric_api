//package account;
//
//import astric.server.dao.AuthDAOImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.Instant;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class AuthDAOImplTest {
//    AuthDAOImpl authDAO;
//    @BeforeEach
//    void setUp() {
//        authDAO = new AuthDAOImpl();
//    }
//    @Test
//    public void testSignUp(){
//        String result = authDAO.signUp("testusername");
//        assertNotNull(result);
//    }
//
//    @Test
//    public void testSessionIsValid() {
////        assertTrue(authDAO.sessionIsValid("4fed76d5-87fd-4aa2-a311-b5db593c3d49"));
//
//        authDAO.logout("4fed76d5-87fd-4aa2-a311-b5db593c3d49", "fakeuser");
//    }
//
//
//}
