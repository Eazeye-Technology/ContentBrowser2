package com.txkj.contentbrowser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.txkj.contentbrowser2.R;

public class HomeFragment4MenuDialog {
    private static final int POPUP_OFFSET = 0;//30;

    private static boolean isOpen = false;
    private static View.OnClickListener mListener = null;
    private static PopupWindow popup;
    public static void show(Context context, View anchor, final View.OnClickListener listener) {
        if (isOpen == true) {
            return;
        }
        isOpen = true;
        //View layout = View.inflate(context, R.layout.activity_main_menu1, null);
        View layout = View.inflate(context, R.layout.home4_popup_menu, null);
        //View wv = layout.findViewById(R.id.wheel);

        mListener = listener;
        final int[] ids = {
                R.id.llSortedName1,
                R.id.llSortedName2,
                R.id.llSortedType1,
                R.id.llSortedCreationDate1,
                R.id.llSortedCreationDate2,
                R.id.llSortedLastOpened1,
                R.id.llSortedLastOpened2,
                R.id.llSortedLastModified1,
                R.id.llSortedLastModified2,
        };
        for (int id : ids) {
            View popButton = layout.findViewById(id); //R.id.popButtonGrid
            popButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onClick(view);
                    }
                    try {
                        if (popup != null) {
                            popup.dismiss();
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            });
        }
//        TextView popTextViewPageInfo = (TextView) layout.findViewById(R.id.popTextViewPageInfo);
//        if (popTextViewPageInfo != null) {
//            //textViewPageInfo.setText("" + (getPageIdx() + 1) + "/" + pageNum);
//            popTextViewPageInfo.setText("" + pageIndex + "/" + pageNum);
//        }

        //  make a popup window
        popup = new PopupWindow(layout,
                300/*ViewGroup.LayoutParams.WRAP_CONTENT*/, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOpen = false;
            }
        });

        //  now show the popup
        popup.showAsDropDown(anchor, POPUP_OFFSET, POPUP_OFFSET);
    }
//    public interface WidthChangedListener {
//        void onWidthChanged(float value);
//    }
}
