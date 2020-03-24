package astric.model.service.response.post;
import astric.model.domain.Post;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.response.PagedResponse;

import java.util.List;

/**
 * A paged response for a {@link FeedRequest}.
 */
public class FeedResponse extends PagedResponse {
    private List<Post> posts;

    public FeedResponse(String message){
        super(false, message, false);
    }

    public FeedResponse(List<Post> posts, boolean hasMorePages) {
        super(true, hasMorePages);
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
