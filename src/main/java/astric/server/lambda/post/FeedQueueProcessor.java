package astric.server.lambda.post;

import astric.model.domain.Post;
import astric.model.service.PostService;
import astric.model.service.request.post.MakePostRequest;
import astric.server.service.PostServiceImpl;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FeedQueueProcessor implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        PostService postService = new PostServiceImpl();
        Gson gson = new Gson();

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println(msg.getBody());
            Map<String, Object> map = gson.fromJson(msg.getBody(), new TypeToken<HashMap<String, Object>>() {}.getType());
            Post post = gson.fromJson((String)map.get("post"),Post.class);
            List<String> followerUsernames = gson.fromJson((String)map.get("followerUsernames"),List.class);

            System.out.println("Post: " + post.toMap());
            System.out.println("Followers: " + Arrays.toString(followerUsernames.toArray()));
            postService.updateFeeds(followerUsernames, post);
        }
        return null;
    }
}