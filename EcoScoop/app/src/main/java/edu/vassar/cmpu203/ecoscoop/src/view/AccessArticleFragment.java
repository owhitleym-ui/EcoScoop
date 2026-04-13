package edu.vassar.cmpu203.ecoscoop.src.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.vassar.cmpu203.ecoscoop.databinding.ExampleArticleBinding;

public class AccessArticleFragment extends Fragment implements AccessArticleUI {
    private ExampleArticleBinding binding;
    private Listener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = ExampleArticleBinding.inflate(inflater);
        return this.binding.getRoot();

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        binding.feedCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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