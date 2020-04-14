package astric.server.dao;

import astric.model.dao.PostDAO;
import astric.model.domain.Post;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.request.post.StoryRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;
import astric.model.service.response.post.StoryResponse;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static astric.server.dao.UserDAOImpl.hardCodedUsers;

public class PostDAOImpl implements PostDAO {

    AmazonDynamoDB client;
    DynamoDB dynamoDB;
    Table feedTable;
    UserDAOImpl userDAO;
    FollowingDAOImpl followingDAO;

    public PostDAOImpl() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        this.dynamoDB = new DynamoDB(client);
        this.feedTable = dynamoDB.getTable("Feed");
        this.userDAO = new UserDAOImpl();
        this.followingDAO = new FollowingDAOImpl();
    }

    private List<Post> postList = new ArrayList<>();


    private List<Post> hardCodedPosts = Arrays.asList(
            new Post(hardCodedUsers.get(0), "20121002", "This is a good website https://google.com @tb @birdlover"),
            new Post(hardCodedUsers.get(0), "20121002", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate"),
            new Post(hardCodedUsers.get(1), "20121002", "Tweet 3"),
            new Post(hardCodedUsers.get(2), "20121002", "Tweet 4"),
            new Post(hardCodedUsers.get(3), "20121002", "Tweet 5"),
            new Post(hardCodedUsers.get(4), "20121002", "Tweet 7"),
            new Post(hardCodedUsers.get(1), "20121002", "Tweet 6"),
            new Post(hardCodedUsers.get(5), "20121002", "I love to go backpacking")
    );

    @Override
    public MakePostResponse makePost(MakePostRequest request) {
        Post post = request.getPost();
        String originatingUsername = post.getOriginatingUser().getUsername();

        //currently just gets one, we'll want all followers when we do a batch write
        String feedOwnerUsername =
                (String) ((List)
                        followingDAO.getAllFollowersPaginated(
                                originatingUsername,
                                1,
                                "Alene").get("followersList")).get(0);

        List<String> allFollowers = followingDAO.getAllFollowerUsernames(originatingUsername);

        writePostToFeedTable(post, feedOwnerUsername);

        return new MakePostResponse(true);
    }

    public void writePostToFeedTable(Post post, String feedOwnerUsername) {
        try {
            PutItemOutcome outcome = feedTable.putItem(
                    new Item()
                            .withPrimaryKey("username", feedOwnerUsername)
                            .withString("timestamp", post.getTimestamp())
                    .withMap("originatingUser",post.getOriginatingUser().toMap())
                    .withString("message",post.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUsername() != null;

        //make a list of the posts to return based on if the user is
        //following the originating user or is the originating user

        List<Post> allPosts = hardCodedPosts; //TODO filter hardcoded posts by user/follower
        List<Post> responsePosts = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            int feedIndex = getFeedStartingIndex(request.getLastPost(), allPosts);

            for (int limitCounter = 0; feedIndex < allPosts.size() && limitCounter < request.getLimit(); feedIndex++, limitCounter++) {
                responsePosts.add(allPosts.get(feedIndex));
            }

            hasMorePages = feedIndex < allPosts.size();
        }

        return new FeedResponse(responsePosts, hasMorePages);
    }

    @Override
    public StoryResponse getStory(StoryRequest request) {
        //check user, get all posts with that user as originating user
        //paginated
        assert request.getLimit() > 0;
        assert request.getUsername() != null;

        List<Post> userPosts = hardCodedPosts.stream()
                .filter(post -> post.getOriginatingUser().getUsername()
                        .equals(request.getUsername()))
                .collect(Collectors.toList());

        List<Post> responsePosts = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            int feedIndex = getFeedStartingIndex(request.getLastPost(), userPosts);

            for (int limitCounter = 0; feedIndex < userPosts.size() && limitCounter < request.getLimit(); feedIndex++, limitCounter++) {
                responsePosts.add(userPosts.get(feedIndex));
            }

            hasMorePages = feedIndex < userPosts.size();
        }

        return new StoryResponse(responsePosts, hasMorePages);
    }

    @Override
    public MakePostResponse enqueuePost(MakePostRequest request) {
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/765610589252/astric";
        Gson gson = new Gson();
        String requestJson = gson.toJson(request);
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(requestJson)
                .withDelaySeconds(1);
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .build();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
        String msgId = send_msg_result.getMessageId();
        System.out.println("Message ID: " + msgId);
        return new MakePostResponse(true, "Post successfully enqueued.");
    }

    private int getFeedStartingIndex(Post lastPost, List<Post> allPosts){
        int feedIndex = 0;
        if (lastPost != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allPosts.size(); i++) {
                if(lastPost.equals(allPosts.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    feedIndex = i + 1;
                }
            }
        }
        return feedIndex;
    }
}
