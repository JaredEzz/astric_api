package astric.model.dao;

import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;

public interface UserDAO {
    SignUpResponse signUp(SignUpRequest request);

    LoginResponse login(LoginRequest request);

    LogoutResponse logout(LogoutRequest request);
}
