package edu.vassar.cmpu203.ecoscoop.src.view;

import java.util.List;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;

public interface ArticleFeedUI {

    interface Listener{
        //Navigation Methods
        void onArticleTabClick();
        void onDashBoardClick();
        void onSearchClick();
        void onProfileClick();

        //Action Methods
        void onArticleClicked(int id);
        void onShowFeed(List<Article> ArticleList, ArticleFeedUI ui);
    }

    void setListener(Listener listener);

    void runShowFeed(List<Article> ArticleList);
    void runArticleClicked(int id);
}
