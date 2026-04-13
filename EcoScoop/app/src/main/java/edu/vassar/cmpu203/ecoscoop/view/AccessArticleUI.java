package edu.vassar.cmpu203.ecoscoop.view;

public interface AccessArticleUI {

    interface Listener {
        void onChooseArticle();
        void onDisplayArticle();
    }

    public void setListener(final Listener listener);
    public void runDisplayArticle();

}
