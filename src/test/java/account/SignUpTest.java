package account;

import astric.model.dao.UserDAO;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.SignUpResponse;
import astric.server.dao.UserDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpTest {
    @Test
    void testSignUp() {
        SignUpRequest signUpRequest = new SignUpRequest("jaredhas", "jared", "Jared Hasson", "@jhass", "Instance of ImageProvider");
        UserDAO userDAO = new UserDAOImpl();
        SignUpResponse signUpResponse = userDAO.signUp(signUpRequest);

        assertNotNull(signUpResponse);
        assertTrue(signUpResponse.isSuccess());
        assertEquals("ae04c02a-bc73-4b58-984d-e5038c6f7c02", signUpResponse.getAuthToken());
    }

    // test the handler (milestone 4)
}

