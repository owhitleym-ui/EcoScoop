import java.time.LocalDateTime;
import java.util.ArrayList;

public class Article {
    private int  id;

    private String title;
    private String url;
    private String description;

    private ArrayList<String> category;
    private ArrayList<String> author;

    private String publishDate;

    public Article(int id, String title, String url, String description, ArrayList category, ArrayList author, String publishDate) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.description = description;
        this.category = category;
        this.author = author;
        this.publishDate = publishDate;
    }

    public Article(String title, String url, String description, ArrayList category, ArrayList author, String publishDate) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.category = category;
        this.author = author;
    }
    public Article() {
        id =0;
        title = "";
        url = "";
        description = "";
        category = new ArrayList();
        author = new ArrayList();
        publishDate = "";
    }

    public String toString(){
        return "Article " + id + ": {" + title + ", "+ url + ", " + description + ", " + category + ", "+ author + ", "+ publishDate + "}";
    }


}
