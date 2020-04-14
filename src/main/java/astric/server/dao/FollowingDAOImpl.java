package astric.server.dao;

import astric.model.dao.FollowingDAO;
import astric.model.domain.User;
import astric.model.service.request.follow.FollowRequest;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.request.follow.IsFollowingRequest;
import astric.model.service.response.follow.FollowResponse;
import astric.model.service.response.follow.FollowersResponse;
import astric.model.service.response.follow.FollowingResponse;
import astric.model.service.response.follow.IsFollowingResponse;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.*;

public class FollowingDAOImpl implements FollowingDAO {

    AmazonDynamoDB client;
    DynamoDB dynamoDB;
    Table table;
    UserDAOImpl userDAO;

    public FollowingDAOImpl() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        this.dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable("Follows");
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerUsername() != null;
        Map<String, Object> result = getAllFollowingPaginated(request.getFollowerUsername(), request.getLimit(), request.getLastFollowee() == null ? null : request.getLastFollowee().getUsername());
        List<User> responseFollowees = new ArrayList<>();
        List<String> followingList = (ArrayList<String>) result.get("followingList");
        for (String username : followingList) {
            responseFollowees.add(userDAO.findUser(username));
        }
        boolean hasMorePages = (boolean) result.get("hasMorePages");
        return new FollowingResponse(responseFollowees, hasMorePages);
    }

