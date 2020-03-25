package astric.server.lambda.account;

import astric.model.service.AccountService;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.response.account.LoginResponse;
import astric.server.service.AccountServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LoginHandler implements RequestHandler<LoginRequest, LoginResponse> {
    public LoginResponse handleRequest(LoginRequest request, Context context) {
        try {
            AccountService service = new AccountServiceImpl();
            return service.login(request);
        } catch (Exception e) {
            throw new RuntimeException("[ServerError] Something went wrong on our end. Please try again.");
        }
    }
}
