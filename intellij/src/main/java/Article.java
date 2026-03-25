import java.time.LocalDateTime;
import java.util.ArrayList;

public class Article {
    private int  id;
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
        return title;
    }


}
