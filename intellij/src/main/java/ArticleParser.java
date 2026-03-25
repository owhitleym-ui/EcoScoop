import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;

public class ArticleParser {

    // result list
    private ArrayList<Article> articleList = new ArrayList<>();

    // buffers
    private String currentTag = "";
    private boolean insideItem = false;
    private String currentTitle;
    private String currentUrl;
    private String currentDescription;
    private String currentPubDate;
    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayList<String> authorList = new ArrayList<>();

    private int idCounter = 0;

    public void processDocument(XmlPullParser xpp)
            throws XmlPullParserException, IOException {

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            processEvent(xpp, eventType);
            eventType = xpp.next();
        }
    }

    private void processEvent(XmlPullParser xpp, int eventType)
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
            case "category":    categoryList.add(text); break;
            case "creator":     authorList.add(text); break;
        }
    }

    private void handleEndTag(String tagName) {
        if (tagName.equals("item")) {
            //articleList.add(buildArticle());
            insideItem = false;
        }
        currentTag = "";
    }

    //private Article buildArticle() {
        // TODO: construct Article from buffers
        //idCounter++;
        //Article article = new Article(idCounter, currentTitle,currentUrl,currentDescription,new ArrayList<>(categoryList), new ArrayList<>(authorList),currentPubDate);
        //return article;
    //}

    private void resetBuffers() {
        currentTitle = null;
        currentUrl = null;
        currentDescription = null;
        currentPubDate = null;
        categoryList.clear();
        authorList.clear();
    }

    public ArrayList<Article> loadArticles() {
        return articleList;
    }


}