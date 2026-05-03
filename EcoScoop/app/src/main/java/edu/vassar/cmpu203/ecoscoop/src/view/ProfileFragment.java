package edu.vassar.cmpu203.ecoscoop.src.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.ecoscoop.databinding.FragmentProfileBinding;
import edu.vassar.cmpu203.ecoscoop.databinding.ItemArticleCardBinding;
import edu.vassar.cmpu203.ecoscoop.databinding.ItemFolderBinding;
import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Folder;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Shows the user's folders of saved articles.
 * Tap a folder to browse its articles; press Back to return to the folder list.
 */
public class ProfileFragment extends Fragment implements ProfileUI {

    private FragmentProfileBinding binding;
    private ProfileUI.Listener listener;
    private final FoldersAdapter foldersAdapter = new FoldersAdapter();
    private final SavedArticlesAdapter savedArticlesAdapter = new SavedArticlesAdapter();
    private OnBackPressedCallback backCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileUI.Listener) {
            this.listener = (ProfileUI.Listener) context;
        } else {
            throw new ClassCastException(context + " must implement ProfileUI.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentProfileBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back callback — enabled only when viewing a folder's contents
        backCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                showFolderList();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), backCallback);

        // Username in header
        String username = getArguments() != null
                ? getArguments().getString("username", "") : "";
        if (!username.isEmpty()) {
            binding.usernameLabel.setText("@" + username);
        }

        // Folders RecyclerView
        binding.foldersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.foldersRecyclerView.setAdapter(foldersAdapter);

        // Articles RecyclerView (inside a folder)
        binding.savedArticlesRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.savedArticlesRecyclerView.setAdapter(savedArticlesAdapter);

        // "← Folders" button in the folder-contents header
        binding.backToFoldersButton.setOnClickListener(v -> showFolderList());

        // Load folders and comments
        if (listener != null) {
            loadFolders();
            loadComments();
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
        binding.profileTab.setOnClickListener(v -> { /* already here */ });
    }

    @Override
    public void setListener(ProfileUI.Listener listener) {
        this.listener = listener;
    }

    private void loadFolders() {
        List<Folder> folders = listener.onGetFolders();
        foldersAdapter.setFolders(folders);
        if (folders.isEmpty()) {
            binding.foldersRecyclerView.setVisibility(View.GONE);
            binding.emptyFoldersLabel.setVisibility(View.VISIBLE);
        } else {
            binding.foldersRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyFoldersLabel.setVisibility(View.GONE);
        }
    }

    private void loadComments() {
        List<String> comments = listener.onGetUserComments();
        if (comments.isEmpty()) {
            binding.commentsSection.setVisibility(View.GONE);
            return;
        }
        binding.commentsSection.setVisibility(View.VISIBLE);
        binding.commentsContainer.removeAllViews();
        for (String comment : comments) {
            TextView tv = new TextView(requireContext());
            tv.setText("• " + comment);
            tv.setTextSize(14f);
            tv.setPadding(0, 8, 0, 8);
            binding.commentsContainer.addView(tv);
        }
    }

    private void openFolder(Folder folder) {
        if (listener == null) return;
        List<Article> articles = listener.onGetFolderContents(folder.getFolderName());

        binding.openFolderLabel.setText(folder.getFolderName());
        binding.folderListScreen.setVisibility(View.GONE);
        binding.folderContentsScreen.setVisibility(View.VISIBLE);
        backCallback.setEnabled(true);

        if (articles.isEmpty()) {
            binding.savedArticlesRecyclerView.setVisibility(View.GONE);
            binding.emptyLabel.setVisibility(View.VISIBLE);
        } else {
            binding.savedArticlesRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyLabel.setVisibility(View.GONE);
            savedArticlesAdapter.setArticles(articles);
        }
    }

    private void showFolderList() {
        binding.folderContentsScreen.setVisibility(View.GONE);
        binding.folderListScreen.setVisibility(View.VISIBLE);
        backCallback.setEnabled(false);
    }

    // ── Folders adapter ────────────────────────────────────────────────────────

    private class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ViewHolder> {

        private List<Folder> folders = new ArrayList<>();

        void setFolders(List<Folder> newFolders) {
            this.folders = new ArrayList<>(newFolders);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemFolderBinding b;

            ViewHolder(ItemFolderBinding b) {
                super(b.getRoot());
                this.b = b;
            }

            void bind(Folder folder) {
                b.folderName.setText(folder.getFolderName());
                int count = folder.size();
                b.folderCount.setText(count + " article" + (count == 1 ? "" : "s"));
                b.getRoot().setOnClickListener(v -> openFolder(folder));
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemFolderBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(folders.get(position));
        }

        @Override
        public int getItemCount() { return folders.size(); }
    }

    // ── Saved articles adapter ─────────────────────────────────────────────────

    private class SavedArticlesAdapter extends RecyclerView.Adapter<SavedArticlesAdapter.ViewHolder> {

        private List<Article> articles = new ArrayList<>();

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

            void bind(Article article) {
                // Header image
                String imgUrl = article.getImageUrl();
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    cardBinding.imgHeader.setVisibility(View.VISIBLE);
                    Glide.with(cardBinding.getRoot().getContext())
                            .load(imgUrl)
                            .centerCrop()
                            .into(cardBinding.imgHeader);
                } else {
                    cardBinding.imgHeader.setImageDrawable(null);
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
