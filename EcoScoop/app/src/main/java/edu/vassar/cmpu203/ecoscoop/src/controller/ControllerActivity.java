package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.vassar.cmpu203.ecoscoop.R;
import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleRetriever;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedUI;
import edu.vassar.cmpu203.ecoscoop.src.view.DisplayArticleFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.DisplayArticleUI;
import edu.vassar.cmpu203.ecoscoop.src.view.MainUI;

public class ControllerActivity extends AppCompatActivity
        implements ArticleFeedUI.Listener, DisplayArticleUI.Listener {

    // Private Fields
    private ArticleRetriever retriever;
    private List<Article> articleList;
    private Article currentArticle;
    private MainUI mainUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_controller.xml is the shell layout containing fragment_container
        // NOT a fragment layout — the activity holds the container, fragments go inside it
        setContentView(R.layout.fragment_article_feed);

        new Thread(() -> {
            try {
                retriever = new ArticleRetriever();
                articleList = retriever.articleList;
                Log.d("FeedDebug", "Article count: " + articleList.size());

                runOnUiThread(() -> {
                    ArticleFeedFragment feedFragment = new ArticleFeedFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, feedFragment)
                            .commitNow(); // commitNow so fragment is fully attached before onShowFeed
                    feedFragment.setListener(this);
                    onShowFeed(articleList, feedFragment);
                });

            } catch (Exception e) {
                Log.e("ControllerActivity", "Failed to load articles", e);
                runOnUiThread(this::finish);
            }
        }).start();
    }

    // -------------------------------------------------------------------------
    // Navigation tab methods
    // -------------------------------------------------------------------------

    @Override
    public void onArticleTabClick() {
        // Pop back to the feed, clearing any detail fragments on the stack
        getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onDashBoardClick() {
        // TODO: replace with DashboardFragment when built
    }

    @Override
    public void onSearchClick() {
        // TODO: replace with SearchFragment when built
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
        Log.d("FeedDebug", "onArticleClicked fired, id: " + id);

        currentArticle = retriever.getArticle(id);
        Log.d("FeedDebug", "article retrieved: " + currentArticle);

        if (currentArticle == null) {
            Log.d("FeedDebug", "article was null, returning");
            return;
        }

        Bundle args = new Bundle();
        args.putInt("article_id", id);

        DisplayArticleFragment detailFragment = new DisplayArticleFragment();
        detailFragment.setArguments(args);

        Log.d("FeedDebug", "committing fragment transaction");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
        Log.d("FeedDebug", "transaction committed");
    }

    @Override
    public void onShowFeed(List<Article> articleList, ArticleFeedUI ui) {
        ui.runShowFeed(articleList);
    }

    // -------------------------------------------------------------------------
    // DisplayArticleUI.Listener
    // -------------------------------------------------------------------------

    // Called by DisplayArticleFragment once its view is ready to receive data
    @Override
    public void onRequestArticle(int id, DisplayArticleUI ui) {
        Article article = retriever.getArticle(id);
        if (article != null) {
            ui.runShowArticle(article);
        }
    }

    @Override
    public void onReturnClick() {
        // Mirrors case 0: running = false — pops detail fragment, returns to feed
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onSaveClick(int id, String folderName) {
        retriever.saveToFolder(id, folderName);
    }
}