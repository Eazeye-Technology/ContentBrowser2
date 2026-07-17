package com.getdirectory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.txkj.contentbrowser2.R;

public class CopyCutMenuDialog {
    private static final int POPUP_OFFSET_X = 0;//30;
    private static final int POPUP_OFFSET_Y = 0;//30;

    public static boolean isOpen = false;
    private static View.OnClickListener mListener = null;
    private static PopupWindow popup;
    public static void show(Context context, View anchor, final View.OnClickListener listener,
                            boolean isEnablePaste, final View.OnClickListener listener2) {
        //View layout = View.inflate(context, R.layout.activity_main_menu1, null);
        View layout = View.inflate(context, R.layout.activity_manipulation_menu, null);
        //View wv = layout.findViewById(R.id.wheel);

        mListener = listener;
        final int[] ids = {
                R.id.popTextViewSelectItems,
                R.id.popTextViewSelectAll,
                R.id.popButtonDeselectAll,

                R.id.popTextViewNewFolder,

                R.id.popTextViewCut,
                R.id.popTextViewCopy,
                R.id.popTextViewPaste,

                R.id.popTextViewRenameFile,

                R.id.popButtonDelete,
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

        //20260410: paste gray color
        ImageView ivMenuPaste = (ImageView) layout.findViewById(R.id.ivMenuPaste);
        TextView tvMenuPaste = (TextView) layout.findViewById(R.id.tvMenuPaste);
        //this.copyPaths.isEmpty()
        if (isEnablePaste) {
            ivMenuPaste.clearColorFilter();
            tvMenuPaste.setTextColor(Color.BLACK);
        } else {
            ivMenuPaste.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            tvMenuPaste.setTextColor(Color.LTGRAY);
        }

        int menuWidth = context.getResources().getDimensionPixelSize(R.dimen.activity_book4_popup_menu_width);
        int offsetX = context.getResources().getDimensionPixelSize(R.dimen.activity_book4_popup_menu_offsetx);
        int offsetY = context.getResources().getDimensionPixelSize(R.dimen.activity_book4_popup_menu_offsety);
        //  make a popup window
        popup = new PopupWindow(layout,
                menuWidth/*300 ViewGroup.LayoutParams.WRAP_CONTENT*/, ViewGroup.LayoutParams.WRAP_CONTENT);

        //shadow
        popup.setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
        popup.setElevation(10);

        popup.setFocusable(true);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOpen = false;
                if (listener2 != null) {
                    listener2.onClick(null);
                }
            }
        });

        //  now show the popup
        popup.showAsDropDown(anchor, offsetX, offsetY, Gravity.RIGHT);
    }
//    public interface WidthChangedListener {
//        void onWidthChanged(float value);
//    }
}
