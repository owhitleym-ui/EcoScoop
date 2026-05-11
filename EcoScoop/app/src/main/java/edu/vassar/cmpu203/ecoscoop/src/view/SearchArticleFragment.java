package edu.vassar.cmpu203.ecoscoop.src.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.ecoscoop.databinding.FragmentSearchArticleBinding;
import edu.vassar.cmpu203.ecoscoop.databinding.ItemArticleCardBinding;
import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Search screen. Lets the user query articles by keyword, tag, or author,
 * then sort results by relevance or date. Tapping a card opens the article.
 *
 * Navigation follows the same tab model as all other fragments:
 * every tab button delegates to ControllerActivity.
 */
public class SearchArticleFragment extends Fragment implements SearchArticleUI {

    private FragmentSearchArticleBinding binding;
    private SearchArticleUI.Listener listener;

    private final SearchResultAdapter adapter = new SearchResultAdapter();
    private List<Article> currentResults = new ArrayList<>();
    private List<Article> originalResults = new ArrayList<>();

    /** Grabs the controller as the listener when the fragment attaches to the activity. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchArticleUI.Listener) {
            this.listener = (SearchArticleUI.Listener) context;
        } else {
            throw new ClassCastException(context + " must implement SearchArticleUI.Listener");
        }
    }

    /** Clears the listener when the fragment detaches. */
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    /** Inflates the search screen layout. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentSearchArticleBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    /** Sets up the RecyclerView, search button, sort chips, and nav buttons. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.searchResultsRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.searchResultsRecyclerView.setAdapter(adapter);

        // Search input
        binding.searchButton.setOnClickListener(v -> performSearch());
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // Sort chips — delegate to controller, which calls runShowResults
        binding.chipGroupSort.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty() || originalResults.isEmpty() || listener == null) return;
            int chipId = checkedIds.get(0);
            if (chipId == binding.chipSortRelevance.getId()) {
                runShowResults(new ArrayList<>(originalResults));
            } else {
                String criteria = (chipId == binding.chipSortDate.getId())   ? "oldest"
                                : (chipId == binding.chipSortSource.getId()) ? "source"
                                : "relevance";
                listener.onSortResults(originalResults, criteria, this);
            }
        });

        // Bottom nav
        binding.articleFeedTab.setOnClickListener(v -> {
            if (listener != null) listener.onArticleTabClick();
        });
        binding.dashboardTab.setOnClickListener(v -> {
            if (listener != null) listener.onDashBoardClick();
        });
        binding.searchTab.setOnClickListener(v -> {
            // Already on search — refocus the input
            binding.searchInput.requestFocus();
        });
        binding.profileTab.setOnClickListener(v -> {
            if (listener != null) listener.onProfileClick();
        });
    }

    /** Sets the listener for user events. */
    @Override
    public void setListener(SearchArticleUI.Listener listener) {
        this.listener = listener;
    }

    /** Shows search results in the list and updates the result count label. */
    @Override
    public void runShowResults(List<Article> results) {
        currentResults = new ArrayList<>(results);
        adapter.setArticles(results);
        int n = results.size();
        binding.searchResultsCount.setText(
                n == 0 ? "No results found" : n + " result" + (n == 1 ? "" : "s"));
    }

    /** Shows a fresh search result set, resetting the sort state to Relevance. */
    public void runShowFreshResults(List<Article> results) {
        originalResults = new ArrayList<>(results);
        binding.chipSortRelevance.setChecked(true);
        runShowResults(results);
    }

    /** Reads the search input and selected type, then asks the controller for results. */
    private void performSearch() {
        if (listener == null) return;
        String query = binding.searchInput.getText() != null
                ? binding.searchInput.getText().toString().trim() : "";
        if (query.isEmpty()) return;

        InputMethodManager imm = (InputMethodManager)
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(binding.searchInput.getWindowToken(), 0);

        String type = getSelectedSearchType();
        listener.onSearchQuery(query, type, this);
    }

    /** Returns the search type selected by the chip group: "tag", "author", or "keyword". */
    private String getSelectedSearchType() {
        int id = binding.chipGroupType.getCheckedChipId();
        if (id == binding.chipTag.getId())    return "tag";
        if (id == binding.chipAuthor.getId()) return "author";
        return "keyword";
    }

    /** Adapter that binds search result articles to article card views. */
    private class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

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

            /** Fills the card view with the article's info and sets the tap listener. */
            void bind(Article article) {
                String imgUrl = article.getImageUrl();
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    cardBinding.imgHeader.setVisibility(View.VISIBLE);
                    Glide.with(cardBinding.getRoot().getContext())
                            .load(imgUrl)
                            .centerCrop()
                            .into(cardBinding.imgHeader);
                } else {
                    cardBinding.imgHeader.setVisibility(View.GONE);
                }

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
