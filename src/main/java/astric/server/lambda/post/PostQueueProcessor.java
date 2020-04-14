package astric.server.lambda.post;

import astric.model.service.PostService;
import astric.model.service.request.post.MakePostRequest;
import astric.server.service.PostServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;

import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;


public class PostQueueProcessor implements RequestHandler<SQSEvent, Void> {

    @Override

    public Void handleRequest(SQSEvent event, Context context) {
        PostService postService = new PostServiceImpl();
        Gson gson = new Gson();

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            MakePostRequest request = gson.fromJson(msg.getBody(), MakePostRequest.class);
            // Add code to print message body to the log
            postService.makePost(request);
        }

        return null;

    }

}