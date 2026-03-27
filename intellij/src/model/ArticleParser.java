package model;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Parses RSS XML content into model.Article objects using an XML pull parser.
 * Handles standard RSS fields like title, link, description, pubDate,
 * dc:creator for authors, category for tags, and content:encoded for body text.
 */
public class ArticleParser {

    private final ArrayList<Article> articleList = new ArrayList<>();

    private ArrayList<String> tagList = new ArrayList<>();
    private ArrayList<String> authorList = new ArrayList<>();
    private ArrayList<String> content = new ArrayList<>();

    private String website;

    private boolean insideItem = false;

    private static final String MEDIA_NS = "http://search.yahoo.com/mrss/";

    private int mediaDepth = 0;

    private String currentTag = "";

    private String currentTitle;
    private String currentUrl;
    private String currentDescription;
    private String currentPubDate;

    private static final String CONTENT_NS = "http://purl.org/rss/1.0/modules/content/";

    private static int idCounter = 0;

    private final XmlPullParser xpp;

    public ArticleParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        this.xpp = factory.newPullParser();
    }

    /**
     * Parses RSS content either from a string or from files.
     * Pass an empty args array to parse from the content string directly.
     *
     * @param args        file paths to parse, or empty to use the content string
     * @param content     raw RSS XML string (used when args is empty)
     * @param fileWebsite the name of the source website, attached to each article
     */
    public void parse(String[] args, String content, String fileWebsite)
            throws XmlPullParserException, IOException {

        this.website = fileWebsite;

        if (args.length == 0) {
            xpp.setInput(new StringReader(content));
            processDocument();
        } else {
            for (String file : args) {
                xpp.setInput(new FileReader(file));
                processDocument();
            }
        }
    }

    private void processDocument() throws XmlPullParserException, IOException {
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                handleStartTag();
            }
            else if (eventType == XmlPullParser.TEXT) {
                handleText(xpp.getText());
            }
            else if (eventType == XmlPullParser.END_TAG) {
                handleEndTag();
            }

            eventType = xpp.next();
        }
    }

    private void handleStartTag() {
        String tagName = xpp.getName();
        String namespace = xpp.getNamespace();
        if (MEDIA_NS.equals(namespace)) {
            mediaDepth++;
        }

        if (tagName.equals("item")) {
            insideItem = true;
            resetBuffers();
        }

        if (mediaDepth == 0) {
            currentTag = tagName;
        }
    }

    private void handleText(String text) {
        if (!insideItem) return;
        if (mediaDepth > 0) return;  // ignore everything inside media:*
        if (text == null || text.trim().isEmpty()) return;

        text = text.trim();

        switch (currentTag) {
            case "title":
                currentTitle += text;
                break;
            case "link":
                currentUrl += text;
                break;
            case "description":
                currentDescription += text;
                break;
            case "pubDate":
                currentPubDate += text;
                break;
            case "category":
                tagList.add(text);
                break;
            case "creator":
                authorList.add(text);
                break;
            case "p":
                content.add(text);
                break;
            case "encoded":
                String plainText = text.replaceAll("<[^>]+>", " ")
                                       .replaceAll("\\s{2,}", " ")
                                       .trim();
                if (!plainText.isEmpty()) {
                    content.add(plainText);
                }
                break;
        }
    }

    private void handleEndTag() {
        String tagName = xpp.getName();
        String namespace = xpp.getNamespace();

        if (MEDIA_NS.equals(namespace)) {
            mediaDepth--;
        }

        if (tagName.equals("item")) {
            articleList.add(buildArticle());
            insideItem = false;
        }

        if (mediaDepth == 0) {
            currentTag = "";
        }
    }

    private Article buildArticle() {
        idCounter++;

        ArrayList<Author> authors = new ArrayList<>();
        for (String name : authorList) {
            authors.add(new Author(name));
        }

        ArrayList<Tag> tags = new ArrayList<>();
        for (String category : tagList) {
            tags.add(new Tag(category));
        }

        // Strip HTML tags and extra whitespace from description so it displays cleanly
        String cleanDesc = currentDescription
                .replace("&#8217;", "'")
                .replace("&#8216;", "'")
                .replace("&#8220;", "\"")
                .replace("&#8221;", "\"")
                .replace("&#8211;", "-")
                .replace("&#8212;", "--")
                .replaceAll("<[^>]+>", " ")
                .replaceAll("&[a-zA-Z]+;", " ")
                .replaceAll("\\s+", " ")
                .trim();

        // If no body content was parsed (feed has no content:encoded), fall back to description
        String body = content.isEmpty() ? cleanDesc : String.join(" ", content);

        String cleanTitle = currentTitle
                .replace("&#8217;", "'")
                .replace("&#8216;", "'")
                .replace("&#8220;", "\"")
                .replace("&#8221;", "\"")
                .replace("&#8211;", "-")
                .replace("&#8212;", "--");

        return new Article(idCounter, cleanTitle, cleanDesc, authors, tags,
                new Source(website, currentUrl, currentPubDate),
                body
        );
    }

    private void resetBuffers() {
        currentTitle = "";
        currentUrl = "";
        currentDescription = "";
        currentPubDate = "";

        tagList.clear();
        authorList.clear();
        content.clear();
    }

    /** Returns the list of articles built during the last parse call. */
    public ArrayList<Article> loadArticles() {
        return articleList;
    }
}
