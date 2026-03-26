import java.util.ArrayList;

public class Folder {

    private String name;
    private ArrayList<Integer> articleIds;
    private ArticleRetriever retriever;

    public Folder(String name, ArticleRetriever retriever) {
        this.name = name;
        this.retriever = retriever;
        this.articleIds = new ArrayList<>();
    }

    public String getFolderName() {
        return name;
    }

    public void rename(String newName) {
        if (newName == null) {
            throw new IllegalArgumentException("Folder name cannot be blank.");
        }
        this.name = newName;
    }

    public void addArticle(int id) {
        if (retriever.getArticle(id) == null) {
            throw new IllegalArgumentException("Article ID not found in database.");
        }
        if (!articleIds.contains(id)) {
            articleIds.add(id);
        }
    }

    public void removeArticle(int id) {
        articleIds.remove((Integer) id);
    }

    public ArrayList<Article> open() {
        ArrayList<Article> contents = new ArrayList<>();

        for (int id : articleIds) {
            Article a = retriever.getArticle(id);
            if (a != null) {
                contents.add(a);
            }
        }

        return contents;
    }
}