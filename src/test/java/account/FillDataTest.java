//package account;
//
//import astric.model.domain.User;
//import astric.server.dao.FollowingDAOImpl;
//import astric.server.dao.UserDAOImpl;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
//import com.amazonaws.services.dynamodbv2.document.*;
//import com.amazonaws.services.dynamodbv2.model.WriteRequest;
//import com.amazonaws.services.sqs.AmazonSQS;
//import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
//import com.github.javafaker.Faker;
//import com.google.gson.Gson;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class FillDataTest {
//    FollowingDAOImpl followingDAO;
//    UserDAOImpl userDAO;
//    Faker faker;
//    AmazonDynamoDB client;
//    DynamoDB dynamoDB;
//    Table userTable;
//
//    @BeforeEach
//    void setUp() {
//        followingDAO = new FollowingDAOImpl();
//        userDAO = new UserDAOImpl();
//        faker = new Faker();
//        this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
//        this.dynamoDB = new DynamoDB(client);
//        this.userTable = dynamoDB.getTable("Users");
//        this.userDAO = new UserDAOImpl();
//    }
//
//    @Test
//    public void fillTestUsers(){
//        for (int j = 0; j < 4; j++) {
//            List<Item> itemBatch = new ArrayList<>();
//            List<Item> followBatch = new ArrayList<>();
//
//            for (int i = 0; i < 25; i++) {
//                String firstName = faker.name().firstName();
//                String lastName = faker.name().lastName();
//                String username = firstName.toLowerCase() +"_"+ faker.animal().name().replace(" ", "_");
//                itemBatch.add(
//                        new Item()
//                                .withPrimaryKey("username", username)
//                                .withPrimaryKey("handle", username)
//                                .withString("passwordHash", "fakeHash")
//                                .withString("name", firstName + " " + lastName)
//                                .withString("imageURL", "https://raw.githubusercontent.com/JaredEzz/Astric/master/assets/images/blank_profile.jpg?token=ADIQLIJ2ECUB5U7YKDU6BTC6T5I5O")
//                );
//                followBatch.add(new Item()
//                        .withPrimaryKey("follower", username)
//                        .withString("followee", "jaredhasson"));
//                System.out.println("Added "+username);
//            }
//
//            try {
//                TableWriteItems feedTableWriteItems = new TableWriteItems(userTable.getTableName())
//                        .withItemsToPut(itemBatch);
//
//
//                System.out.println("Making the request.");
//                BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(feedTableWriteItems);
//                do {
//
//                    // Check for unprocessed keys which could happen if you exceed
//                    // provisioned throughput
//
//                    Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
//
//                    if (outcome.getUnprocessedItems().size() == 0) {
//                        System.out.println("No unprocessed items found");
//                    } else {
//                        System.out.println("Retrieving the unprocessed items");
//                        outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
//                    }
//
//                } while (outcome.getUnprocessedItems().size() > 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                TableWriteItems followTableWriteItems = new TableWriteItems("Follow")
//                        .withItemsToPut(followBatch);
//
//
//                System.out.println("Making the request.");
//                BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(followTableWriteItems);
//                do {
//
//                    // Check for unprocessed keys which could happen if you exceed
//                    // provisioned throughput
//
//                    Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
//
//                    if (outcome.getUnprocessedItems().size() == 0) {
//                        System.out.println("No unprocessed items found");
//                    } else {
//                        System.out.println("Retrieving the unprocessed items");
//                        outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
//                    }
//
//                } while (outcome.getUnprocessedItems().size() > 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Test
//    public void addFollowers(){
//        //userdao.get all users
//        List<User> users = userDAO.getAllUsers();
//        //for each user, make them follow jaredhasson
//        for(User u : users){
//            followingDAO.writeToFollowTable(u.getUsername(),"jaredhasson");
//            System.out.println("added follow from "+u.getUsername());
//        }
//    }
//}
