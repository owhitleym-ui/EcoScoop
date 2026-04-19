package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;

import edu.vassar.cmpu203.ecoscoop.R;
import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleRetriever;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedUI;
import edu.vassar.cmpu203.ecoscoop.src.view.DashboardFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.DashboardUI;
import edu.vassar.cmpu203.ecoscoop.src.view.DisplayArticleFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.DisplayArticleUI;
import edu.vassar.cmpu203.ecoscoop.src.view.MainUI;
import edu.vassar.cmpu203.ecoscoop.src.view.SearchArticleFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.SearchArticleUI;

public class ControllerActivity extends AppCompatActivity
        implements ArticleFeedUI.Listener,
                   DisplayArticleUI.Listener,
                   SearchArticleUI.Listener,
                   DashboardUI.Listener {

    private ArticleRetriever retriever;
    private List<Article> articleList;
    private Article currentArticle;
    private MainUI mainUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // clean shell — holds only fragment_container

        // Show dashboard immediately as the launch screen
        mainUI = new MainUI(getSupportFragmentManager());
        mainUI.showDashboard(R.id.fragment_container);

        // Load articles in the background; the feed will receive them when ready
        new Thread(() -> {
            try {
                retriever = new ArticleRetriever();
                articleList = retriever.articleList;
                Log.d("FeedDebug", "Article count: " + articleList.size());

                // If the user already navigated to the feed, push data to it
                runOnUiThread(() -> {
                    Fragment current = getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);
                    if (current instanceof ArticleFeedUI) {
                        onShowFeed(articleList, (ArticleFeedUI) current);
                    }
                });
            } catch (Exception e) {
                Log.e("ControllerActivity", "Failed to load articles", e);
            }
        }).start();
    }

    // -------------------------------------------------------------------------
    // Shared navigation helpers
    // -------------------------------------------------------------------------

    /** Replaces the container with a fresh ArticleFeedFragment and populates it. */
    private void showArticleFeed() {
        ArticleFeedFragment feedFragment = new ArticleFeedFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, feedFragment)
                .commitNow();
        feedFragment.setListener(this);
        if (articleList != null) {
            onShowFeed(articleList, feedFragment);
        }
    }

    // -------------------------------------------------------------------------
    // Navigation tab callbacks (shared across all fragment listeners)
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
        // TODO: replace with ProfileFragment when built
    }

    // -------------------------------------------------------------------------
    // ArticleFeedUI.Listener
    // -------------------------------------------------------------------------

    @Override
    public void onArticleClicked(int id) {
        Log.d("FeedDebug", "onArticleClicked id=" + id);

        currentArticle = retriever != null ? retriever.getArticle(id) : null;
        if (currentArticle == null) return;

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
    public void onShowFeed(List<Article> articleList, ArticleFeedUI ui) {
        ui.runShowFeed(articleList);
    }

    // -------------------------------------------------------------------------
    // DisplayArticleUI.Listener
    // -------------------------------------------------------------------------

    @Override
    public void onRequestArticle(int id, DisplayArticleUI ui) {
        if (retriever != null) {
            Article article = retriever.getArticle(id);
            if (article != null) ui.runShowArticle(article);
        }
    }

    @Override
    public void onReturnClick() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onSaveClick(int id, String folderName) {
        if (retriever != null) retriever.saveToFolder(id, folderName);
    }

    // -------------------------------------------------------------------------
    // SearchArticleUI.Listener
    // onArticleTabClick, onDashBoardClick, onProfileClick already implemented above.
    // onArticleClicked already implemented above.
    // -------------------------------------------------------------------------

    @Override
    public List<Article> onSearchQuery(String query, String type) {
        return retriever != null
                ? retriever.searchArticles(query, type)
                : List.of();
    }

    @Override
    public List<Article> onSortResults(List<Article> results, String criteria) {
        return retriever != null
                ? retriever.sortArticles(results, criteria)
                : results;
    }
}
