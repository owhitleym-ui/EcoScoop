package edu.vassar.cmpu203.ecoscoop.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.vassar.cmpu203.ecoscoop.databinding.FragmentAccessArticleBinding;

public class AccessArticleFragment extends Fragment {
    private FragmentAccessArticleBinding binding;

    // TODO: Rename and change types of parameters
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentAccessArticleBinding.inflate(inflater);
        return this.binding.getRoot();

    }
}