package astric.model.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a user in the system.
 */
public class User implements Comparable<User> {

    private String username;
    private String name;
    private String alias;
    private String imageUrl;

    public User(String name, String alias, String imageUrl, String username) {
        this.name = name;
        this.alias = alias;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    protected User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return alias.equals(user.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }

    @Override
    public int compareTo(User user) {
        return this.getAlias().compareTo(user.getAlias());
    }

    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(){{
            this.put("username", username);
            this.put("name", name);
            this.put("handle", alias);
            this.put("imageUrl", imageUrl);
        }};
    }
}
