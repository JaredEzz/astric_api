package astric.server.dao;

import astric.HashUtil;
import astric.model.dao.UserDAO;
import astric.model.domain.User;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import javax.management.DynamicMBean;
import java.util.*;

public class UserDAOImpl implements UserDAO {
    private Map<String, String> usernamePasswordMap = new HashMap<String, String>() {{
        put("username", "password");
        put("jaredhasson", "password");
    }};

    public static List<User> hardCodedUsers = Arrays.asList(
            new User("Jared", "Hasson", "@jaredezz", "assets/images/astric.png", "jaredhasson"),
            new User("Thomas", "Banks", "@tb", "assets/images/man_profile.png", "tbanks"),
            new User("Wendy", "Watts", "@wwatts", "assets/images/woman_profile.png", "wendyw"),
            new User("Orville", "Klaus", "@santa", "assets/images/santa.png", "ovk"),
            new User("Manny", "Woodpecker", "@birdlover", "assets/images/woodpecker.jpg", "mannywp"),
            new User("Fanny", "Follower", "@ff", "assets/images/fanny_pack.jpeg", "follo"));


    private List<String> usernames = Arrays.asList("username", "jaredhasson");

    private List<String> handles = Arrays.asList("@user", "@jared");

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        // TODO check username against existing database, if username/handle doesn't exist,
        String username = request.getUsername();
        String handle = request.getHandle();


        // return success message, (milestone 4 - add user to database)
        if (usernames.contains(username)){
            //username exists in user db
            return new SignUpResponse(false, null, "Username already exists.");
        } else if (handles.contains(handle)) {
            //handle exists in user db
            return new SignUpResponse(false, null, "Handle already exists.");
        } else {
            //hash password
            String hashedPassword = HashUtil.hashPassword(request.getPassword());

            //add user to user table
            User userToAdd = new User("Test User First", "Test User Last", "alias", "S3Url", "username");

            //generate auth token

            //add sign up action to auth table

            //return auth token

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

    public User findUser(String username){
        for (User u : hardCodedUsers) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    public void writeToUserTable(User user, String passwordHash){
        String username = user.getUsername();
        String fullName = user.getFirstName() + " " + user.getLastName();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("Users");

        final Map<String, Object> userMap = new HashMap<>();

        userMap.put("passwordHash", passwordHash);

        try {
            System.out.println("Adding a new user...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("username", username, "fullName", fullName).withMap("user", userMap));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e) {
            System.err.println("Unable to add user: " + username + ": " + fullName);
            System.err.println(e.getMessage());
        }
    }
}
