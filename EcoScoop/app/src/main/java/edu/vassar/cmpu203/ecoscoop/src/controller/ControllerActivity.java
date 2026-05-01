package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleDatabase;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleRepository;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;
import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.model.User;
import edu.vassar.cmpu203.ecoscoop.src.persistence.FirestoreFacade;
import edu.vassar.cmpu203.ecoscoop.src.model.WeatherRepository;
import edu.vassar.cmpu203.ecoscoop.src.persistence.PersistenceFacade;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.ArticleFeedUI;
import edu.vassar.cmpu203.ecoscoop.src.view.AuthFragment;
import edu.vassar.cmpu203.ecoscoop.src.view.AuthUI;
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
        implements AuthUI.Listener,
                   ArticleFeedUI.Listener,
                   DisplayArticleUI.Listener,
                   SearchArticleUI.Listener,
                   DashboardUI.Listener,
                   ProfileUI.Listener {

    private static final String STATE = "state";
    private static final String CUR_ARTICLE_ID = "curArticleId";
    private PersistenceFacade pfacade;
    private WeatherRetriever weatherRetriever;
    private ArticleRetriever articleRetriever;
    private FolderManager folderManager;
    private Article curArticle;
    private User curUser;

    State curState = State.AUTH;

    private MainUI mainUI;

    /**
     * An enumeration to keep track of which screen the user is currently on.
     */
    private enum State {
        AUTH("Authentication"),
        DASHBOARD("Dashboard"),
        FEED("Article Feed"),
        DISPLAY_ARTICLE("Article"),
        SEARCH("Search"),
        PROFILE("Profile");

        final String name;

        State(String name) { this.name = name; }

        @NonNull
        public String toString() { return name; }
    }

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}. Otherwise, it is null.
     */
    //Get Location
    private FusedLocationProviderClient locationClient;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        this.mainUI = new MainUI(this);
        super.onCreate(savedInstanceState);
        setContentView(mainUI.getRootView());

        this.pfacade = new FirestoreFacade();
        this.locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (savedInstanceState != null) {
            this.curState = State.valueOf(savedInstanceState.getString(STATE));
        }

        onUpdateDatabase(); // start loading articles in background while user authenticates
        requestLocation();
        onUpdateWeather(lat, lon);
        onAuth();
    }

    /**
     * Called before activity destruction to give it an opportunity to store state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE, this.curState.name());
        if (this.curArticle != null) outState.putInt(CUR_ARTICLE_ID, this.curArticle.getId());
    }

    /** Request Location of the User */
    private void requestLocation() {
        // Check permission first
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                this.lat = location.getLatitude();
                this.lon = location.getLongitude();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation(); // permission granted, try again
        }
    }

    /** Fetch and Updates Database on a background thread; update the feed when ready */
    private void onUpdateDatabase(){
        new Thread( () ->{
            try {
                ArticleDatabase newDatabase = new ArticleRepository();
                Log.d("ControllerActivity", "Loaded " + newDatabase.getDatabase().size() + " articles");
                runOnUiThread(() -> onLoadArticles(newDatabase));
            } catch (Exception e) {
                Log.e("ControllerActivity", "Failed to load articles", e);
            }
        }).start();
    }

    /** Loads the updated database into the retriever and FolderManager. */
    private void onLoadArticles(ArticleDatabase newDatabase) {
        this.articleRetriever = new ArticleRetriever(newDatabase);
        if (folderManager != null) {
            this.folderManager.updateRetriever(articleRetriever);
        } else {
            this.folderManager = new FolderManager(articleRetriever);
        }

        Log.d("FeedDebug", "Article Size: " + articleRetriever.getDatabaseSize());

        // Only navigate to feed if the user has already signed in
        if (curState != State.AUTH) showArticleFeedTab();
    }

    /** Fetches and updates weather database on a background thread; updates the view when ready */
    private void onUpdateWeather(double lat, double lon) {
        new Thread(() -> {
            try {
                WeatherRepository repo = new WeatherRepository(new EcoDataFetcher());
                repo.refresh(lat, lon);
                WeatherRetriever retriever = new WeatherRetriever(repo);

                runOnUiThread(() -> onLoadWeather(retriever));
            } catch (Exception e) {
                Log.e("FeedDebug", "Failed to load weather", e);
            }
        }).start();
    }

    private void onLoadWeather(WeatherRetriever retriever) {
        this.weatherRetriever = retriever;

        // Only navigate to dashboard if the user has already signed in
        if (curState == State.DASHBOARD) {
            DashboardFragment dashboardFragment = new DashboardFragment();
            dashboardFragment.setListener(this);
            dashboardFragment.onWeatherLoaded(retriever);
            mainUI.displayFragment(dashboardFragment);
        }
    }


    /**
     * Navigation Helpers
     */

    private void onAuth() {
        this.curState = State.AUTH;
        AuthFragment authFragment = new AuthFragment();
        authFragment.setListener(this);
        if (mainUI != null) mainUI.displayFragment(authFragment);
    }

    private void showDashBoardTab() {
        this.curState = State.DASHBOARD;
        DashboardFragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setListener(this);
        if (weatherRetriever != null) dashboardFragment.onWeatherLoaded(weatherRetriever);
        if (mainUI != null) mainUI.displayFragment(dashboardFragment);
    }

    private void showArticleFeedTab() {
        this.curState = State.FEED;
        ArticleFeedFragment feedFragment = new ArticleFeedFragment();
        feedFragment.setListener(this);
        mainUI.displayFragment(feedFragment);

        if (articleRetriever != null && articleRetriever.getDatabaseSize() != 0) {
            onShowFeed(articleRetriever.returnDatabase(), feedFragment);
        }
    }

    private void showSearchTab() {
        this.curState = State.SEARCH;
        SearchArticleFragment searchFragment = new SearchArticleFragment();
        searchFragment.setListener(this);
        if (mainUI != null) mainUI.displayFragment(searchFragment);
    }

    private void showProfileTab() {
        this.curState = State.PROFILE;
        pfacade.loadFolderManager(new PersistenceFacade.DataListener<FolderManager>() {
            @Override
            public void onDataReceived(FolderManager loaded) {
                folderManager = loaded;
                if (articleRetriever != null) folderManager.updateRetriever(articleRetriever);
                displayProfileFragment();
            }
            @Override
            public void onNoDataFound() {
                displayProfileFragment();
            }
        });
    }

    private void displayProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setListener(this);
        if (mainUI != null) mainUI.displayFragment(profileFragment);
    }


    /**
     * Navigation Tab Implementations
     */

    @Override
    public void onArticleTabClick() { showArticleFeedTab(); }

    @Override
    public void onDashBoardClick() { showDashBoardTab(); }

    @Override
    public void onSearchClick() { showSearchTab(); }

    @Override
    public void onProfileClick() { showProfileTab(); }


    /**
     * AuthUI.Listener Implementations
     */

    @Override
    public void onRegister(String username, String password, AuthUI ui) {
        User user = new User(username, password);
        this.pfacade.createUserIfNotExists(user, new PersistenceFacade.BinaryResultListener() {
            @Override
            public void onYesResult() { ui.onRegisterSuccess(); }

            @Override
            public void onNoResult() { ui.onUserAlreadyExists(); }
        });
    }

    @Override
    public void onSigninAttempt(String username, String password, AuthUI ui) {
        this.pfacade.loadUser(username, new PersistenceFacade.DataListener<User>() {
            @Override
            public void onDataReceived(@NonNull User user) {
                if (user.validatePassword(password)) {
                    curUser = user;
                    // If articles already loaded go straight to feed, otherwise dashboard while loading
                    if (articleRetriever != null) {
                        showArticleFeedTab();
                    } else {
                        showDashBoardTab();
                    }
                } else {
                    ui.onInvalidCredentials();
                }
            }

            @Override
            public void onNoDataFound() { ui.onInvalidCredentials(); }
        });
    }


    /**
     * ArticleFeedUI.Listener Implementations
     */

    @Override
    public void onArticleClicked(int id) {
        if (articleRetriever.getArticle(id) == null) return;

        this.curState = State.DISPLAY_ARTICLE;

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
        if (articleRetriever != null && articleRetriever.getArticle(id) != null) {
            curArticle = articleRetriever.getArticle(id);
            ui.runShowArticle(curArticle);
        }
    }

    @Override
    public void onReturnClick() {
        curArticle = null;
        onArticleTabClick();
    }

    @Override
    public void onSaveClick(int id, String folderName) {
        if (folderManager != null) {
            folderManager.saveToFolder(id, folderName);
            pfacade.saveFolderManager(folderManager);
        }
    }

    @Override
    public void onLikeClick(int id) {
        if (curArticle != null) curArticle.addLike();
    }

    @Override
    public void onDislikeClick(int id) {
        if (curArticle != null) curArticle.addDislike();
    }

    @Override
    public void onCommentSubmit(int id, String comment) {
        if (curArticle != null) curArticle.addComment(comment);
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
