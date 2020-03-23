package astric.server.lambda.account;

import astric.model.service.AccountService;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.response.account.LoginResponse;
import astric.server.service.AccountServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class LoginHandler {
    public LoginResponse handleRequest(LoginRequest request, Context context) {
        AccountService service = new AccountServiceImpl();
        return service.login(request);
    }
}
