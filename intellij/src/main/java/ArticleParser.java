import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

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

    private static int idCounter = 0;

    private final XmlPullParser xpp;

    public ArticleParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        this.xpp = factory.newPullParser();
    }

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
                currentTitle = text;
                break;
            case "link":
                currentUrl = text;
                break;
            case "description":
                currentDescription = text;
                break;
            case "pubDate":
                currentPubDate = text;
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

        return new Article(idCounter, currentTitle, currentDescription, authors, tags,
                new Source(website, currentUrl, currentPubDate),
                String.join(" ", content)
        );
    }

    private void resetBuffers() {
        currentTitle = null;
        currentUrl = null;
        currentDescription = null;
        currentPubDate = null;

        tagList.clear();
        authorList.clear();
        content.clear();
    }

    public ArrayList<Article> loadArticles() {
        return articleList;
    }
}
