package astric.server.dao;

import astric.model.dao.AuthDAO;
import astric.server.lambda.account.HashUtil;
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
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.*;

public class UserDAOImpl implements UserDAO {

    AmazonDynamoDB client;
    DynamoDB dynamoDB;
    Table table;
    AuthDAOImpl authDAO;

    public static List<User> hardCodedUsers = Arrays.asList(
            new User("Jared Hasson", "@jaredezz", "assets/images/astric.png", "jaredhasson"),
            new User("Thomas Banks", "@tb", "assets/images/man_profile.png", "tbanks"),
            new User("Wendy Watts", "@wwatts", "assets/images/woman_profile.png", "wendyw"),
            new User("Orville Klaus", "@santa", "assets/images/santa.png", "ovk"),
            new User("Manny Woodpecker", "@birdlover", "assets/images/woodpecker.jpg", "mannywp"),
            new User("Fanny Follower", "@ff", "assets/images/fanny_pack.jpeg", "follo"));


    private List<String> usernames = Arrays.asList("username", "jaredhasson");

    private List<String> handles = Arrays.asList("@user", "@jared");

    private Map<String, String> usernamePasswordMap = new HashMap<String, String>() {{
        put("username", "password");
        put("jaredhasson", "password");
    }};

    public UserDAOImpl() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        this.dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable("Users");
        this.authDAO = new AuthDAOImpl();
    }

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        String username = request.getUsername();
        String handle = request.getHandle();

        // return success message, (milestone 4 - add user to database)
        if (userExistsWithUsername(username)) {
            //username exists in user db
            return new SignUpResponse(false, null, "Username already exists.");
        } else if (userExistsWithHandle(handle)) {
            //handle exists in user db
            return new SignUpResponse(false, null, "Handle already exists.");
        } else {
            //hash password
            String hashedPassword = HashUtil.hashPassword(request.getPassword());

            //add sign up action to auth table
            String authToken = authDAO.signUp(username);

            //add user to user table
            User userToAdd = new User(request.getName(), handle, "S3Url", username);
            writeToUserTable(userToAdd, hashedPassword);

            //return auth token
            return new SignUpResponse(true, authToken);
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String givenPassword = request.getPassword();

        String expectedPassword = usernamePasswordMap.get(username);
        //hash given password
        String givenHash = HashUtil.hashPassword(givenPassword);

        //get user

        //check that the hashes are equal




        if (expectedPassword != null && expectedPassword.equals(givenPassword)) {
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

    public User findUser(String username) {
        for (User u : hardCodedUsers) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }


    public void writeToUserTable(User user, String passwordHash) {
        String username = user.getUsername();
        String handle = user.getAlias();

        try {
            System.out.println("Adding a new user...");
            PutItemOutcome outcome = table
                    .putItem(
                            new Item()
                                    .withPrimaryKey("username", username)
                            .withPrimaryKey("handle", handle)
                            .withString("passwordHash", passwordHash)
                            .withString("name", user.getName())
                            .withString("imageURL", user.getImageUrl())
                                    );

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e) {
            System.err.println("Unable to add user: " + username);
            System.err.println(e.getMessage());
        }
    }

    public boolean userExistsWithUsername(String username) {
        Item item =  null;
        try{
            item = table.getItem("username", username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item != null;
    }

    public boolean userExistsWithHandle(String handle) {
        Index index = table.getIndex("handle-index");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("#h = :v_handle")
                .withNameMap(new NameMap()
                        .with("#h", "handle"))
                .withValueMap(new ValueMap()
                        .withString(":v_handle", handle));

        ItemCollection<QueryOutcome> items = index.query(spec);
        Iterator<Item> iterator = items.iterator();

        List<Object> results = new ArrayList<>();
        while (iterator.hasNext()) {
            results.add(iterator.next());
        }
        return !results.isEmpty();
    }
}
