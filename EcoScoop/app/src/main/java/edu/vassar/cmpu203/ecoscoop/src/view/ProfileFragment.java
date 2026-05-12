package edu.vassar.cmpu203.ecoscoop.src.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
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
 * Shows the user's profile: stats, settings, folders of saved articles, and comment history.
 * Tap a folder to browse its articles; long-press an article to remove it from the folder.
 */
public class ProfileFragment extends Fragment implements ProfileUI {

    private FragmentProfileBinding binding;
    private ProfileUI.Listener listener;
    private final FoldersAdapter foldersAdapter = new FoldersAdapter();
    private final SavedArticlesAdapter savedArticlesAdapter = new SavedArticlesAdapter();
    private OnBackPressedCallback backCallback;
    private String currentFolderName = null;

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

        // Stats
        if (listener != null) {
            int[] stats = listener.getUserStats();
            binding.statRead.setText(String.valueOf(stats[0]));
            binding.statLiked.setText(String.valueOf(stats[1]));
            binding.statDisliked.setText(String.valueOf(stats[2]));
        }

        // Settings button
        binding.settingsButton.setOnClickListener(v -> showSettingsDialog());

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

    /** Refreshes the folders list from the controller and toggles the empty-state label. */
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

    /** Rebuilds the comment history view, adding a removable bullet row for each comment. */
    private void loadComments() {
        if (!isAdded() || listener == null) return;
        List<String> comments = listener.onGetUserComments();
        if (comments.isEmpty()) {
            binding.commentsSection.setVisibility(View.GONE);
            return;
        }
        binding.commentsSection.setVisibility(View.VISIBLE);
        binding.commentsContainer.removeAllViews();

        int padH = (int) (4 * getResources().getDisplayMetrics().density);
        int padV = (int) (6 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < comments.size(); i++) {
            final int index = i;
            final String text = comments.get(i);

            // Row: bullet text (fills width) + ✕ button
            android.widget.LinearLayout row = new android.widget.LinearLayout(requireContext());
            row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(0, padV, 0, padV);

            TextView tv = new TextView(requireContext());
            tv.setText("• " + text);
            tv.setTextSize(14f);
            android.widget.LinearLayout.LayoutParams tvLp =
                    new android.widget.LinearLayout.LayoutParams(
                            0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tv.setLayoutParams(tvLp);
            row.addView(tv);

            TextView removeBtn = new TextView(requireContext());
            removeBtn.setText("✕");
            removeBtn.setTextSize(16f);
            removeBtn.setTextColor(android.graphics.Color.parseColor("#C0392B"));
            removeBtn.setPadding(padH * 3, padV, padH, padV);
            removeBtn.setClickable(true);
            removeBtn.setFocusable(true);
            removeBtn.setOnClickListener(v -> {
                if (!isAdded() || listener == null) return;
                new AlertDialog.Builder(requireContext())
                        .setTitle("Remove comment")
                        .setMessage("Remove this comment?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            listener.onRemoveComment(index);
                            loadComments();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
            row.addView(removeBtn);

            binding.commentsContainer.addView(row);
        }
    }

    /** Switches to the folder-contents screen and loads the given folder's articles. */
    private void openFolder(Folder folder) {
        if (listener == null) return;
        currentFolderName = folder.getFolderName();
        List<Article> articles = listener.onGetFolderContents(currentFolderName);

        binding.openFolderLabel.setText(currentFolderName);
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

    /** Returns to the folder-list screen and disables the back-navigation callback. */
    private void showFolderList() {
        currentFolderName = null;
        binding.folderContentsScreen.setVisibility(View.GONE);
        binding.folderListScreen.setVisibility(View.VISIBLE);
        backCallback.setEnabled(false);
        loadFolders(); // refresh in case a rename/delete happened
    }

    /** Shows the settings dialog with metric/imperial and local/global location toggles. */
    private void showSettingsDialog() {
        if (listener == null) return;

        boolean currentMetric = listener.getUserSettingMetric();
        boolean currentLocal  = listener.getUserSettingLocal();

        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, 0);

        TextView unitLabel = new TextView(requireContext());
        unitLabel.setText("Unit System");
        unitLabel.setTextSize(14f);
        unitLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(unitLabel);

        Switch metricSwitch = new Switch(requireContext());
        metricSwitch.setText(currentMetric ? "Metric" : "Customary (Imperial)");
        metricSwitch.setChecked(currentMetric);
        metricSwitch.setOnCheckedChangeListener((btn, checked) ->
                metricSwitch.setText(checked ? "Metric" : "Customary (Imperial)"));
        layout.addView(metricSwitch);

        TextView locLabel = new TextView(requireContext());
        locLabel.setText("Dashboard Location");
        locLabel.setTextSize(14f);
        locLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        android.widget.LinearLayout.LayoutParams lp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = pad;
        locLabel.setLayoutParams(lp);
        layout.addView(locLabel);

        Switch localSwitch = new Switch(requireContext());
        localSwitch.setText(currentLocal ? "Local" : "Global");
        localSwitch.setChecked(currentLocal);
        localSwitch.setOnCheckedChangeListener((btn, checked) ->
                localSwitch.setText(checked ? "Local" : "Global"));
        layout.addView(localSwitch);

        new AlertDialog.Builder(requireContext())
                .setTitle("Settings")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) ->
                        listener.onSettingChanged(metricSwitch.isChecked(), localSwitch.isChecked()))
                .setNegativeButton("Cancel", null)
                .show();
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

                b.renameFolder.setOnClickListener(v -> showRenameDialog(folder));
                b.deleteFolder.setOnClickListener(v -> showDeleteDialog(folder));
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

    /** Shows a dialog to rename the given folder, then refreshes the folder list on confirmation. */
    private void showRenameDialog(Folder folder) {
        if (listener == null) return;
        EditText input = new EditText(requireContext());
        input.setText(folder.getFolderName());
        input.setSelection(input.getText().length());
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);
        new AlertDialog.Builder(requireContext())
                .setTitle("Rename folder")
                .setView(input)
                .setPositiveButton("Rename", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty() && !newName.equals(folder.getFolderName())) {
                        listener.onRenameFolder(folder.getFolderName(), newName);
                        loadFolders();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Shows a confirmation dialog to permanently delete the given folder. */
    private void showDeleteDialog(Folder folder) {
        if (listener == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete folder")
                .setMessage("Delete \"" + folder.getFolderName() + "\"? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    listener.onDeleteFolder(folder.getFolderName());
                    loadFolders();
                })
                .setNegativeButton("Cancel", null)
                .show();
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

                // Long-press to remove article from the current folder
                cardBinding.getRoot().setOnLongClickListener(v -> {
                    if (listener == null || currentFolderName == null) return false;
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Remove article")
                            .setMessage("Remove this article from \"" + currentFolderName + "\"?")
                            .setPositiveButton("Remove", (dialog, which) -> {
                                listener.onRemoveArticle(currentFolderName, article.getId());
                                articles.remove(article);
                                notifyDataSetChanged();
                                if (articles.isEmpty()) {
                                    binding.savedArticlesRecyclerView.setVisibility(View.GONE);
                                    binding.emptyLabel.setVisibility(View.VISIBLE);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
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
