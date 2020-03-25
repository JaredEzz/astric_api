package astric.server.lambda.account;

import astric.model.service.AccountService;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.response.account.LogoutResponse;
import astric.server.service.AccountServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static astric.server.lambda.AuthenticationHandler.authenticateRequest;

public class LogoutHandler implements RequestHandler<LogoutRequest, LogoutResponse> {
    public LogoutResponse handleRequest(LogoutRequest request, Context context) {
        AccountService service = new AccountServiceImpl();
        authenticateRequest(request.getAuthToken());
        try {
            return service.logout(request);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Something went wrong on our end. Please try again.");
        }
    }
}