//    public void getAllFollowing(String followerUsername) {
//        Item item;
//        try {
//            QuerySpec spec = new QuerySpec()
//                    .withKeyConditionExpression("#fr = :v_fr")
//                    .withNameMap(new NameMap().with("#fr", "follower"))
//                    .withValueMap(new ValueMap().withString(":v_fr", followerUsername))
//                    .withScanIndexForward(false);
//            ItemCollection<QueryOutcome> outcome = table.query(spec);
//            if (outcome != null) {
//                for (Item value : outcome) {
//                    item = value;
//                    System.out.println(item.get("followee"));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public List<String> getAllFollowerUsernames(String followeeUsername) {
        List<String> result = new ArrayList<>();
        Index index = table.getIndex("followee-index");
        Item item;
        try{
            QuerySpec spec = new QuerySpec()
                    .withKeyConditionExpression("#fe = :v_fe")
                    .withNameMap(new NameMap().with("#fe", "followee"))
                    .withValueMap(new ValueMap().withString(":v_fe", followeeUsername))
                    .withScanIndexForward(true);
            ItemCollection<QueryOutcome> outcome = index.query(spec);
            if (outcome != null) {
                for (Item value : outcome) {
                    item = value;
                    result.add((String) item.get("follower"));
//                    System.out.println(item.get("follower"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object> getAllFollowingPaginated(String followerUsername, int pageSize, String lastFollowee) {
        Item item;
        Map<String, AttributeValue> lastEvaluatedKey = null;
        List<String> followingUsernames = new ArrayList<>();
        try {
            QuerySpec spec = (lastFollowee == null) ? new QuerySpec()
                    .withKeyConditionExpression("#fr = :v_fr")
                    .withNameMap(new NameMap().with("#fr", "follower"))
                    .withValueMap(new ValueMap().withString(":v_fr", followerUsername))
                    .withScanIndexForward(true)
                    .withMaxResultSize(pageSize) :
                    new QuerySpec()
                            .withKeyConditionExpression("#fr = :v_fr")
                            .withNameMap(new NameMap().with("#fr", "follower"))
                            .withValueMap(new ValueMap().withString(":v_fr", followerUsername))
                            .withScanIndexForward(true)
                            .withMaxResultSize(pageSize)
                            .withExclusiveStartKey("follower", followerUsername, "followee", lastFollowee);

            ItemCollection<QueryOutcome> outcome = table.query(spec);
            if (outcome != null) {
                for (Item value : outcome) {
                    item = value;
                    lastEvaluatedKey = outcome.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
                    System.out.println(item.get("followee"));
                    followingUsernames.add((String) item.get("followee"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean hasMorePages = lastEvaluatedKey != null;

        Map<String, Object> result = new HashMap<>();
        result.put("followingList", followingUsernames);
        result.put("hasMorePages", hasMorePages);
        return result;
    }

    @Override
    public FollowersResponse getFollowers(FollowersRequest request) {
        assert request.getLimit() > 0;
        assert request.getFolloweeUsername() != null;

        Map<String, Object> result = getAllFollowersPaginated(request.getFolloweeUsername(), request.getLimit(), request.getLastFollower() == null ? null : request.getLastFollower().getUsername());
        List<User> responseFollowers = new ArrayList<>();
        List<String> followersList = (ArrayList<String>) result.get("followersList");

        for (String username : followersList) {
            responseFollowers.add(userDAO.findUser(username));
        }
        boolean hasMorePages = (boolean) result.get("hasMorePages");
        return new FollowersResponse(responseFollowers, hasMorePages);
    }

    public Map<String, Object> getAllFollowersPaginated(String followeeUsername, int pageSize, String lastFollower) {
        Item item;
        Index index = table.getIndex("followee-index");
        List<String> followersUsernames = new ArrayList<>();
        Map<String, AttributeValue> lastEvaluatedKey = null;
        try {
            QuerySpec spec = (lastFollower == null) ? new QuerySpec()
                    .withKeyConditionExpression("#fe = :v_fe")
                    .withNameMap(new NameMap().with("#fe", "followee"))
                    .withValueMap(new ValueMap().withString(":v_fe", followeeUsername))
                    .withScanIndexForward(true)
                    .withMaxResultSize(pageSize) :
                    new QuerySpec()
                            .withKeyConditionExpression("#fe = :v_fe")
                            .withNameMap(new NameMap().with("#fe", "followee"))
                            .withValueMap(new ValueMap().withString(":v_fe", followeeUsername))
                            .withScanIndexForward(true)
                            .withMaxResultSize(pageSize)
                            .withExclusiveStartKey("followee", followeeUsername, "follower", lastFollower);

            ItemCollection<QueryOutcome> outcome = index.query(spec);
            if (outcome != null) {
                for (Item value : outcome) {
                    item = value;
                    lastEvaluatedKey = outcome.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
                    System.out.println(item.get("follower"));
                    followersUsernames.add((String) item.get("follower"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean hasMorePages = lastEvaluatedKey != null;

        Map<String, Object> result = new HashMap<>();
        result.put("followersList", followersUsernames);
        result.put("hasMorePages", hasMorePages);
        return result;
    }

    @Override
    public FollowResponse doFollow(FollowRequest request) {
        assert request.getFolloweeUsername() != null;
        assert request.getFollowerUsername() != null;

        if (!userDAO.userExistsWithUsername(request.getFolloweeUsername())) {
            return new FollowResponse(false, "User " + request.getFolloweeUsername() + " does not exist!");
        }
        if (!userDAO.userExistsWithUsername(request.getFollowerUsername())) {
            return new FollowResponse(false, "User " + request.getFollowerUsername() + " does not exist!");
        }

        String message;
        if (request.isFollow()) {
            //follow
            writeToFollowTable(request.getFollowerUsername(), request.getFolloweeUsername());
            message = String.format("%s has successfully followed %s", request.getFollowerUsername(), request.getFolloweeUsername());
        } else {
            //unfollow
            removeFromFollowTable(request.getFollowerUsername(), request.getFolloweeUsername());
            message = String.format("%s has successfully unfollowed %s", request.getFollowerUsername(), request.getFolloweeUsername());
        }
        return new FollowResponse(true, message);
    }

    @Override
    public IsFollowingResponse isFollowing(IsFollowingRequest request) {

        String followerUsername = request.getFollowerUsername();
        String followeeUsername = request.getFolloweeUsername();

        assert followeeUsername != null;
        assert followerUsername != null;

        boolean exists = checkFollowTable(followerUsername, followeeUsername);

        return new IsFollowingResponse(true, exists, String.format("%s is %sfollowing %s", followerUsername, exists ? "" : "not ", followeeUsername));
    }

    public boolean checkFollowTable(String followerUsername, String followeeUsername) {
        boolean result = false;
        try {
            QuerySpec spec = new QuerySpec()
                    .withKeyConditionExpression("#fr = :v_fr and followee = :v_fe")
                    .withNameMap(new NameMap().with("#fr", "follower"))
                    .withValueMap(new ValueMap().withString(":v_fr", followerUsername).withString(":v_fe", followeeUsername));
            ItemCollection<QueryOutcome> outcome = table.query(spec);
            if (outcome != null) {
                Iterator<Item> iterator = outcome.iterator();
                result = iterator.hasNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void writeToFollowTable(String follower, String followee) {
        try {
            PutItemOutcome outcome = table
                    .putItem(
                            new Item()
                                    .withPrimaryKey("follower", follower)
                                    .withString("followee", followee)
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void removeFromFollowTable(String followerUsername, String followeeUsername) {
        try {
            DeleteItemOutcome outcome = table
                    .deleteItem(
                            new DeleteItemSpec().withPrimaryKey(new PrimaryKey("follower", followerUsername, "followee", followeeUsername))
                    );
            // Check the response.
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}

