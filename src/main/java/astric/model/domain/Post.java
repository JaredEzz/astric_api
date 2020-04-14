package astric.model.domain;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private User originatingUser;
    private String timestamp;
    private String message;

    Post(){}

    public Post(User originatingUser, String timestamp, String message) {
        this.originatingUser = originatingUser;
        this.timestamp = timestamp;
        this.message = message;
    }

    public User getOriginatingUser() {
        return originatingUser;
    }

    public void setOriginatingUser(User originatingUser) {
        this.originatingUser = originatingUser;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        Post comparePost = (Post) obj;
        return comparePost.timestamp.equals(this.timestamp) && comparePost.message.equals(this.message);
    }

    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(){{
            this.put("originatingUser", originatingUser.toMap());
            this.put("timestamp", timestamp);
            this.put("message", message);
        }};
    }
}
