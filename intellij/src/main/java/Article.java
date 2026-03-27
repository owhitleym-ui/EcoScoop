import java.util.ArrayList;

/**
 * Represents a news article retrieved from an RSS feed.
 * Stores the article's content, metadata, and user reactions (likes/dislikes).
 */
public class Article {
    private int id;
    private String title;
    private String description;
    private ArrayList<Author> authors;
    private Source source;
    private String content;
    private ArrayList<Tag> tagList;
    private String publishDate;
    private int likes;
    private int dislikes;

    /**
     * Creates a fully populated article with all fields.
     *
     * @param id          unique article ID
     * @param title       article headline
     * @param description short article summary
     * @param authors     list of authors
     * @param tagList     list of topic tags
     * @param source      the source website and URL
     * @param content     full article body text
     */
    public Article(int id, String title, String description, ArrayList<Author> authors, ArrayList<Tag> tagList, Source source, String content) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.source = source;
        this.tagList = tagList;
        this.content = content;
        this.likes = 0;
        this.dislikes = 0;
    }

    /**
     * Creates an article without an id or content (legacy constructor).
     */
    public Article(String title, String url, String description, ArrayList<Tag> tagList, ArrayList<Author> authors, Source source) {
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.source = source;
        this.tagList = tagList;
        this.likes = 0;
        this.dislikes = 0;
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
    }

    @Override
    public String toString(){
        return "Title: " + title + "\n Description: " + description + "\n Authors:" + authors + "\n Source:" + source + "\n Tags: " + tagList + "\n Content: " + getContent();
    }

    /**
     * Returns a formatted string for displaying the full article with reactions.
     */
    public String printArticle(){
        return "" + title + "\n" + authors + "\n \n" + getContent() + "\n \n" + source + "\n" + tagList + "\n Likes: " + likes + " | Dislikes: " + dislikes;
    }

    /**
     * Returns a short one-screen summary with ID, title, authors, description, source, and tags.
     */
    public String getSummary(){
        return "ID: " + id + " " + title + "\n" + authors + "\n \n" + wordWrap(description, 80) + "\n \n" + source.getWebsiteName() + " -- " + source.getPublishDate() + "\n" + tagList;
    }

    /**
     * Returns the article body cleaned of HTML entities and word-wrapped at 80 characters.
     */
    public String getContent() {
        String cleaned = content.replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return wordWrap(cleaned, 80);
    }

    /**
     * Increments the like count by one.
     */
    public void addLike() {
        this.likes++;
    }

    /**
     * Increments the dislike count by one.
     */
    public void addDislike() {
        this.dislikes++;
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

    /**
     * Returns true if the article's title, description, authors, or tags contain
     * the given query string (case-insensitive).
     *
     * @param query the search string, already lowercased by the caller
     */
    public boolean matchesSearch(String query) {
        if (title != null && title.toLowerCase().contains(query)) return true;
        if (description != null && description.toLowerCase().contains(query)) return true;
        for (Author a : authors) {
            if (a.getName() != null && a.getName().toLowerCase().contains(query)) return true;
        }
        for (Tag t : tagList) {
            if (t.getName() != null && t.getName().toLowerCase().contains(query)) return true;
        }
        return false;
    }

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
}
