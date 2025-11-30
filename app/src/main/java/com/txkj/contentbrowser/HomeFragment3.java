package com.txkj.contentbrowser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.txkj.contentbrowser2.R;

public class HomeFragment3 extends Fragment {
    private final static String FILENAME_VIEW = "home/test6_2.html";
    private WebView wv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home3, container, false);

        wv = (WebView) view.findViewById(R.id.webView1);
        wv.loadUrl("file:///android_asset/" + FILENAME_VIEW);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}