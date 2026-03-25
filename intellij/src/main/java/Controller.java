import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;

public class Controller implements UI.Listener{
    private UI ui;
    private ArticleRetriever retriever = new ArticleRetriever();
    private ArrayList<Article> articles = retriever.articleList;
    private Article currentArticle = new Article();

    private Controller(final UI ui) throws XmlPullParserException {
        this.ui = ui;
        this.ui.setListener(this);
    }

    public static void main(String[] args) throws XmlPullParserException {
        final UI ui = new CmdLineUI();
        final Controller controller = new Controller(ui);
        controller.run();
    }

    private void run(){
        this.ui.runMainMenu();
    }

    //Listener Override Methods
    @Override
    public void onChooseArticle(int id) {
        this.currentArticle = retriever.articleList.get(id);
        this.ui.runClickArticle(currentArticle);
    }

    @Override
    public void onCancel() {
        this.currentArticle = null;
        this.ui.runCancel();

    }

    @Override
    public void onDisplayArticleList(ArrayList<Article> articles) {

    }

}
