package com.baontq.mob201.ui.news;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.baontq.mob201.databinding.FragmentWebViewBinding;
import com.kongzue.dialogx.dialogs.PopTip;

public class WebViewFragment extends Fragment {

    private FragmentWebViewBinding binding;
    private WebView webView;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWebViewBinding.inflate(inflater, container, false);
        webView = binding.wvNews;
        if (savedInstanceState == null) {
            webView.loadUrl(getArguments().getString("link"));
        } else {
            webView.restoreState(savedInstanceState);
        }

        binding.ivBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
        return binding.getRoot();
    }

}