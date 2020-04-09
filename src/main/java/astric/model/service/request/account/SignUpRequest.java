package astric.model.service.request.account;

import astric.model.domain.User;

import java.awt.*;

public class SignUpRequest {
    private String name;
    private String username;
    private String password;
    private String handle;
    private String image;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private SignUpRequest() {}

    /**
     * Creates an instance.
     *
     */
    public SignUpRequest(String name, String username, String password, String handle, String image) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.handle = handle;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
