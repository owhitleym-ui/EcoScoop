package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a news article retrieved from an RSS feed.
 * Stores the article's content, metadata, and user reactions (likes/dislikes).
 */
public class Article {
    private int id;
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

    /**
     * Creates an article with all fields.
     *
     * @param id          unique article ID
     * @param title       article headline
     * @param description short article summary
     * @param authors     list of authors
     * @param tagList     list of topic tags
     * @param source      the source website and URL
     * @param content     full article body text
     */
    public Article(int id, String title, String description, List<Author> authors, List<Tag> tagList, Source source, String content) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.source = source;
        this.tagList = tagList;
        this.content = content;
        this.likes = 0;
        this.dislikes = 0;
        this.comments = new ArrayList<>();
    }


    /**
     * Creates a blank default article with empty fields.
     */
    public Article() {
        this.id = 0;
        this.title = "";
        this.description = "";
        this.authors = new ArrayList<Author>();
        this.source = new Source("","","");
        this.tagList = new ArrayList<Tag>();
        this.content = "";
        this.likes = 0;
        this.dislikes = 0;
        this.comments = new ArrayList<>();
    }

    @Override
    public String toString(){
        return "Title: " + title + "\n Description: " + description + "\n Authors:" + authors + "\n model.Source:" + source + "\n Tags: " + tagList + "\n Content: " + getContent();
    }

    /**
     * Returns a formatted string for displaying the full article with reactions.
     */
    public String printArticle(){
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n").append(authors).append("\n \n")
          .append(getContent()).append("\n \n")
          .append(source).append("\n").append(tagList)
          .append("\n Likes: ").append(likes).append(" | Dislikes: ").append(dislikes);
        if (!comments.isEmpty()) {
            sb.append("\n\n--- Comments ---");
            for (int i = 0; i < comments.size(); i++) {
                sb.append("\n ").append(i + 1).append(". ").append(comments.get(i));
            }
        }
        return sb.toString();
    }

    /**
     * Returns a one-screen summary with ID, title, authors, description, source, and tags.
     */
    public String getSummary(){
        return "ID: " + id + " " + title + "\n" + authors + "\n \n" + wordWrap(description, 80) + "\n \n" + source.getWebsiteName() + " -- " + source.getPublishDate() + "\n" + tagList;
    }

    /**
     * Returns the article body with no HTML entities and word-wrapped at 80 characters.
     */
    public String getContent() {
        String cleaned = content.replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return wordWrap(cleaned, 80);
    }

    /**
     * Increments like count by one.
     */
    public void addLike() {
        this.likes++;
    }

    /**
     * Increments dislike count by one.
     */
    public void addDislike() {
        this.dislikes++;
    }

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

    /**
     * Wraps text at a certain lineWidth
     * @param text
     * @param lineWidth
     * @return
     */
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

    /**
     * Returns the unique article ID assigned during parsing.
     */
    public int getId() {
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
