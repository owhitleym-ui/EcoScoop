package edu.vassar.cmpu203.ecoscoop.src.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleDatabase;
import edu.vassar.cmpu203.ecoscoop.src.model.ArticleRepository;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;
import edu.vassar.cmpu203.ecoscoop.src.model.FolderManager;
import edu.vassar.cmpu203.ecoscoop.src.model.User;
import edu.vassar.cmpu203.ecoscoop.src.persistence.FirestoreFacade;
import edu.vassar.cmpu203.ecoscoop.src.model.EcoRepository;
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

    // Fallback coordinates used when GPS is unavailable or local location is off (New York City)
    private static final double DEFAULT_LAT = 40.7128;
    private static final double DEFAULT_LON = -74.0060;

    private static final int GPS_TIMEOUT_MS = 15_000;

    private PersistenceFacade pfacade;
    private EcoDataRetriever ecoDataRetriever;
    private ArticleRetriever articleRetriever;
    private FolderManager folderManager;
    private Article curArticle;
    private User curUser;

    // Held so onLoadEcoData can push data to the existing instance instead of creating a new one
    private DashboardFragment dashboardFragment;

    // Location state
    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private double lat = 0.0;
    private double lon = 0.0;
    private String lastSearchedName = null;

    // GPS fallback: fires if no location arrives within GPS_TIMEOUT_MS
    private final Handler gpsTimeoutHandler = new Handler(Looper.getMainLooper());
    private final Runnable gpsFallbackRunnable = () -> {
        if (lat == 0.0 && lon == 0.0) {
            Log.d("FeedDebug", "GPS timeout — falling back to default location");
            lat = DEFAULT_LAT;
            lon = DEFAULT_LON;
            reverseGeocode(DEFAULT_LAT, DEFAULT_LON);
            onUpdateEcoData(lat, lon);
        }
    };

    State curState = State.AUTH;
    State prevState = State.FEED;

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
        onAuth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpsTimeoutHandler.removeCallbacks(gpsFallbackRunnable);
        if (locationCallback != null) {
            locationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE, this.curState.name());
        if (this.curArticle != null) outState.putString(CUR_ARTICLE_ID, this.curArticle.getId());
    }

    // ── Location ──────────────────────────────────────────────────────────────

    /** Request the user's location; falls back to a fresh fix if the cache is empty. */
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }

        // Schedule fallback in case GPS never responds
        gpsTimeoutHandler.removeCallbacks(gpsFallbackRunnable);
        gpsTimeoutHandler.postDelayed(gpsFallbackRunnable, GPS_TIMEOUT_MS);

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                gpsTimeoutHandler.removeCallbacks(gpsFallbackRunnable);
                this.lat = location.getLatitude();
                this.lon = location.getLongitude();
                Log.d("FeedDebug", "Cached location: " + this.lat + ", " + this.lon);
                reverseGeocode(this.lat, this.lon);
                onUpdateEcoData(this.lat, this.lon);
            } else {
                Log.d("FeedDebug", "No cached location — requesting fresh fix");
                requestFreshLocation();
            }
        });
    }

    /** Requests a single fresh GPS fix; falls back to default if the fix is null. */
    @SuppressWarnings("MissingPermission")
    private void requestFreshLocation() {
        LocationRequest req = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setMaxUpdates(1)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                gpsTimeoutHandler.removeCallbacks(gpsFallbackRunnable);
                locationClient.removeLocationUpdates(this);
                android.location.Location loc = result.getLastLocation();
                if (loc != null) {
                    lat = loc.getLatitude();
                    lon = loc.getLongitude();
                    Log.d("FeedDebug", "Fresh location: " + lat + ", " + lon);
                    reverseGeocode(lat, lon);
                    onUpdateEcoData(lat, lon);
                } else {
                    Log.d("FeedDebug", "Fresh location result was null — using default");
                    lat = DEFAULT_LAT;
                    lon = DEFAULT_LON;
                    reverseGeocode(DEFAULT_LAT, DEFAULT_LON);
                    onUpdateEcoData(lat, lon);
                }
            }
        };
        locationClient.requestLocationUpdates(req, locationCallback, getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else if (requestCode == 100) {
            // Permission denied — use default location so dashboard still loads
            Log.d("FeedDebug", "Location permission denied — using default location");
            lat = DEFAULT_LAT;
            lon = DEFAULT_LON;
            reverseGeocode(DEFAULT_LAT, DEFAULT_LON);
            onUpdateEcoData(lat, lon);
        }
    }

    /**
     * Geocodes a city name via Open-Meteo's geocoding API and fetches weather
     * for the first matching result. Runs entirely on a background thread.
     */
    private void geocodeAndFetch(String query) {
        new Thread(() -> {
            try {
                String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
                String urlStr = "https://geocoding-api.open-meteo.com/v1/search?name="
                        + encoded + "&count=1&language=en&format=json";

                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10_000);
                conn.setReadTimeout(10_000);

                int code = conn.getResponseCode();
                if (code != 200) {
                    Log.e("ControllerActivity", "Geocoding HTTP " + code);
                    conn.disconnect();
                    return;
                }

                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                try (InputStream is = conn.getInputStream()) {
                    byte[] chunk = new byte[4096];
                    int n;
                    while ((n = is.read(chunk)) != -1) buf.write(chunk, 0, n);
                } finally {
                    conn.disconnect();
                }

                org.json.JSONObject obj = new org.json.JSONObject(buf.toString("UTF-8"));
                org.json.JSONArray results = obj.optJSONArray("results");
                if (results == null || results.length() == 0) {
                    Log.d("ControllerActivity", "No geocoding results for: " + query);
                    return;
                }

                org.json.JSONObject first = results.getJSONObject(0);
                double geoLat  = first.getDouble("latitude");
                double geoLon  = first.getDouble("longitude");
                String geoName = first.optString("name", query);

                runOnUiThread(() -> {
                    lastSearchedName = geoName;
                    if (curState == State.DASHBOARD && dashboardFragment != null) {
                        dashboardFragment.setLocationLabel("📍 " + geoName);
                    }
                    onUpdateEcoData(geoLat, geoLon);
                });

            } catch (Exception e) {
                Log.e("ControllerActivity", "Geocoding error", e);
            }
        }).start();
    }

    /**
     * Reverse-geocodes coordinates to a human-readable city name using the device's
     * built-in Geocoder (no API key required). Runs on a background thread and
     * pushes the result to the dashboard label on the UI thread.
     */
    private void reverseGeocode(double lat, double lon) {
        new Thread(() -> {
            String label = String.format(Locale.US, "📍 %.2f°, %.2f°", lat, lon); // safe fallback
            try {
                if (Geocoder.isPresent()) {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address addr = addresses.get(0);
                        String city = addr.getLocality();
                        if (city == null || city.isEmpty()) city = addr.getSubAdminArea();
                        if (city == null || city.isEmpty()) city = addr.getAdminArea();
                        if (city != null && !city.isEmpty()) label = "📍 " + city;
                    }
                }
            } catch (Exception e) {
                Log.d("ControllerActivity", "Reverse geocoding failed: " + e.getMessage());
            }
            final String finalLabel = label;
            runOnUiThread(() -> {
                if (curState == State.DASHBOARD && dashboardFragment != null) {
                    dashboardFragment.setLocationLabel(finalLabel);
                }
            });
        }).start();
    }

    // ── Eco data ──────────────────────────────────────────────────────────────

    /** Fetches weather + climate on a background thread; delivers to the UI thread when done. */
    private void onUpdateEcoData(double lat, double lon) {
        new Thread(() -> {
            try {
                EcoRepository repo = new EcoRepository(new EcoDataFetcher());
                repo.refresh(lat, lon);
                EcoDataRetriever retriever = new EcoDataRetriever(repo);
                runOnUiThread(() -> onLoadEcoData(retriever));
            } catch (Exception e) {
                Log.e("FeedDebug", "Failed to load weather", e);
            }
        }).start();
    }

    private void onLoadEcoData(EcoDataRetriever retriever) {
        this.ecoDataRetriever = retriever;
        // Push to the dashboard if it's the current screen.
        // onWeatherLoaded() safely stores the data as pendingRetriever when
        // binding is null (fragment not yet attached), so no isAdded() guard needed.
        if (curState == State.DASHBOARD && dashboardFragment != null) {
            dashboardFragment.onWeatherLoaded(retriever);
        }
    }

    // ── Article database ──────────────────────────────────────────────────────

    /** Fetch and updates database on a background thread; updates the feed when ready. */
    private void onUpdateDatabase() {
        new Thread(() -> {
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

        if (curState != State.AUTH) showArticleFeedTab();
    }

    // ── Navigation helpers ────────────────────────────────────────────────────

    private void onAuth() {
        this.curState = State.AUTH;
        AuthFragment authFragment = new AuthFragment();
        authFragment.setListener(this);
        if (mainUI != null) mainUI.displayFragment(authFragment);
    }

    private void showDashBoardTab() {
        this.curState = State.DASHBOARD;
        this.dashboardFragment = new DashboardFragment();
        this.dashboardFragment.setListener(this);
        this.dashboardFragment.setUseMetric(curUser != null && curUser.isUseMetric());
        if (ecoDataRetriever != null) this.dashboardFragment.onWeatherLoaded(ecoDataRetriever);
        if (mainUI != null) mainUI.displayFragment(this.dashboardFragment);
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
                pfacade.loadSavedArticles(new PersistenceFacade.DataListener<java.util.Map<String, Article>>() {
                    @Override
                    public void onDataReceived(@NonNull java.util.Map<String, Article> saved) {
                        if (articleRetriever != null) articleRetriever.injectSavedArticles(saved);
                        displayProfileFragment();
                    }
                    @Override
                    public void onNoDataFound() { displayProfileFragment(); }
                });
            }
            @Override
            public void onNoDataFound() { displayProfileFragment(); }
        });
    }

    private void displayProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        if (curUser != null) {
            Bundle args = new Bundle();
            args.putString("username", curUser.getUsername());
            profileFragment.setArguments(args);
        }
        profileFragment.setListener(this);
        if (mainUI != null) mainUI.displayFragment(profileFragment);
    }

    // ── Navigation tab callbacks ──────────────────────────────────────────────

    @Override public void onArticleTabClick() { showArticleFeedTab(); }
    @Override public void onDashBoardClick()   { showDashBoardTab(); }
    @Override public void onSearchClick()      { showSearchTab(); }
    @Override public void onProfileClick()     { showProfileTab(); }

    @Override
    public void onRequestGPSRefresh() {
        lastSearchedName = null; // clear any searched city; go back to GPS
        // Skip the cache so we always get the current device location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gpsTimeoutHandler.removeCallbacks(gpsFallbackRunnable);
            gpsTimeoutHandler.postDelayed(gpsFallbackRunnable, GPS_TIMEOUT_MS);
            requestFreshLocation();
        } else {
            requestLocation();
        }
    }

    @Override
    public void onSearchLocation(String query) {
        if (query == null || query.trim().isEmpty()) return;
        geocodeAndFetch(query.trim());
    }

    // ── AuthUI.Listener ───────────────────────────────────────────────────────

    @Override
    public void onRegister(String username, String password, AuthUI ui) {
        User user = new User(username, password);
        this.pfacade.createUserIfNotExists(user, new PersistenceFacade.BinaryResultListener() {
            @Override public void onYesResult() { ui.onRegisterSuccess(); }
            @Override public void onNoResult()  { ui.onUserAlreadyExists(); }
        });
    }

    @Override
    public void onSigninAttempt(String username, String password, AuthUI ui) {
        this.pfacade.loadUser(username, new PersistenceFacade.DataListener<User>() {
            @Override
            public void onDataReceived(@NonNull User user) {
                if (user.validatePassword(password)) {
                    curUser = user;
                    pfacade.setCurrentUser(user.getUsername());
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

    // ── ArticleFeedUI.Listener ────────────────────────────────────────────────

    @Override
    public void onArticleClicked(String id) {
        if (articleRetriever == null || articleRetriever.getArticle(id) == null) return;

        this.prevState = this.curState;
        this.curState = State.DISPLAY_ARTICLE;

        Bundle args = new Bundle();
        args.putString("article_id", id);

        DisplayArticleFragment displayArticleFragment = new DisplayArticleFragment();
        displayArticleFragment.setListener(this);
        displayArticleFragment.setArguments(args);
        mainUI.displayFragment(displayArticleFragment);
    }

    @Override
    public void onShowFeed(List<Article> articles, ArticleFeedUI ui) {
        ui.runShowFeed(articles);
    }

    // ── DisplayArticleUI.Listener ─────────────────────────────────────────────

    @Override
    public void onRequestArticle(String id, DisplayArticleUI ui) {
        if (articleRetriever != null && articleRetriever.getArticle(id) != null) {
            curArticle = articleRetriever.getArticle(id);
            ui.runShowArticle(curArticle);
            if (curUser != null) { curUser.incrementRead(); pfacade.saveUser(curUser); }
        }
    }

    @Override
    public void onReturnClick() {
        curArticle = null;
        switch (prevState) {
            case PROFILE: showProfileTab(); break;
            case SEARCH:  showSearchTab();  break;
            default:      showArticleFeedTab(); break;
        }
    }

    @Override
    public void onSaveClick(String id, String folderName) {
        if (folderManager != null) {
            folderManager.saveToFolder(id, folderName);
            pfacade.saveFolderManager(folderManager);
            if (curArticle != null) pfacade.saveArticle(curArticle);
        }
    }

    @Override
    public void onLikeClick(String id) {
        if (curArticle != null) {
            curArticle.addLike();
            if ("liked".equals(curArticle.getUserReaction()) && curUser != null) {
                curUser.incrementLiked();
                pfacade.saveUser(curUser);
            }
        }
    }

    @Override
    public void onDislikeClick(String id) {
        if (curArticle != null) {
            curArticle.addDislike();
            if ("disliked".equals(curArticle.getUserReaction()) && curUser != null) {
                curUser.incrementDisliked();
                pfacade.saveUser(curUser);
            }
        }
    }

    @Override
    public void onCommentSubmit(String id, String comment) {
        if (curArticle != null) curArticle.addComment(comment);
        if (curUser != null) {
            curUser.addComment(comment);
            pfacade.saveUser(curUser);
        }
    }

    // ── SearchArticleUI.Listener ──────────────────────────────────────────────

    @Override
    public void onSearchQuery(String query, String type, SearchArticleUI ui) {
        List<Article> results = articleRetriever != null
                ? articleRetriever.searchArticles(query, type)
                : new ArrayList<>();
        ui.runShowFreshResults(results);
    }

    @Override
    public void onSortResults(List<Article> results, String criteria, SearchArticleUI ui) {
        List<Article> sorted = articleRetriever != null
                ? articleRetriever.sortArticles(results, criteria)
                : results;
        ui.runShowResults(sorted);
    }

    // ── ProfileUI.Listener ────────────────────────────────────────────────────

    @Override
    public List<Folder> onGetFolders() {
        if (folderManager == null) return new ArrayList<>();
        return folderManager.getFolders();
    }

    @Override
    public List<Article> onGetFolderContents(String folderName) {
        if (folderManager == null) return new ArrayList<>();
        Folder folder = folderManager.getFolder(folderName);
        return folder != null ? folder.open() : new ArrayList<>();
    }

    @Override
    public List<String> onGetUserComments() {
        if (curUser == null) return new ArrayList<>();
        return new ArrayList<>(curUser.getComments());
    }

    @Override
    public void onRemoveComment(int index) {
        if (curUser != null) {
            curUser.removeComment(index);
            pfacade.saveUser(curUser);
        }
    }

    @Override
    public void onDeleteFolder(String folderName) {
        if (folderManager != null) {
            folderManager.deleteFolder(folderName);
            pfacade.saveFolderManager(folderManager);
        }
    }

    @Override
    public void onRenameFolder(String oldName, String newName) {
        if (folderManager != null) {
            folderManager.renameFolder(oldName, newName);
            pfacade.saveFolderManager(folderManager);
        }
    }

    @Override
    public void onRemoveArticle(String folderName, String articleId) {
        if (folderManager != null) {
            Folder folder = folderManager.getFolder(folderName);
            if (folder != null) {
                folder.removeArticle(articleId);
                pfacade.saveFolderManager(folderManager);
            }
        }
    }

    @Override
    public void onSettingChanged(boolean useMetric, boolean useLocalLocation) {
        if (curUser == null) return;

        boolean wasLocal = curUser.isUseLocalLocation();
        curUser.setUseMetric(useMetric);
        curUser.setUseLocalLocation(useLocalLocation);
        pfacade.saveUser(curUser);

        // When the local/global toggle changes, refresh weather with the right source
        if (wasLocal != useLocalLocation) {
            if (useLocalLocation) {
                lastSearchedName = null;
                requestLocation();
            } else {
                // Global: use default location (clears any GPS coords)
                lat = DEFAULT_LAT;
                lon = DEFAULT_LON;
                lastSearchedName = null;
                reverseGeocode(DEFAULT_LAT, DEFAULT_LON);
                onUpdateEcoData(DEFAULT_LAT, DEFAULT_LON);
            }
        }

        // Push updated metric preference to the dashboard immediately if it's on screen
        if (curState == State.DASHBOARD
                && dashboardFragment != null
                && dashboardFragment.isAdded()) {
            dashboardFragment.setUseMetric(useMetric);
            if (ecoDataRetriever != null) dashboardFragment.onWeatherLoaded(ecoDataRetriever);
        }
    }

    @Override
    public boolean getUserSettingMetric() {
        return curUser != null && curUser.isUseMetric();
    }

    @Override
    public boolean getUserSettingLocal() {
        return curUser != null && curUser.isUseLocalLocation();
    }

    @Override
    public int[] getUserStats() {
        if (curUser == null) return new int[]{0, 0, 0};
        return new int[]{curUser.getArticlesRead(), curUser.getArticlesLiked(), curUser.getArticlesDisliked()};
    }
}
