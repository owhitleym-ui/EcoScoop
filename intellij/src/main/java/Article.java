import java.time.LocalDateTime;
import java.util.ArrayList;

public class Article {
    private int id;
    private String title;
    private String description;
    private ArrayList<Author> authors;
    private Source source;
    private String content;
    private ArrayList<Tag> tagList;

    private String publishDate;

    public Article(int id, String title, String description, ArrayList<Author> authors, ArrayList<Tag> tagList, Source source, String content) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.source = source;
        this.tagList = tagList;
        this.content = content;
    }

    public Article(String title, String url, String description, ArrayList<Tag> tagList, ArrayList<Author> authors, Source source) {
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.source = source;
        this.tagList = tagList;
    }
    public Article() {
        this.id = 0;
        this.title = "";
        this.description = "";
        this.authors = new ArrayList<Author>();
        this.source = new Source("","","");
        this.tagList = new ArrayList<Tag>();
        this.content = "";
    }



    @Override
    public String toString(){
        return "Title: " + title + "\n Description: " + description + "\n Authors:" + authors + "\n Source:" + source + "\n Tags: " + tagList + "\n Content: " + getContent();
    }

    public String printArticle(){
        return "" + title + "\n" + authors + "\n \n" + getContent() + "\n \n" + source + "\n" + tagList;
    }

    public String getSummary(){
        return "ID: " + id + " " + title + "\n" + authors + "\n \n" + wordWrap(description, 80) + "\n \n" + source.getWebsiteName() + " -- " + source.getPublishDate() + "\n" + tagList;
    }
    public String getContent() {
        String cleaned = content.replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return wordWrap(cleaned, 80);
    }

    private String wordWrap(String text, int lineWidth) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split(" ");
        int currentLineLength = 0;

        for (String word : words) {
            if (word.isEmpty()) continue;

            // If adding this word exceeds the line width, start a new line
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

    public int getId() {
        return id;
    }

}
