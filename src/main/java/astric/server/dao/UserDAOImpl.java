package astric.server.dao;

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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.Base64;


import java.io.*;
import java.net.URL;
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

            //upload image to S3
            byte[] image = new byte[0];
            String imageUrl = uploadProfileImage(username, image);

            //add sign up action to auth table
            String authToken = authDAO.signUp(username);

            //add user to user table
            User userToAdd = new User(request.getName(), handle, imageUrl, username);
            writeToUserTable(userToAdd, hashedPassword);

            //return auth token
            return new SignUpResponse(true, authToken);
        }
    }

    public String uploadProfileImage(String username, byte[] image) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        InputStream inputStream = new ByteArrayInputStream(image);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.length);
        metadata.setContentType("image/png");
//        try {
//            OutputStream os = new FileOutputStream(new File(fileName));
//            os.write(imageBytes);
//            os.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String uploadFilePath = "profiles/" + username + ".png";
        String bucketname = "hasson340";
        s3.putObject(bucketname, uploadFilePath, inputStream, metadata);

        URL result = s3.getUrl(bucketname, uploadFilePath);
        System.out.println(result.toString());
        return result.toString();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String givenPassword = request.getPassword();

        String storedPassword = getStoredPassword(username);

        boolean verified = HashUtil.validatePassword(givenPassword, storedPassword);

        if (verified) {
            //add login action to auth table
            String authToken = authDAO.login(username);
            return new LoginResponse(true, authToken);
        } else {
            return new LoginResponse(false, "Your username or password is invalid.", null);
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        LogoutResponse response = authDAO.sessionIsValid(request.getAuthToken()) ?
                new LogoutResponse(true) :
                new LogoutResponse(true, "Your session has timed out. You were logged out automatically.");
        authDAO.logout(request.getAuthToken(), request.getUsername());
        return response;
    }

    public User findUser(String username) {
        Item item = null;
        try {
            item = table.getItem("username", username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (item != null) {
            return new User(
                    item.getString("name"),
                    item.getString("handle"),
                    item.getString("imageURL"),
                    item.getString("username")
            );
        }
        return null;
    }

    private String getStoredPassword(String username) {
        String result = null;
        try {
            Item item = table.getItem("username", username);

            System.out.println("Printing item after retrieving it....");
            System.out.println(item.toJSONPretty());
            result = (String) item.get("passwordHash");
        } catch (Exception e) {
            System.err.println("GetItem failed.");
            System.err.println(e.getMessage());
        }
        return result;
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
        Item item = null;
        try {
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

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        ScanRequest scanRequest = new ScanRequest().withTableName(table.getTableName());
        ScanResult result = client.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            System.out.println(item);
            users.add(new User() {{
                setName(String.valueOf(item.get("name").getS()));
                setAlias(String.valueOf(item.get("handle").getS()));
                setImageUrl(String.valueOf(item.get("imageURL").getS()));
                setUsername(String.valueOf(item.get("username").getS()));
            }});
        }
        return users;
    }
}
