package astric.server.dao;

import astric.model.dao.PostDAO;
import astric.model.domain.Post;
import astric.model.domain.User;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.request.post.StoryRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;
import astric.model.service.response.post.StoryResponse;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.*;

public class PostDAOImpl implements PostDAO {

    AmazonDynamoDB client;
    DynamoDB dynamoDB;
    Table feedTable;
    Table storyTable;
    UserDAOImpl userDAO;
    FollowingDAOImpl followingDAO;
    Gson gson;
    AmazonSQS sqs;

    public PostDAOImpl() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        this.dynamoDB = new DynamoDB(client);
        this.feedTable = dynamoDB.getTable("Feed");
        this.storyTable = dynamoDB.getTable("Story");
        this.userDAO = new UserDAOImpl();
        this.followingDAO = new FollowingDAOImpl();
        this.gson = new Gson();
        this.sqs = AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .build();
    }

    @Override
    public MakePostResponse makePost(MakePostRequest request) {
        Post post = request.getPost();
        String originatingUsername = post.getOriginatingUser().getUsername();

        //get followers paginated, send post and follower usernames as a message to another sqs queue
        boolean hasMoreFollowers = true;
        String lastFollower = null;
        List<String> followers;
        while (hasMoreFollowers) {
            Map<String, Object> result = followingDAO.getAllFollowersPaginated(originatingUsername, 25, lastFollower);
            System.out.println(result);
            hasMoreFollowers = (boolean) result.get("hasMorePages");
            followers = (List<String>) result.get("followersList");
            if (hasMoreFollowers) {
                lastFollower = followers.get(followers.size() - 1);
            }
            //queue handler will batchWrite the post to the feed table
            enqueueFeedUpdate(followers, post);
        }


//        List<String> allFollowers = followingDAO.getAllFollowerUsernames(originatingUsername);

//        batchWritePostToFeedTable(post, allFollowers);
//        writePostToStoryTable(post, originatingUsername);

        return new MakePostResponse(true);
    }

    public void enqueueFeedUpdate(List<String> followerUsernames, Post post) {
        if (!followerUsernames.isEmpty()) {
            String updateFeedQueueUrl = "https://sqs.us-west-2.amazonaws.com/765610589252/updateFeedQueue";
            Map<String, String> messageBody = new HashMap<>();
            messageBody.put("followerUsernames", gson.toJson(followerUsernames));
            messageBody.put("post", gson.toJson(post));
            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(updateFeedQueueUrl)
                    .withMessageBody(gson.toJson(messageBody))
                    .withDelaySeconds(1);
            SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
            String msgId = send_msg_result.getMessageId();
            System.out.println("Message ID: " + msgId);
        }
    }

    public void batchWritePostToFeedTable(Post post, List<String> feedOwnerUsernames) {
        List<Item> itemsToPut = new ArrayList<>();

        for (String username : feedOwnerUsernames) {
            itemsToPut.add(new Item()
                    .withPrimaryKey("username", username)
                    .withString("timestamp", post.getTimestamp())
                    .withMap("originatingUser", post.getOriginatingUser().toMap())
                    .withString("message", post.getMessage()))
            ;
        }

        try {
            TableWriteItems feedTableWriteItems = new TableWriteItems(feedTable.getTableName())
                    .withItemsToPut(itemsToPut);

            System.out.println("Making the request.");
            BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(feedTableWriteItems);
            do {

                // Check for unprocessed keys which could happen if you exceed
                // provisioned throughput

                Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();

                if (outcome.getUnprocessedItems().size() == 0) {
                    System.out.println("No unprocessed items found");
                } else {
                    System.out.println("Retrieving the unprocessed items");
                    outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
                }

            } while (outcome.getUnprocessedItems().size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writePostToFeedTable(Post post, String feedOwnerUsername) {
        try {
            PutItemOutcome outcome = feedTable.putItem(
                    new Item()
                            .withPrimaryKey("username", feedOwnerUsername)
                            .withString("timestamp", post.getTimestamp())
                            .withMap("originatingUser", post.getOriginatingUser().toMap())
                            .withString("message", post.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writePostToStoryTable(Post post, String originatingUsername) {
        try {
            PutItemOutcome outcome = storyTable.putItem(
                    new Item()
                            .withPrimaryKey("username", originatingUsername)
                            .withString("timestamp", post.getTimestamp())
                            .withMap("originatingUser", post.getOriginatingUser().toMap())
                            .withString("message", post.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUsername() != null;
        Map<String, Object> result = getFeedPaginated(request.getUsername(), request.getLimit(), request.getLastPost());
        List<Post> responsePosts = (ArrayList<Post>) result.get("feedPostList");
        boolean hasMorePages = (boolean) result.get("hasMorePages");
        return new FeedResponse(responsePosts, hasMorePages);
    }

    public Map<String, Object> getFeedPaginated(String username, int limit, Post lastPost) {
        Item item;
        Map<String, AttributeValue> lastEvaluatedKey = null;
        List<Post> feedPosts = new ArrayList<>();
        try {
            QuerySpec spec = (lastPost == null) ? new QuerySpec()
                    .withKeyConditionExpression("#u = :v_u")
                    .withNameMap(new NameMap().with("#u", "username"))
                    .withValueMap(new ValueMap().withString(":v_u", username))
                    .withScanIndexForward(true)
                    .withMaxResultSize(limit) :
                    new QuerySpec()
                            .withKeyConditionExpression("#u = :v_u")
                            .withNameMap(new NameMap().with("#u", "username"))
                            .withValueMap(new ValueMap().withString(":v_u", username))
                            .withScanIndexForward(true)
                            .withMaxResultSize(limit)
                            .withExclusiveStartKey("username", username, "timestamp", lastPost.getTimestamp());
            ItemCollection<QueryOutcome> outcome = feedTable.query(spec);
            if (outcome != null) {
                for (Item value : outcome) {
                    item = value;
                    lastEvaluatedKey = outcome.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
                    Map<String, String> userMap = (Map<String, String>) item.get("originatingUser");
                    Post post = new Post(
                            new User(
                                    userMap.get("name"),
                                    userMap.get("handle"),
                                    userMap.get("imageUrl"),
                                    userMap.get("username")
                            ),
                            (String) item.get("timestamp"),
                            (String) item.get("message")
                    );
                    feedPosts.add(post);
                    System.out.println(post.toMap());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean hasMorePages = lastEvaluatedKey != null;
        Map<String, Object> result = new HashMap<>();
        result.put("feedPostList", feedPosts);
        result.put("hasMorePages", hasMorePages);
        return result;
    }

    @Override
    public StoryResponse getStory(StoryRequest request) {
        assert request.getLimit() > 0;
        assert request.getUsername() != null;
        Map<String, Object> result = getStoryPaginated(request.getUsername(), request.getLimit(), request.getLastPost());
        List<Post> responsePosts = (ArrayList<Post>) result.get("storyPostList");
        boolean hasMorePages = (boolean) result.get("hasMorePages");
        return new StoryResponse(responsePosts, hasMorePages);
    }

    public Map<String, Object> getStoryPaginated(String username, int limit, Post lastPost) {
        Item item;
        Map<String, AttributeValue> lastEvaluatedKey = null;
        List<Post> storyPosts = new ArrayList<>();
        try {
            QuerySpec spec = (lastPost == null) ? new QuerySpec()
                    .withKeyConditionExpression("#u = :v_u")
                    .withNameMap(new NameMap().with("#u", "username"))
                    .withValueMap(new ValueMap().withString(":v_u", username))
                    .withScanIndexForward(true)
                    .withMaxResultSize(limit) :
                    new QuerySpec()
                            .withKeyConditionExpression("#u = :v_u")
                            .withNameMap(new NameMap().with("#u", "username"))
                            .withValueMap(new ValueMap().withString(":v_u", username))
                            .withScanIndexForward(true)
                            .withMaxResultSize(limit)
                            .withExclusiveStartKey("username", username, "timestamp", lastPost.getTimestamp());
            ItemCollection<QueryOutcome> outcome = storyTable.query(spec);
            if (outcome != null) {
                for (Item value : outcome) {
                    item = value;
                    lastEvaluatedKey = outcome.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
                    Map<String, String> userMap = (Map<String, String>) item.get("originatingUser");
                    Post post = new Post(
                            new User(
                                    userMap.get("name"),
                                    userMap.get("handle"),
                                    userMap.get("imageUrl"),
                                    userMap.get("username")
                            ),
                            (String) item.get("timestamp"),
                            (String) item.get("message")
                    );
                    storyPosts.add(post);
                    System.out.println(post.toMap());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean hasMorePages = lastEvaluatedKey != null;
        Map<String, Object> result = new HashMap<>();
        result.put("storyPostList", storyPosts);
        result.put("hasMorePages", hasMorePages);
        return result;
    }

    @Override
    public MakePostResponse enqueuePost(MakePostRequest request) {
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/765610589252/astric";
        String requestJson = gson.toJson(request);
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(requestJson)
                .withDelaySeconds(1);
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
        String msgId = send_msg_result.getMessageId();
        System.out.println("Message ID: " + msgId);
        return new MakePostResponse(true, "Post successfully enqueued.");
    }
}
