package edu.vassar.cmpu203.ecoscoop.src.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a news article retrieved from an RSS feed.
 * Stores the article's content, metadata, and user reactions (likes/dislikes).
 */
public class Article implements Serializable {
    private String id;
    private String title;
    private String description;
    private List<Author> authors;
    private Source source;
    private String content;
    private List<Tag> tagList;
    private String publishDate;
    private int likes;
    private int dislikes;
    private List<String> comments;
    private String userReaction = "none"; // "none", "liked", or "disliked"
    private String imageUrl;

    /**
     * Creates an article with all fields.
     *
     * @param id          unique article UUID derived from the article URL
     * @param title       article headline
     * @param description short article summary
     * @param authors     list of authors
     * @param tagList     list of topic tags
     * @param source      the source website and URL
     * @param content     full article body text
     */
    public Article(String id, String title, String description, List<Author> authors, List<Tag> tagList, Source source, String content, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.source = source;
        this.tagList = tagList;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likes = 0;
        this.dislikes = 0;
        this.comments = new ArrayList<>();
    }


    /**
     * Creates a blank default article with empty fields.
     */
    public Article() {
        this.id = "";
        this.title = "";
        this.description = "";
        this.authors = new ArrayList<Author>();
        this.source = new Source("","","");
        this.tagList = new ArrayList<Tag>();
        this.content = "";
        this.imageUrl = "";
        this.likes = 0;
        this.dislikes = 0;
        this.comments = new ArrayList<>();
    }


    /** Returns a full string representation of the article including reactions and comments. */
    @Override
    public String toString(){
       StringBuilder sb = new StringBuilder();
       sb.append(title).append("\n").append(authors).append("\n \n")
               .append((getContent())).append("\n \n")
               .append(source).append("\n").append(tagList)
               .append("\n Likes:").append(likes).append("| Dislikes: ").append(dislikes);
       if (!comments.isEmpty()){
           sb.append("\n\n--- Comments ---");
           for (int i = 0; i < comments.size(); i++) {
               sb.append("\n ").append(i + 1).append(". ").append(comments.get(i));
           }
       }

        return sb.toString();
    }

    /**
     * Returns a one-screen summary with title, authors, description, source, and tags.
     */
    public String getSummary(){
        return title + "\n" + authors + "\n \n" + wordWrap(description, 80) + "\n \n" + source.getWebsiteName() + " -- " + source.getPublishDate() + "\n" + tagList;
    }

    /**
     * Returns the article body with inline whitespace normalized and paragraph
     * breaks preserved as exactly one blank line (\n\n).
     * Android's TextView handles word wrapping within each paragraph naturally.
     */
    public String getContent() {
        return content
                .replaceAll("&nbsp;", " ")
                .replaceAll("[ \t]+", " ")       // collapse inline spaces/tabs only
                .replaceAll(" *\\n *", "\n")     // strip spaces flanking newlines
                .replaceAll("\\n{3,}", "\n\n")   // cap to one blank line max
                .trim();
    }

    /**
     * Toggles the like reaction.
     * If already liked, removes the like. If disliked, switches to liked.
     */
    public void addLike() {
        if ("liked".equals(userReaction)) {
            likes--;
            userReaction = "none";
        } else {
            if ("disliked".equals(userReaction)) dislikes--;
            likes++;
            userReaction = "liked";
        }
    }

    /**
     * Toggles the dislike reaction.
     * If already disliked, removes the dislike. If liked, switches to disliked.
     */
    public void addDislike() {
        if ("disliked".equals(userReaction)) {
            dislikes--;
            userReaction = "none";
        } else {
            if ("liked".equals(userReaction)) likes--;
            dislikes++;
            userReaction = "disliked";
        }
    }

    /** Returns the current user reaction: "liked", "disliked", or "none". */
    public String getUserReaction() { return userReaction; }

    /**
     * Adds a user comment to this article.
     *
     * @param comment the comment text
     */
    public void addComment(String comment) {
        this.comments.add(comment);
    }

    /** Returns all comments left on this article. */
    public List<String> getComments() {
        return comments;
    }

    /** Inserts line breaks so no line exceeds {@code lineWidth} characters. */
    private String wordWrap(String text, int lineWidth) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split(" ");
        int currentLineLength = 0;

        for (String word : words) {
            if (word.isEmpty()) continue;

            if (currentLineLength + word.length() + 1 > lineWidth && currentLineLength > 0) {
                result.append(System.lineSeparator());
                currentLineLength = 0;
            }

            if (currentLineLength > 0) {
                result.append(" ");
                currentLineLength++;
            }

            result.append(word);
            currentLineLength += word.length();
        }

        return result.toString();
    }
    /** Serializes this article to a Firestore-compatible map. */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("description", description);
        map.put("content", content);
        map.put("imageUrl", imageUrl != null ? imageUrl : "");
        List<String> authorNames = new ArrayList<>();
        for (Author a : authors) authorNames.add(a.getName());
        map.put("authors", authorNames);
        List<String> tagNames = new ArrayList<>();
        for (Tag t : tagList) tagNames.add(t.getName());
        map.put("tags", tagNames);
        map.put("websiteName", source.getWebsiteName());
        map.put("articleUrl", source.getUrl());
        map.put("publishDate", source.getPublishDate());
        return map;
    }

    /** Reconstructs an Article from a Firestore document map. */
    public static Article fromMap(Map<String, Object> map) {
        String id = (String) map.getOrDefault("id", "");
        String title = (String) map.getOrDefault("title", "");
        String description = (String) map.getOrDefault("description", "");
        String content = (String) map.getOrDefault("content", "");
        String imageUrl = (String) map.getOrDefault("imageUrl", "");
        String websiteName = (String) map.getOrDefault("websiteName", "");
        String articleUrl = (String) map.getOrDefault("articleUrl", "");
        String publishDate = (String) map.getOrDefault("publishDate", "");

        List<Author> authors = new ArrayList<>();
        Object authorList = map.get("authors");
        if (authorList instanceof List) {
            for (Object name : (List<?>) authorList) {
                if (name instanceof String) authors.add(new Author((String) name));
            }
        }

        List<Tag> tags = new ArrayList<>();
        Object tagList = map.get("tags");
        if (tagList instanceof List) {
            for (Object name : (List<?>) tagList) {
                if (name instanceof String) tags.add(new Tag((String) name));
            }
        }

        return new Article(id, title, description, authors, tags,
                new Source(websiteName, articleUrl, publishDate), content, imageUrl);
    }

    /** Returns the unique article UUID derived from the article URL. */
    public String getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public List<Author> getAuthors(){
        return authors;
    }

    public List<Tag> getTagList(){
        return tagList;
    }

    public Source getSource(){
        return source;
    }

    /** Returns the article's header image URL, or empty string if none. */
    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    /**
     * Returns the number of likes this article has received.
     */
    public int getLikes() {
        return likes;
    }

    /**
     * Returns the number of dislikes this article has received.
     */
    public int getDislikes() {
        return dislikes;
    }
}
