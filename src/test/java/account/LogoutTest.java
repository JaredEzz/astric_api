package account;

import astric.model.dao.UserDAO;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.server.dao.UserDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogoutTest {
    @Test
    void testLogout(){
        LogoutRequest logoutRequest = new LogoutRequest("jaredhasson", "ae04c02a-bc73-4b58-984d-e5038c6f7c02");

        UserDAO userDAO = new UserDAOImpl();
        LogoutResponse logoutResponse = userDAO.logout(logoutRequest);

        assertNotNull(logoutResponse);
        assertTrue(logoutResponse.isSuccess());
    }
}