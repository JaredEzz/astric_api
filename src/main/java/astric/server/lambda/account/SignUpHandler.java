package astric.server.lambda.account;

import astric.model.service.AccountService;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.SignUpResponse;
import astric.server.service.AccountServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class SignUpHandler {
    public SignUpResponse handleRequest(SignUpRequest request, Context context) {
        AccountService service = new AccountServiceImpl();
        return service.signUp(request);
    }
}
