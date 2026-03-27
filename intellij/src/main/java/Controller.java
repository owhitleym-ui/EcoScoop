import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;

public class Controller implements UI.Listener{

    //Private Fields
    private UI ui;
    private final ArticleRetriever retriever = new ArticleRetriever();
    private final ArrayList<Article> articleList = retriever.articleList;
    private Article currentArticle = new Article();

    private Controller(final UI ui) throws Exception {
        this.ui = ui;
        this.ui.setListener(this);
    }

    public static void main(String[] args) throws Exception {
        final UI ui = new CmdLineUI();
        final Controller controller = new Controller(ui);
        controller.run();
    }

    // Main Run Method
    private void run(){
        this.ui.runMainMenu();
    }

    //Listener -  Article Display Methods

    @Override
    public void onGetArticle(int id){
        currentArticle = retriever.getArticle(id);
        onDisplayArticle(currentArticle);
    }

    @Override
    public void onDisplayArticle(Article article){
        this.ui.runDisplayArticle(article);
    }

    @Override
    public void onChooseArticle() {
        this.ui.runChooseArticle();
    }

    public void onViewArticleTab(){
        this.ui.runArticleTab();
    }

    @Override
    public void onDisplayArticleList() {
        this.ui.runDisplayArticleList(articleList);
    }

    // Listener - Article Search Methods
    @Override
    public void onSearchArticles() {
        this.ui.runSearchArticles();
    }

    @Override
    public ArrayList<Article> onSearchQuery(String query, String type) {
        return retriever.searchArticles(query, type);
    }

    @Override
    public ArrayList<Article> onSortResults(ArrayList<Article> results, String criteria) {
        return retriever.sortArticles(results, criteria);
    }

}
