package astric.model.service.request.post;

import astric.model.domain.Post;

public class MakePostRequest {
    private Post post;
    private String authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private MakePostRequest() {}

    /**
     * Creates an instance.
     *
     */
    public MakePostRequest(Post post, String authToken) {
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
