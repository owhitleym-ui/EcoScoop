package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Downloads RSS feeds over HTTP and parses them into model.Article objects.
 * If one feed fails, it logs the error and moves on so the others still load.
 */
public class FeedFetcher {

    private static final int CONNECT_TIMEOUT_MS = 8_000;
    private static final int READ_TIMEOUT_MS    = 15_000;

    private static final String USER_AGENT =
            "Mozilla/5.0 (compatible; RSSReader/1.0)";

    /**
     * Fetches and parses all feeds in the given map.
     *
     * @param feeds a map of site name to RSS feed URL
     * @return all successfully parsed articles from every feed
     */
    public List<Article> fetchAll(Map<String, String> feeds) {
        ArrayList<Article> allArticles = new ArrayList<>();

        for (Map.Entry<String, String> entry : feeds.entrySet()) {
            String siteName = entry.getKey();
            String feedUrl  = entry.getValue();

            System.out.println("Fetching: " + feedUrl);
            try {
                String xml = download(feedUrl);
                ArticleParser parser = new ArticleParser();
                // Pass empty args array so the parser reads from the string
                parser.parse(new String[0], xml, siteName);
                List<Article> parsed = parser.loadArticles();
                System.out.println("  → " + parsed.size() + " articles from " + siteName);
                allArticles.addAll(parsed);
            } catch (Exception e) {
                // Log and continue so one bad feed doesn't stop the rest
                System.err.println("  ✗ Failed to fetch/parse " + siteName
                        + " (" + feedUrl + "): " + e.getMessage());
            }
        }

        return allArticles;
    }

    private String download(String feedUrl) throws IOException {
        HttpURLConnection conn = openConnection(feedUrl);
        int status = conn.getResponseCode();

        if (status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == 307 || status == 308) {
            String redirectUrl = conn.getHeaderField("Location");
            conn.disconnect();
            if (redirectUrl == null || redirectUrl.isEmpty()) {
                throw new IOException("Redirect with no Location header from " + feedUrl);
            }
            System.out.println("  ↳ Redirected to " + redirectUrl);
            conn = openConnection(redirectUrl);
            status = conn.getResponseCode();
        }

        if (status != HttpURLConnection.HTTP_OK) {
            conn.disconnect();
            throw new IOException("HTTP " + status + " for " + feedUrl);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            conn.disconnect();
        }

        return sb.toString();
    }

    private HttpURLConnection openConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "application/rss+xml, application/xml, text/xml, */*");
        conn.setInstanceFollowRedirects(true);
        return conn;
    }
}
