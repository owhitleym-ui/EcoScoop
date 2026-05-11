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
    private static final String USE_METRIC = "useMetric";
    private static final String USE_LOCAL = "useLocalLocation";
    private static final String ARTICLES_READ = "articlesRead";
    private static final String ARTICLES_LIKED = "articlesLiked";
    private static final String ARTICLES_DISLIKED = "articlesDisliked";

    private String username;
    private AuthKey authKey;
    private List<String> userComments = new ArrayList<>();
    private boolean useMetric = false;
    private boolean useLocalLocation = false;
    private int articlesRead = 0;
    private int articlesLiked = 0;
    private int articlesDisliked = 0;

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
    public void removeComment(int index) { userComments.remove(index); }
    public List<String> getComments() { return Collections.unmodifiableList(userComments); }

    public boolean isUseMetric() { return useMetric; }
    public void setUseMetric(boolean v) { this.useMetric = v; }
    public boolean isUseLocalLocation() { return useLocalLocation; }
    public void setUseLocalLocation(boolean v) { this.useLocalLocation = v; }
    public int getArticlesRead() { return articlesRead; }
    public void incrementRead() { articlesRead++; }
    public int getArticlesLiked() { return articlesLiked; }
    public void incrementLiked() { articlesLiked++; }
    public int getArticlesDisliked() { return articlesDisliked; }
    public void incrementDisliked() { articlesDisliked++; }

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
        map.put(USE_METRIC, this.useMetric);
        map.put(USE_LOCAL, this.useLocalLocation);
        map.put(ARTICLES_READ, this.articlesRead);
        map.put(ARTICLES_LIKED, this.articlesLiked);
        map.put(ARTICLES_DISLIKED, this.articlesDisliked);
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
        if (map.get(USE_METRIC) instanceof Boolean) user.useMetric = (Boolean) map.get(USE_METRIC);
        if (map.get(USE_LOCAL) instanceof Boolean) user.useLocalLocation = (Boolean) map.get(USE_LOCAL);
        if (map.get(ARTICLES_READ) instanceof Long) user.articlesRead = ((Long) map.get(ARTICLES_READ)).intValue();
        if (map.get(ARTICLES_LIKED) instanceof Long) user.articlesLiked = ((Long) map.get(ARTICLES_LIKED)).intValue();
        if (map.get(ARTICLES_DISLIKED) instanceof Long) user.articlesDisliked = ((Long) map.get(ARTICLES_DISLIKED)).intValue();
        return user;
    }
}
