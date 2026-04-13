package edu.vassar.cmpu203.ecoscoop.src.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import edu.vassar.cmpu203.ecoscoop.databinding.ExampleFeedBinding;

public class AccessArticleFragment extends Fragment implements AccessArticleUI {
    private ExampleFeedBinding binding;
    AccessArticleUI.Listener listener;

    public AccessArticleFragment() {
        //Required Empty Public Constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = ExampleFeedBinding.inflate(inflater);
        return this.binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.feedCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (listener != null) listener.onDisplayArticle();

            }
        });

    }


    public void onClick(View view){

    }

    @Override
    public void setListener(Listener listener) { this.listener = listener;}

    @Override
    public void runDisplayArticle() {

    }


}