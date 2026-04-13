package edu.vassar.cmpu203.ecoscoop.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import edu.vassar.cmpu203.ecoscoop.databinding.FragmentAccessArticleBinding;
import edu.vassar.cmpu203.ecoscoop.databinding.FragmentScrollArticlesBinding;

class ScrollArticlesFragment extends Fragment {
    private FragmentScrollArticlesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        this.binding = FragmentScrollArticlesBinding.inflate(inflater);
        return this.binding.getRoot();
    }
    public void onViewCreated(View view, Bundle savedInstanceState){

    }


}