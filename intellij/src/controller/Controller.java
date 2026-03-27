package controller;

import model.Article;
import model.ArticleRetriever;
import view.CmdLineUI;
import view.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * The main controller for EcoScoop.
 * Sits between the UI and the data layer — the UI calls listener methods
 * on the Controller, and the Controller calls run methods back on the UI.
 */
public class Controller implements UI.Listener{

    //Private Fields
    private UI ui;
    private final ArticleRetriever retriever = new ArticleRetriever();
    private final List<Article> articleList = retriever.articleList;
    private Article currentArticle = new Article();

    /**
     * Creates the controller and registers it as the UI's listener.
     *
     * @param ui the UI implementation to use
     * @throws Exception if loading the article feeds fails
     */
    private Controller(final UI ui) throws Exception {
        this.ui = ui;
        this.ui.setListener(this);
    }

    /** Starts the application. */
    public static void main(String[] args) throws Exception {
        final UI ui = new CmdLineUI();
        final Controller controller = new Controller(ui);
        controller.run();
    }

    // Main Run Method
    private void run(){
        this.ui.runMainMenu();
    }

    // Listener - Article Display Methods

    /**
     * Gets the current article the user has selected and calls for displaying
     *
     * @param id
     */
    @Override
    public void onGetArticle(int id){
        currentArticle = retriever.getArticle(id);
        if (currentArticle == null) {
            return;
        }
        onDisplayArticle(currentArticle);
    }

    @Override
    public int getArticleCount() {
        return articleList.size();
    }

    @Override
    public void onDisplayArticle(Article article){
        this.ui.runDisplayArticle(article);
    }

    @Override
    public void onChooseArticle() {
        this.ui.runChooseArticle();
    }

    @Override
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
    public List<Article> onSearchQuery(String query, String type) {
        return retriever.searchArticles(query, type);
    }

    @Override
    public List<Article> onSortResults(List<Article> results, String criteria) {
        return retriever.sortArticles(results, criteria);
    }

    // Listener - Folder Methods
    @Override
    public void onSaveToFolder(int articleId, String folderName) {
        retriever.saveToFolder(articleId, folderName);
    }

    // Listener - React Methods
    @Override
    public void onLikeArticle(int id) {
        Article article = retriever.getArticle(id);
        if (article != null) article.addLike();
    }

    @Override
    public void onDislikeArticle(int id) {
        Article article = retriever.getArticle(id);
        if (article != null) article.addDislike();
    }

    @Override
    public void onCommentArticle(int id, String comment) {
        Article article = retriever.getArticle(id);
        if (article != null) article.addComment(comment);
    }

}
