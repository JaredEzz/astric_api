package astric.server.dao;

import astric.model.dao.UserDAO;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;

import java.util.*;

public class UserDAOImpl implements UserDAO {
    private Map<String, String> usernamePasswordMap = new HashMap<String, String>() {{
        put("username", "password");
        put("jaredhasson", "password");
    }};

    private List<String> usernames = Arrays.asList("username", "jaredhasson");

    private List<String> handles = Arrays.asList("@user", "@jared");

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        // TODO check username against existing database, if username/handle doesn't exist,
        String username = request.getUsername();
        String handle = request.getHandle();


        // return success message, (milestone 4 - add user to database)
        if (usernames.contains(username)){
            return new SignUpResponse(false, null, "Username already exists.");
        } else if (handles.contains(handle)) {
            return new SignUpResponse(false, null, "Handle already exists.");
        } else {
            String auth = "ae04c02a-bc73-4b58-984d-e5038c6f7c02";
            return new SignUpResponse(true, auth);
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String expectedPassword = usernamePasswordMap.get(username);
        //check password TODO hashing
        if(expectedPassword != null && expectedPassword.equals(password)){
//            String auth = UUID.randomUUID().toString();
            // milestone 4 - set up auth token/session to expire
            String auth = "ae04c02a-bc73-4b58-984d-e5038c6f7c02";
            return new LoginResponse(true, auth);
        } else {
            return new LoginResponse(false, "Your username or password is invalid.", null);
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        // (milestone 4 - invalidate authToken)
        return new LogoutResponse(true);
    }
}
