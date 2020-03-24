package astric.server.dao;

import astric.model.dao.UserDAO;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserDAOImpl implements UserDAO {
    private Map<String, String> usernamePasswordMap = new HashMap<String, String>() {{
        put("username", "password");
        put("jaredhasson", "password");
    }};

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        // TODO check username against existing database, if username doesn't exist,
        // return success message, (milestone 4 - add user to database)

        return new SignUpResponse(true);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String expectedPassword = usernamePasswordMap.get(username);
        if(expectedPassword != null && expectedPassword.equals(password)){
//            String auth = UUID.randomUUID().toString();
            String auth = "ae04c02a-bc73-4b58-984d-e5038c6f7c02";
            return new LoginResponse(true, auth);
        } else {
            return new LoginResponse(false, "Incorrect Username/Password", null);
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse(true);
    }
}
