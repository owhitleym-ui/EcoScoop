package edu.vassar.cmpu203.ecoscoop.src.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to represent a user in the application.
 */
public class User implements Serializable {

    private static final String USERNAME = "username";
    private static final String AUTHKEY = "authkey";
    private static final String COMMENTS = "comments";

    private String username;
    private AuthKey authKey;
    private List<String> userComments = new ArrayList<>();

    /**
     * Creates an empty user.
     * Useful for fromMap() serialization.
     */
    public User() {}

    /**
     * Creates a new user with a username and password.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public User(final String username, final String password) {
        this.username = username;
        this.authKey = new AuthKey(password);
    }

    /**
     * Returns the user's username.
     *
     * @return the user's username.
     */
    public String getUsername() { return this.username; }

    public void addComment(String comment) { userComments.add(comment); }

    public List<String> getComments() { return Collections.unmodifiableList(userComments); }

    /**
     * Checks whether the argument password matches against the stored key.
     *
     * @param password the password to validate.
     * @return true if the password is valid, false otherwise.
     */
    public boolean validatePassword(String password) {
        return this.authKey.validatePassword(password);
    }

    /**
     * Converts this User into a key-value map.
     *
     * @return a map representation of this User.
     */
    @NonNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(USERNAME, this.username);
        map.put(AUTHKEY, this.authKey.toMap());
        map.put(COMMENTS, new ArrayList<>(userComments));
        return map;
    }

    /**
     * Builds and returns a User from a key-value map.
     *
     * @param map the map to build from.
     * @return a User containing the same information as the map.
     */
    @NonNull
    public static User fromMap(Map<String, Object> map) {
        User user = new User();
        user.username = (String) map.get(USERNAME);
        user.authKey = AuthKey.fromMap((Map<String, Object>) map.get(AUTHKEY));
        Object commentsObj = map.get(COMMENTS);
        if (commentsObj instanceof List) {
            for (Object c : (List<?>) commentsObj) {
                if (c instanceof String) user.userComments.add((String) c);
            }
        }
        return user;
    }
}
