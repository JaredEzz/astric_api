package astric.model.service.response.post;
import astric.model.domain.Post;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.response.PagedResponse;

import java.util.List;

/**
 * A paged response for a {@link FeedRequest}.
 */
public class StoryResponse extends PagedResponse {
    private List<Post> posts;

    public StoryResponse(String message){
        super(false, message, false);
    }

    public StoryResponse(List<Post> posts, boolean hasMorePages) {
        super(true, hasMorePages);
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
