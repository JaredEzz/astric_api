package astric.server.lambda.account;

import astric.model.service.AccountService;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.response.account.LogoutResponse;
import astric.server.service.AccountServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class LogoutHandler {
    public LogoutResponse handleRequest(LogoutRequest request, Context context) {
        AccountService service = new AccountServiceImpl();
        return service.logout(request);
    }
}
