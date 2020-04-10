package astric.server.dao;

import astric.model.dao.AuthDAO;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

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

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public String signUp(String username) {
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

    public void logout(String authToken, String username){
        Instant timestamp = Instant.now();
        Action action = Action.LOGOUT;
        putAuthTableAction(username, timestamp, authToken, action);
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

    public boolean sessionIsValid(String authToken) {
        boolean result = false;

        // if exists, has not been logged out, and was created in the last 5 minutes, return true
        try {
            QuerySpec spec = new QuerySpec()
                    .withKeyConditionExpression("authToken = :v_authToken")
                    .withValueMap(new ValueMap()
                            .withString(":v_authToken", authToken));
            ItemCollection<QueryOutcome> items = table.query(spec);
            Iterator<Item> iterator = items.iterator();

            //get all items with the authToken
            Item item;
            List<Item> actions = new ArrayList<>();
            while (iterator.hasNext()) {
                item = iterator.next();
                actions.add(item);
                System.out.println(item.toJSONPretty());
            }

            //check to see if actions exist with the authToken
            if (actions.isEmpty()) {
                return false;
            }

            //check to see if it has been logged out
            if (hasLogoutAction(actions)) {
                return false;
            }

            //check timestamps, within 5 minutes
            int sessionLength = 5;
            ChronoUnit timeUnit = ChronoUnit.MINUTES;
            if (elapsedSessionLength(actions, sessionLength, timeUnit)) {
                return false;
            }

            // if exists, has not been logged out, and was created in the last 5 minutes, return true
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean elapsedSessionLength(List<Item> actions, int sessionLength, ChronoUnit unit) {
        boolean result = true;
        Instant currentTime = Instant.now();
        Instant sessionValidTime = currentTime.minus(sessionLength, unit);

        //check to see if any action has happened in the last five minutes, if so, the session is valid
        for (Item item : actions) {
            Instant actionTime = Instant.parse((CharSequence) item.get("timestamp"));
            if (sessionValidTime.compareTo(actionTime) <= 0) {
                result = false;
            }
        }

        return result;
    }

    private boolean hasLogoutAction(List<Item> items) {
        for (Item item : items) {
            String authAction = (String) item.get("action");
            if (authAction.equals(Action.LOGOUT.toString())) return true;
        }
        return false;
    }
}
