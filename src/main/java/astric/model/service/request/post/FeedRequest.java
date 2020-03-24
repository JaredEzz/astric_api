package astric.model.service.request.post;

import astric.model.domain.Post;
import astric.model.domain.User;

public class FeedRequest {
    private int limit;
    private User user;
    private Post lastPost;

    private FeedRequest(){}

    public FeedRequest(int limit, User user, Post lastPost) {
        this.limit = limit;
        this.user = user;
        this.lastPost = lastPost;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getLastPost() {
        return lastPost;
    }

    public void setLastPost(Post lastPost) {
        this.lastPost = lastPost;
    }
}
