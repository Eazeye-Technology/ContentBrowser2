package com.txkj.contentbrowser2.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.txkj.contentbrowser2.R;

public class CopyCutMenuDialog {
    private static final int POPUP_OFFSET = 0;//30;

    public static void show(Context context, View anchor, final WidthChangedListener listener) {
        View layout = View.inflate(context, R.layout.activity_main_menu1, null);
        //View wv = layout.findViewById(R.id.wheel);

        //  make a popup window
        final PopupWindow popup = new PopupWindow(layout,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);

        //  now show the popup
        popup.showAsDropDown(anchor, POPUP_OFFSET, POPUP_OFFSET);
    }
    public interface WidthChangedListener {
        void onWidthChanged(float value);
    }
}
