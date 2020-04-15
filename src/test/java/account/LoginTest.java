package account;

import astric.model.dao.UserDAO;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.response.account.LoginResponse;
import astric.server.dao.UserDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {
    @Test
    void testLogin(){
        LoginRequest loginRequest = new LoginRequest("jaredhasson1200", "password1200");

        UserDAO userDAO = new UserDAOImpl();
        LoginResponse loginResponse = userDAO.login(loginRequest);

        assertNotNull(loginResponse);
        assertTrue(loginResponse.isSuccess());
    }
}