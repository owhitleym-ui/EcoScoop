package edu.vassar.cmpu203.ecoscoop.src.view;

import edu.vassar.cmpu203.ecoscoop.src.model.Article;

public interface AccessArticleUI {

    // Events that go UP to the Controller
    interface Listener {
        void onArticleSelected(int articleId); // tells controller which article was tapped
    }

    // Methods the Controller calls DOWN into the fragment
    void setListener(Listener listener);
    void displayFeedCard(Article article); // populate the card with article data
}