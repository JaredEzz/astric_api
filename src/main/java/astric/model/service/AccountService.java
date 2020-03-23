package astric.model.service;

import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;

/**
 * Defines the interface for the 'account' service.
 */
public interface AccountService {
    /**
     * Returns the username of a user that is successfully signed up or an
     * error message if the user could not be signed up, and the reason.
     *
     * @param request contains the data required to fulfill the request.
     * @return on success, username, else error.
     */
    SignUpResponse signUp(SignUpRequest request);

    /**
     * Returns true or error based on correct login information.
     *
     * @param request contains the data required to fulfill the request.
     * @return on success, true, else error with info.
     */
    LoginResponse login(LoginRequest request);

    /**
     * Returns whether the logout was successful or not.
     *
     * @param request contains the username of the user to log out.
     * @return on success, true, else error with info.
     */
    LogoutResponse logout(LogoutRequest request);
}
