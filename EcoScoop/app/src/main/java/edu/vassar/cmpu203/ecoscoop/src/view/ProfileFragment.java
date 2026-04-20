package edu.vassar.cmpu203.ecoscoop.src.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.ecoscoop.databinding.FragmentProfileBinding;
import edu.vassar.cmpu203.ecoscoop.databinding.ItemArticleCardBinding;
import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Shows the user's saved articles in a scrollable list.
 * Delegates all user actions to the controller via ProfileUI.Listener.
 */
public class ProfileFragment extends Fragment implements ProfileUI {

    private FragmentProfileBinding binding;
    private ProfileUI.Listener listener;
    private final SavedArticlesAdapter adapter = new SavedArticlesAdapter();

    /** Grabs the controller as the listener when the fragment attaches to the activity. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileUI.Listener) {
            this.listener = (ProfileUI.Listener) context;
        } else {
            throw new ClassCastException(context + " must implement ProfileUI.Listener");
        }
    }

    /** Clears the listener when the fragment detaches. */
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    /** Inflates the profile layout. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentProfileBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    /** Sets up the RecyclerView, loads saved articles, and wires the nav buttons. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.savedArticlesRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.savedArticlesRecyclerView.setAdapter(adapter);

        // Load saved articles from the controller
        if (listener != null) {
            runShowSavedArticles(listener.onGetSavedArticles());
        }

        // Bottom nav
        binding.articleFeedTab.setOnClickListener(v -> {
            if (listener != null) listener.onArticleTabClick();
        });
        binding.dashboardTab.setOnClickListener(v -> {
            if (listener != null) listener.onDashBoardClick();
        });
        binding.searchTab.setOnClickListener(v -> {
            if (listener != null) listener.onSearchClick();
        });
        binding.profileTab.setOnClickListener(v -> {
            // Already on profile — no-op
        });
    }

    /** Sets the listener for user events. */
    @Override
    public void setListener(ProfileUI.Listener listener) {
        this.listener = listener;
    }

    /** Shows the saved articles list, or an empty state message if there are none. */
    @Override
    public void runShowSavedArticles(List<Article> articles) {
        adapter.setArticles(articles);
        if (articles.isEmpty()) {
            binding.savedArticlesRecyclerView.setVisibility(View.GONE);
            binding.emptyLabel.setVisibility(View.VISIBLE);
        } else {
            binding.savedArticlesRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyLabel.setVisibility(View.GONE);
        }
    }

    /** Adapter that binds saved articles to article card views. */
    private class SavedArticlesAdapter extends RecyclerView.Adapter<SavedArticlesAdapter.ViewHolder> {

        private List<Article> articles = new ArrayList<>();

        /** Replaces the current list and refreshes the RecyclerView. */
        void setArticles(List<Article> newArticles) {
            this.articles = new ArrayList<>(newArticles);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemArticleCardBinding cardBinding;

            ViewHolder(ItemArticleCardBinding b) {
                super(b.getRoot());
                this.cardBinding = b;
            }

            /** Fills the card with the article's info and sets the tap listener. */
            void bind(Article article) {
                cardBinding.cardTitle.setText(article.getTitle());

                String desc = article.getDescription();
                if (desc != null && !desc.isEmpty()) {
                    cardBinding.cardDescription.setText(desc);
                    cardBinding.cardDescription.setVisibility(View.VISIBLE);
                } else {
                    cardBinding.cardDescription.setVisibility(View.GONE);
                }

                if (article.getSource() != null) {
                    cardBinding.cardSource.setText(article.getSource().getWebsiteName());
                    cardBinding.cardDate.setText(article.getSource().getPublishDate());
                }

                List<Author> authors = article.getAuthors();
                if (authors != null && !authors.isEmpty()) {
                    StringBuilder sb = new StringBuilder("By ");
                    for (int i = 0; i < authors.size(); i++) {
                        sb.append(authors.get(i).getName());
                        if (i < authors.size() - 1) sb.append(", ");
                    }
                    cardBinding.cardAuthor.setText(sb.toString());
                    cardBinding.cardAuthor.setVisibility(View.VISIBLE);
                } else {
                    cardBinding.cardAuthor.setVisibility(View.INVISIBLE);
                }

                List<Tag> tags = article.getTagList();
                if (tags != null && !tags.isEmpty()) {
                    cardBinding.cardTag.setText(tags.get(0).getName());
                    cardBinding.cardTag.setVisibility(View.VISIBLE);
                } else {
                    cardBinding.cardTag.setVisibility(View.GONE);
                }

                cardBinding.getRoot().setOnClickListener(v -> {
                    if (listener != null) listener.onArticleClicked(article.getId());
                });
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemArticleCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(articles.get(position));
        }

        @Override
        public int getItemCount() { return articles.size(); }
    }
}
