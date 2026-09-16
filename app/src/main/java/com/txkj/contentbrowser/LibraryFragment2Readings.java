package com.txkj.contentbrowser;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tvg.AutoWrapViewGroup;
import com.txkj.contentbrowser2.R;

public class LibraryFragment2Readings extends LibraryFragment2Book {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        AutoWrapViewGroup autoWrapViewGroup = (AutoWrapViewGroup) view.findViewById(R.id.autoWrapViewGroup);
        autoWrapViewGroup.clearViews();
        autoWrapViewGroup.output("Readings", "");
        return view;
    }

    public void showPopupMenuNoteFragment2(View view) {
        //do nothing
    }

    @Override
    public int getInitIndex() {
        return 0; //0:all, 1:pdf, 2:epub(book)
    }
}