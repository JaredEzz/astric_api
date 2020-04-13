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
import com.sun.tools.javac.util.Pair;

import java.util.*;

import static astric.server.dao.UserDAOImpl.hardCodedUsers;


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
        Pair<List<String>, Boolean> result = getAllFollowingPaginated(request.getFollowerUsername(), request.getLimit(), request.getLastFollowee().getUsername());
        List<User> responseFollowees = new ArrayList<>();
        for (String username : result.fst) {
            responseFollowees.add(userDAO.findUser(username));
        }
        boolean hasMorePages = result.snd;
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

    public Pair<List<String>, Boolean> getAllFollowingPaginated(String followerUsername, int pageSize, String lastFollowee) {
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
        return new Pair<>(followingUsernames, hasMorePages);
    }

    @Override
    public FollowersResponse getFollowers(FollowersRequest request) {
        assert request.getLimit() > 0;
        assert request.getFolloweeUsername() != null;

        Pair<List<String>, Boolean> result = getAllFollowersPaginated(request.getFolloweeUsername(), request.getLimit(), request.getLastFollower().getUsername());
        List<User> responseFollowers = new ArrayList<>();
        for (String username : result.fst) {
            responseFollowers.add(userDAO.findUser(username));
        }
        boolean hasMorePages = result.snd;
        return new FollowersResponse(responseFollowers, hasMorePages);
    }

    public Pair<List<String>, Boolean> getAllFollowersPaginated(String followeeUsername, int pageSize, String lastFollower) {
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
        return new Pair<>(followersUsernames, hasMorePages);
    }

    @Override
    public FollowResponse doFollow(FollowRequest request) {
        assert request.getFolloweeUsername() != null;
        assert request.getFollowerUsername() != null;

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
                            new DeleteItemSpec().withPrimaryKey("follower", followerUsername)
                                    .withConditionExpression("#fe = :val")
                                    .withNameMap(new NameMap().with("#fe", "followee"))
                                    .withValueMap(new ValueMap().withString(":val", followeeUsername))
                    );
            // Check the response.
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
//
//    /**
//     * Determines the index for the first followee in the specified 'allFollowees' list that should
//     * be returned in the current request. This will be the index of the next followee after the
//     * specified 'lastFollowee'.
//     *
//     * @param lastFollowee the last followee that was returned in the previous request or null if
//     *                     there was no previous request.
//     * @param allFollowees the generated list of followees from which we are returning paged results.
//     * @return the index of the first followee to be returned.
//     */
//    private int getFolloweesStartingIndex(User lastFollowee, List<User> allFollowees) {
//
//        int followeesIndex = 0;
//
//        if (lastFollowee != null) {
//            // This is a paged request for something after the first page. Find the first item
//            // we should return
//            for (int i = 0; i < allFollowees.size(); i++) {
//                if (lastFollowee.equals(allFollowees.get(i))) {
//                    // We found the index of the last item returned last time. Increment to get
//                    // to the first one we should return
//                    followeesIndex = i + 1;
//                }
//            }
//        }
//
//        return followeesIndex;
//    }
}

