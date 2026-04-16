package edu.vassar.cmpu203.ecoscoop.src.view;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;

public interface DisplayArticleUI {

    interface Listener {
        //Navigation Methods
        void onReturnClick();
        void onArticleTabClick();
        void onDashBoardClick();
        void onSearchClick();
        void onProfileClick();


        //Action Methods
        void onRequestArticle(int id, DisplayArticleUI ui);
        //void onLikeArticle(int id);
        //void onDislikeArticle(int id);
        //void onCommentArticle(int id, String comment);
        void onSaveClick(int id, String folderName);
    }

    void setListener(Listener listener);
    void runShowArticle(Article article);

}
