import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class ArticleParser {

    // Stored Variables
    private ArrayList<String> tagList = new ArrayList<>();
    private ArrayList<String> authorList = new ArrayList<>();
    private final ArrayList<Article> articleList = new ArrayList<>();
    private String website;
    private String content;

    // parse buffers
    private String currentTag = "";
    private boolean insideItem = false;
    private String currentTitle;
    private String currentUrl;
    private String currentDescription;
    private String currentPubDate;
    private int idCounter = 0;

    private final XmlPullParser xpp;


    public ArticleParser() throws XmlPullParserException {
        // Private XmlPull Class
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        this.xpp = factory.newPullParser();
    }

    public void processDocument()
            throws XmlPullParserException, IOException {

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            processEvent(eventType);
            eventType = xpp.next();
        }
    }

    private void processEvent(int eventType)
            throws XmlPullParserException, IOException {
        switch (eventType) {
            case XmlPullParser.START_TAG:
                handleStartTag(xpp.getName());
                break;
            case XmlPullParser.TEXT:
                handleText(xpp.getText());
                break;
            case XmlPullParser.END_TAG:
                handleEndTag(xpp.getName());
                break;
        }
    }

    private void handleStartTag(String tagName) {
        if (tagName.equals("item")) {
            insideItem = true;
            resetBuffers();
        }
        currentTag = tagName;
    }

    private void handleText(String text) {
        if (!insideItem || text.trim().isEmpty()) return;

        switch (currentTag) {
            case "title":       currentTitle = text; break;
            case "link":        currentUrl = text; break;
            case "description": currentDescription = text; break;
            case "pubDate":     currentPubDate = text; break;
            case "category":    tagList.add(text); break;
            case "creator":     authorList.add(text); break;
        }
    }

    private void handleEndTag(String tagName) {
        if (tagName.equals("item")) {
            articleList.add(buildArticle());
            insideItem = false;
        }
        currentTag = "";
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

        return new Article(
                idCounter,
                currentTitle,
                currentDescription,
                authors,
                tags,
                new Source(website, currentUrl, currentPubDate),
                content
        );
    }

    private void resetBuffers() {
        currentTitle = null;
        currentUrl = null;
        currentDescription = null;
        currentPubDate = null;
        tagList.clear();
        authorList.clear();
    }

    public void parse(String[] args, String content, String fileWebsite)
            throws XmlPullParserException, IOException {
        this.content = content;
        this.website = fileWebsite;
        if (args.length == 0) {
            System.out.println("Parsing simple sample XML");
            xpp.setInput(new StringReader(content));
            processDocument();
        } else {
            for (int i = 0; i < args.length; i++) {
                System.out.println("Parsing file: " + args[i]);
                xpp.setInput(new FileReader(args[i]));
                processDocument();
            }
        }
    }

    public ArrayList<Article> loadArticles() {
        return articleList;
    }


}