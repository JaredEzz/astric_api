package astric.model.service.request.post;

import astric.model.domain.Post;

public class StoryRequest {
    private int limit;
    private String username;
    private Post lastPost;
    private String authToken;

    private StoryRequest(){}

    public StoryRequest(int limit, String username, Post lastPost, String authToken) {
        this.limit = limit;
        this.username = username;
        this.lastPost = lastPost;
        this.authToken = authToken;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Post getLastPost() {
        return lastPost;
    }

    public void setLastPost(Post lastPost) {
        this.lastPost = lastPost;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
