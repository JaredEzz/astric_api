package astric.server.dao;

import astric.model.dao.AuthDAO;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class AuthDAOImpl implements AuthDAO {

    AmazonDynamoDB client;
    DynamoDB dynamoDB;
    Table table;

    public enum Action {
        SIGNUP,
        LOGIN,
        LOGOUT,
        POST,
        FOLLOW,
        UNFOLLOW
    }

    public AuthDAOImpl() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        this.dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable("Authorization");
    }

    private String generateAuthToken(){
        return UUID.randomUUID().toString();
    }

    public String signUp(String username){
        Instant timestamp = Instant.now();
        String authToken = generateAuthToken();
        Action action = Action.SIGNUP;

        return putAuthTableAction(username, timestamp, authToken, action);
    }

    public String login(String username) {
        Instant timestamp = Instant.now();
        String authToken = generateAuthToken();
        Action action = Action.LOGIN;

        return putAuthTableAction(username, timestamp, authToken, action);
    }

    private String putAuthTableAction(String username, Instant timestamp, String authToken, Action action) {
        PutItemOutcome outcome = null;
        try {
            outcome = table
                    .putItem(
                            new Item()
                                    .withPrimaryKey("authToken", authToken)
                                    .withString("timestamp", timestamp.toString())
                                    .withString("action", action.toString())
                                    .withString("username", username)
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outcome == null ? null : authToken;
    }
}
