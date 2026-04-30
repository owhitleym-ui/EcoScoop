package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleDatabase;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleRepository;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;
import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.persistence.PersistenceFacade;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedUI;
import edu.vassar.cmpu203.ecoscoop.src.view.DashboardFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.DashboardUI;
import edu.vassar.cmpu203.ecoscoop.src.view.DisplayArticleFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.DisplayArticleUI;
import edu.vassar.cmpu203.ecoscoop.src.view.MainUI;
import edu.vassar.cmpu203.ecoscoop.src.view.ProfileFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.ProfileUI;
import edu.vassar.cmpu203.ecoscoop.src.view.SearchArticleFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.SearchArticleUI;

public class ControllerActivity extends AppCompatActivity
        implements ArticleFeedUI.Listener,
                   DisplayArticleUI.Listener,
                   SearchArticleUI.Listener,
                   DashboardUI.Listener,
                   ProfileUI.Listener {

    private ArticleRetriever articleRetriever;
    private FolderManager folderManager;
    private Article currArticle;
    //private PersistenceFacade persistenceFacade;
    private MainUI mainUI;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        this.mainUI = new MainUI(this);
        super.onCreate(savedInstanceState);
        setContentView(mainUI.getRootView());

        DashboardFragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setListener(this);
        mainUI.displayFragment(dashboardFragment);

        onUpdateDatabase();

    }

    /** Fetch and Updates Database on a background thread; update the feed when ready */
    private void onUpdateDatabase(){
        new Thread( () ->{
            try {
                ArticleDatabase newDatabase = new ArticleRepository();

                Log.d("ControllerActivity", "Loaded " + newDatabase.getDatabase().size() + " articles");

                runOnUiThread(() -> {
                    onLoadArticles(newDatabase);
                });
            } catch (Exception e) {
                Log.e("ControllerActivity", "Failed to load articles", e);
            }
        }
        ).start();
    }

    /** Loads updated Database into the Retriever and FolderManager */
    private void onLoadArticles(ArticleDatabase newDatabase){
        this.articleRetriever = new ArticleRetriever(newDatabase);
        this.folderManager.updateRetreiver(articleRetriever);

        Log.d("FeedDebug", "Article Size:" + articleRetriever.getDatabaseSize());
        ArticleFeedFragment newFeed = new ArticleFeedFragment();
        newFeed.setListener(this);
        mainUI.displayFragment(newFeed);

        onShowFeed(articleRetriever.returnDatabase(), newFeed);

    }


    /**
     * Navigation Helpers - For Documentation and Ease of Reading
     */

    private void showArticleFeedTab(){
        ArticleFeedFragment feedFragment = new ArticleFeedFragment();
        feedFragment.setListener(this);
        mainUI.displayFragment(feedFragment);

        if(articleRetriever.getDatabaseSize() != 0){
            onShowFeed(articleRetriever.returnDatabase(), feedFragment);
        }
    }

    private void showDashBoardTab(){
        DashboardFragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setListener(this);
        if(mainUI != null){
            mainUI.displayFragment(dashboardFragment);
        }
    }

    private void showSearchTab(){
        SearchArticleFragment searchArticleFragment = new SearchArticleFragment();
        searchArticleFragment.setListener(this);
        if(mainUI != null){
            mainUI.displayFragment(searchArticleFragment);
        }
    }

    private void showProfileTab(){
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setListener(this);
        if(mainUI != null){
            mainUI.displayFragment(profileFragment);
        }
    }


    /**
     * Navigation Tab Implementation
     */

    @Override
    public void onArticleTabClick(){
        showArticleFeedTab();
    }

    @Override
    public void onDashBoardClick(){
        showDashBoardTab();
    }

    @Override
    public void onSearchClick(){
        showSearchTab();
    }

    @Override
    public void onProfileClick(){
        showProfileTab();
    }


    /**
     * ArticleFeedUI.Listener Implementations
     */

    @Override
    public void onArticleClicked(int id){
        if (articleRetriever.getArticle(id) == null) return;

        Bundle args = new Bundle();
        args.putInt("article_id", id);

        DisplayArticleFragment displayArticleFragment = new DisplayArticleFragment();
        displayArticleFragment.setListener(this);
        displayArticleFragment.setArguments(args);

        mainUI.displayFragment(displayArticleFragment);
    }

    @Override
    public void onShowFeed(List<Article> articles, ArticleFeedUI ui) {
        ui.runShowFeed(articles);
    }

    /**
     * DisplayArticleUI.Listener Implementations
     */

    @Override
    public void onRequestArticle(int id, DisplayArticleUI ui) {
        if (articleRetriever != null && articleRetriever.getArticle(id) != null){
            currArticle = articleRetriever.getArticle(id);
            ui.runShowArticle(currArticle);
        }
    }

    @Override
    public void onReturnClick(){
        currArticle = null;
        onArticleTabClick();
    }

    @Override
    public void onSaveClick(int id, String folderName){
        if (folderManager != null) folderManager.saveToFolder(id, folderName);
    }

    @Override
    public void onLikeClick(int id) {
        if (currArticle != null) currArticle.addLike();
    }

    @Override
    public void onDislikeClick(int id) {
        if (currArticle != null) currArticle.addDislike();
    }

    @Override
    public void onCommentSubmit(int id, String comment) {
        if (currArticle != null) currArticle.addComment(comment);
    }

    /**
     * SearchArticleUI.Listener Implementations
     */

    @Override
    public void onSearchQuery(String query, String type, SearchArticleUI ui) {
        List<Article> results = articleRetriever != null
                ? articleRetriever.searchArticles(query, type)
                : new ArrayList<>();
        ui.runShowResults(results);
    }

    @Override
    public void onSortResults(List<Article> results, String criteria, SearchArticleUI ui) {
        List<Article> sorted = articleRetriever != null
                ? articleRetriever.sortArticles(results, criteria)
                : results;
        ui.runShowResults(sorted);
    }

    /**
     * ProfileUI.Listener Implementations
     */

    @Override
    public List<Article> onGetSavedArticles() {
        if (folderManager == null) return new ArrayList<>();
        List<Article> saved = new ArrayList<>();
        for (Folder folder : folderManager.getFolders()) {
            saved.addAll(folder.open());
        }
        return saved;
    }




}
