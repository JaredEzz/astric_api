package account;

import astric.server.dao.AuthDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthDAOImplTest {
    AuthDAOImpl authDAO;
    @BeforeEach
    void setUp() {
        authDAO = new AuthDAOImpl();
    }
    @Test
    public void testSignUp(){
        String result = authDAO.signUp("testusername");
        assertNotNull(result);
    }
}
