package astric.server.dao;

import astric.model.dao.PostDAO;
import astric.model.dao.StoryDAO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryDAOImpl implements StoryDAO {

    AmazonDynamoDB client;
    DynamoDB dynamoDB;
    Table storyTable;
    UserDAOImpl userDAO;
    FollowingDAOImpl followingDAO;

    public StoryDAOImpl() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        this.dynamoDB = new DynamoDB(client);
        this.storyTable = dynamoDB.getTable("Story");
        this.userDAO = new UserDAOImpl();
        this.followingDAO = new FollowingDAOImpl();
    }


    public void writePostToStoryTable(Post post, String originatingUsername) {
        try {
            PutItemOutcome outcome = storyTable.putItem(
                    new Item()
                            .withPrimaryKey("username", originatingUsername)
                            .withString("timestamp", post.getTimestamp())
                            .withMap("originatingUser",post.getOriginatingUser().toMap())
                            .withString("message",post.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MakePostResponse writePostToStory(MakePostRequest request) {
        writePostToStoryTable(request.getPost(), request.getPost().getOriginatingUser().getUsername());
        return new MakePostResponse(true, "Post added to story");
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
        try{
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
}
