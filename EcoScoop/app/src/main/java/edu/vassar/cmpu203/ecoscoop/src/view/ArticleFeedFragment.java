package edu.vassar.cmpu203.ecoscoop.src.view;

import android.os.Bundle;
import android.util.Log;
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

import edu.vassar.cmpu203.ecoscoop.databinding.FragmentArticleFeedBinding;
import edu.vassar.cmpu203.ecoscoop.databinding.ItemArticleCardBinding;
import edu.vassar.cmpu203.ecoscoop.src.model.Article;
import edu.vassar.cmpu203.ecoscoop.src.model.Author;
import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

public class ArticleFeedFragment extends Fragment implements ArticleFeedUI {

    private FragmentArticleFeedBinding binding;
    private ArticleFeedUI.Listener listener;

    final ArticleFeedAdapter articleFeedAdapter = new ArticleFeedAdapter();

    public ArticleFeedFragment(){
        //Required Empty Public Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentArticleFeedBinding.inflate(inflater, container, false);

        return this.binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        this.binding.itemsRecView.setLayoutManager(new LinearLayoutManager((this.requireContext())));
        this.binding.itemsRecView.setAdapter(this.articleFeedAdapter);


    }

    @Override
    public void setListener(Listener listener) { this.listener = listener;}

    @Override
    public void runShowFeed(List<Article> articleList) {
        this.articleFeedAdapter.setArticles(articleList);

    }

    @Override
    public void runArticleClicked(int id) {

    }

    private class ArticleFeedAdapter extends RecyclerView.Adapter<ArticleFeedAdapter.ViewHolder> {

        private List<Article> articles = new ArrayList<>();

        //updates article feed
        public void setArticles(List<Article> newArticles) {
            this.articles = newArticles;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemArticleCardBinding cardBinding;

            ViewHolder(ItemArticleCardBinding cardBinding){
                super(cardBinding.getRoot());
                this.cardBinding = cardBinding;
            }


            //Creates InfoFeedCard
            void setCardBinding(Article article){

                //Sets Title
                cardBinding.cardTitle.setText(article.getTitle());

                //Sets Description
                String desc = article.getDescription();
                if (desc != null && !desc.isEmpty()) {
                    cardBinding.cardDescription.setText(desc);
                    cardBinding.cardDescription.setVisibility(View.VISIBLE);
                } else {
                    cardBinding.cardDescription.setVisibility(View.GONE);
                }

                //Sets Source + Date
                if (article.getSource() != null) {
                    cardBinding.cardSource.setText(article.getSource().getWebsiteName());
                    cardBinding.cardDate.setText(article.getSource().getPublishDate());
                }

                //Sets Authors
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

                //Sets First Tag
                List<Tag> tags = article.getTagList();
                if (tags != null && !tags.isEmpty()) {
                    cardBinding.cardTag.setText(tags.get(0).getName());
                    cardBinding.cardTag.setVisibility(View.VISIBLE);
                } else {
                    cardBinding.cardTag.setVisibility(View.GONE);
                }

                // Card tap — reports article ID to ControllerActivity
                cardBinding.getRoot().setOnClickListener(v -> {
                    Log.d("FeedDebug", "Card tapped, article id: " + article.getId() + ", listener: " + listener);
                    if (listener != null) {
                        listener.onArticleClicked(article.getId());
                    }
                });
            }


        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemArticleCardBinding cardBinding = ItemArticleCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(cardBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setCardBinding(articles.get(position));
        }

        @Override
        public int getItemCount() {
            return articles.size();
        }
    }






}
