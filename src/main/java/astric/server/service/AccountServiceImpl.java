package astric.server.service;

import astric.model.service.AccountService;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;
import astric.model.dao.UserDAO;
import astric.server.dao.UserDAOImpl;

public class AccountServiceImpl implements AccountService {

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        UserDAO dao = new UserDAOImpl();
        return dao.signUp(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UserDAO dao = new UserDAOImpl();
        return dao.login(request);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        UserDAO dao = new UserDAOImpl();
        return dao.logout(request);
    }
}
