package edu.vassar.cmpu203.ecoscoop.src.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import edu.vassar.cmpu203.ecoscoop.databinding.FragmentDisplayArticleBinding;
import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;

/**
 * Shows the full content of a single article.
 * Gets the article id from its arguments and asks the controller to load it.
 */
public class DisplayArticleFragment extends Fragment implements DisplayArticleUI {

    private FragmentDisplayArticleBinding binding;

    private DisplayArticleUI.Listener listener;

    /** Grabs the controller as the listener when the fragment attaches to the activity. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DisplayArticleUI.Listener) {
            this.listener = (DisplayArticleUI.Listener) context;
        } else {
            throw new ClassCastException(context + " must implement DisplayArticleUI.Listener");
        }
    }

    /** Clears the listener when the fragment detaches. */
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    /** Inflates the article display layout. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.binding = FragmentDisplayArticleBinding.inflate(inflater, container, false);

        return this.binding.getRoot();
    }

    /** Reads the article id from arguments, requests the article, and wires all buttons. */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int articleId = getArguments() != null ? getArguments().getInt("article_id", -1) : -1;

        if (articleId != -1 && listener != null) {
            listener.onRequestArticle(articleId, this);
        }

        // Top bar
        this.binding.returnButton.setOnClickListener(v -> {
            if (listener != null) listener.onReturnClick();
        });
        this.binding.saveButton.setOnClickListener(v -> {
            if (articleId != -1 && listener != null) {
                // TODO: show folder-picker dialog, then call listener.onSaveClick(articleId, folderName)
            }
        });

        // Bottom nav tabs
        this.binding.articleFeedTab.setOnClickListener(v -> {
            if (listener != null) listener.onArticleTabClick();
        });
        this.binding.dashboardTab.setOnClickListener(v -> {
            if (listener != null) listener.onDashBoardClick();
        });
        this.binding.searchTab.setOnClickListener(v -> {
            if (listener != null) listener.onSearchClick();
        });
        this.binding.profileTab.setOnClickListener(v -> {
            if (listener != null) listener.onProfileClick();
        });
    }

    /** Sets the listener for user events. */
    @Override
    public void setListener(Listener listener) {this.listener = listener;}

    /** Shows the article on screen. */
    @Override
    public void runShowArticle(Article article) {
        setArticleBinding(article);
    }

    /** Fills the layout views with the article's title, author, date, and content. */
    private void setArticleBinding(Article article){

        //Sets Title
        this.binding.articleDisplayTitle.setText(article.getTitle());

        //Sets Authors
        List<Author> authors = article.getAuthors();
        if (authors != null && !authors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < authors.size(); i++) {
                sb.append(authors.get(i).getName());
                if (i < authors.size() - 1) sb.append(", ");
            }
            this.binding.articleDisplayAuthor.setText(sb.toString());
            this.binding.articleDisplayAuthor.setVisibility(View.VISIBLE);
        } else {
            this.binding.articleDisplayAuthor.setVisibility(View.INVISIBLE);
        }

        //Sets Date
        this.binding.articleDisplayDate.setText(article.getSource().getPublishDate());

        //Sets Content
        this.binding.articleContent.setText(article.getContent());

    }
}
