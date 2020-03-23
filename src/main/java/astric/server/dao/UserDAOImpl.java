package astric.server.dao;

import astric.model.dao.UserDAO;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;

public class UserDAOImpl implements UserDAO {
    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        // TODO check username against existing database, if username doesn't exist,
        // return success message, (milestone 4 - add user to database)

        return new SignUpResponse(true);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // TODO check username and password
        return new LoginResponse(true);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse(true);
    }
}
