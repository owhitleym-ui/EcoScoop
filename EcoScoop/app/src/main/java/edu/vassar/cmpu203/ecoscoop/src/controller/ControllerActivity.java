package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.ecoscoop.R;
import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleDatabase;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleRepository;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleRetriever;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;
import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedUI;
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

    private ArticleDatabase  articleDatabase;   // RSS data source
    private ArticleRetriever articleRetriever; // search and sort
    private FolderManager   folderManager;     // saved folders (independent of retrieval)
    private Article         currentArticle;    // article currently open in the detail view
    private MainUI mainUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainUI = new MainUI(getSupportFragmentManager());
        mainUI.showDashboard(R.id.fragment_container);

        // Fetch articles on a background thread; update the feed when ready
        new Thread(() -> {
            try {
                articleDatabase  = new ArticleRepository(); // fetches RSS in constructor
                articleRetriever = new ArticleRetriever(articleDatabase);
                folderManager    = new FolderManager(articleDatabase);
                Log.d("ControllerActivity", "Loaded " + articleDatabase.getArticles().size() + " articles");

                runOnUiThread(() -> {
                    Fragment current = getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);
                    if (current instanceof ArticleFeedUI) {
                        onShowFeed(articleDatabase.getArticles(), (ArticleFeedUI) current);
                    }
                });
            } catch (Exception e) {
                Log.e("ControllerActivity", "Failed to load articles", e);
            }
        }).start();
    }

    // -------------------------------------------------------------------------
    // Navigation helpers
    // -------------------------------------------------------------------------

    /** Replaces the container with a fresh ArticleFeedFragment and populates it. */
    private void showArticleFeed() {
        ArticleFeedFragment feedFragment = new ArticleFeedFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, feedFragment)
                .commitNow();
        feedFragment.setListener(this);
        if (articleDatabase != null) {
            onShowFeed(articleDatabase.getArticles(), feedFragment);
        }
    }

    // -------------------------------------------------------------------------
    // Shared nav tab callbacks
    // -------------------------------------------------------------------------

    @Override
    public void onArticleTabClick() {
        getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        showArticleFeed();
    }

    @Override
    public void onDashBoardClick() {
        getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainUI.showDashboard(R.id.fragment_container);
    }

    @Override
    public void onSearchClick() {
        getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SearchArticleFragment())
                .commitNow();
    }

    @Override
    public void onProfileClick() {
        getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileFragment())
                .commitNow();
    }

    // -------------------------------------------------------------------------
    // ArticleFeedUI.Listener
    // -------------------------------------------------------------------------

    @Override
    public void onArticleClicked(int id) {
        if (articleRetriever == null || articleRetriever.getArticle(id) == null) return;

        Bundle args = new Bundle();
        args.putInt("article_id", id);

        DisplayArticleFragment detailFragment = new DisplayArticleFragment();
        detailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onShowFeed(List<Article> articles, ArticleFeedUI ui) {
        ui.runShowFeed(articles);
    }

    // -------------------------------------------------------------------------
    // DisplayArticleUI.Listener
    // -------------------------------------------------------------------------

    @Override
    public void onRequestArticle(int id, DisplayArticleUI ui) {
        if (articleRetriever != null) {
            currentArticle = articleRetriever.getArticle(id);
            if (currentArticle != null) ui.runShowArticle(currentArticle);
        }
    }

    @Override
    public void onReturnClick() {
        currentArticle = null;
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onSaveClick(int id, String folderName) {
        if (folderManager != null) folderManager.saveToFolder(id, folderName);
    }

    @Override
    public void onLikeClick(int id) {
        if (currentArticle != null) currentArticle.addLike();
    }

    @Override
    public void onDislikeClick(int id) {
        if (currentArticle != null) currentArticle.addDislike();
    }

    @Override
    public void onCommentSubmit(int id, String comment) {
        if (currentArticle != null) currentArticle.addComment(comment);
    }

    // -------------------------------------------------------------------------
    // SearchArticleUI.Listener — controller pushes results back to the UI
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // ProfileUI.Listener
    // -------------------------------------------------------------------------

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
