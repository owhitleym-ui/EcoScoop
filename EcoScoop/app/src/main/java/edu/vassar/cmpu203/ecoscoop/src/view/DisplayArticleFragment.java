package edu.vassar.cmpu203.ecoscoop.src.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private Article currentArticle;

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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int articleId = getArguments() != null ? getArguments().getInt("article_id", -1) : -1;

        if (articleId != -1 && listener != null) {
            listener.onRequestArticle(articleId, this);
        }

        // Back button
        this.binding.returnButton.setOnClickListener(v -> {
            if (listener != null) listener.onReturnClick();
        });

        // Save button — shows a popup asking for a folder name
        this.binding.saveButton.setOnClickListener(v -> {
            if (articleId == -1 || listener == null) return;
            EditText input = new EditText(requireContext());
            input.setHint("Folder name");
            new AlertDialog.Builder(requireContext())
                    .setTitle("Save to folder")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String name = input.getText().toString().trim();
                        if (!name.isEmpty()) listener.onSaveClick(articleId, name);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Like button — toggles and updates both reaction labels
        this.binding.likeButton.setOnClickListener(v -> {
            if (listener == null || currentArticle == null) return;
            listener.onLikeClick(articleId);
            updateReactionButtons();
        });

        // Dislike button — toggles and updates both reaction labels
        this.binding.dislikeButton.setOnClickListener(v -> {
            if (listener == null || currentArticle == null) return;
            listener.onDislikeClick(articleId);
            updateReactionButtons();
        });

        // Comment submit — adds the comment to the article and displays it
        this.binding.submitCommentButton.setOnClickListener(v -> {
            if (listener == null || currentArticle == null) return;
            String text = binding.commentInput.getText() != null
                    ? binding.commentInput.getText().toString().trim() : "";
            if (text.isEmpty()) return;
            listener.onCommentSubmit(articleId, text);
            addCommentView(text);
            binding.commentInput.setText("");
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
    public void setListener(Listener listener) { this.listener = listener; }

    /** Stores the article and shows it on screen. */
    @Override
    public void runShowArticle(Article article) {
        this.currentArticle = article;
        setArticleBinding(article);
    }

    /** Fills the layout views with the article's data and loads any existing reactions. */
    private void setArticleBinding(Article article) {

        // Title
        this.binding.articleDisplayTitle.setText(article.getTitle());

        // Authors
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

        // Date
        this.binding.articleDisplayDate.setText(article.getSource().getPublishDate());

        // Reaction counts (reflect current toggle state)
        updateReactionButtons();

        // Content
        this.binding.articleContent.setText(article.getContent());

        // Load any comments already on the article
        this.binding.commentsContainer.removeAllViews();
        for (String comment : article.getComments()) {
            addCommentView(comment);
        }
    }

    /** Updates like/dislike button text to reflect the current toggle state. */
    private void updateReactionButtons() {
        if (currentArticle == null) return;
        String reaction = currentArticle.getUserReaction();
        binding.likeButton.setText(
                "liked".equals(reaction)
                        ? "Liked: "    + currentArticle.getLikes()
                        : "Likes: "    + currentArticle.getLikes());
        binding.dislikeButton.setText(
                "disliked".equals(reaction)
                        ? "Disliked: " + currentArticle.getDislikes()
                        : "Dislikes: " + currentArticle.getDislikes());
    }

    /** Adds a single comment as a TextView inside the comments container. */
    private void addCommentView(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText("• " + text);
        tv.setTextSize(14f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 6, 0, 6);
        tv.setLayoutParams(params);
        this.binding.commentsContainer.addView(tv);
    }
}
