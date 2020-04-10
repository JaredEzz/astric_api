package astric.server.lambda;

import astric.server.dao.AuthDAOImpl;

public class AuthenticationHandler {

    public static AuthDAOImpl authDAO;

    public static void authenticateRequest(String authToken) throws RuntimeException {
        if (!new AuthDAOImpl().sessionIsValid(authToken)) {
            throw new RuntimeException("[RequestError] Invalid auth token, your session may have expired. Please login again.");
        }
    }
}
