package astric.server.lambda;

public class AuthenticationHandler {

    public static void authenticateRequest(String authToken) throws RuntimeException {
        if (!authToken.equals("ae04c02a-bc73-4b58-984d-e5038c6f7c02")) {
            throw new RuntimeException("[RequestError] Invalid auth token");
        }
    }
}
