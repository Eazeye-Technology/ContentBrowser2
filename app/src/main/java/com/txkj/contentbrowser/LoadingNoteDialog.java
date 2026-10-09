package com.txkj.contentbrowser;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.dseink.DualScreenConstant;
import com.dseink.EinkUtils;
import com.txkj.contentbrowser2.R;

public class LoadingNoteDialog {
    private final static boolean USE_LAYER = true;

    private static final int POPUP_OFFSET_X = 0;//30;
    private static final int POPUP_OFFSET_Y = 0;//30;

    public static boolean isOpen = false;
    private static View.OnClickListener mListener = null;
    private static PopupWindow popup;
    private static View myLoadingView;
    public static void show(Context context, View anchor, Runnable runnable, View loadingView) {
        if (USE_LAYER) {
            if (loadingView != null) {
                myLoadingView = loadingView;
                loadingView.setVisibility(View.VISIBLE);
                loadingView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (myLoadingView != null) {
                            myLoadingView.setVisibility(View.GONE);
                        }
                    }
                });
                loadingView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                }, 100);
            }
        } else {
            View layout = View.inflate(context, R.layout.activity_book4_popup_loading_note, null);

            //  make a popup window
            popup = new PopupWindow(layout,
                    350, 350);//ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popup.setFocusable(true);
            //popup.setOutsideTouchable(false);
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isOpen = false;
                }
            });

            //  now show the popup
            if (context instanceof Activity && EinkUtils.getCurrentScreenPos((Activity) context, 0) ==
                    DualScreenConstant.EXTRA_LAUNCH_SCREEN_PANEL_BOTH) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                popup.showAtLocation(anchor, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0 + screenWidth / 4, 0);
            } else {
                popup.showAtLocation(anchor, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
            anchor.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }, 100);
        }
    }

    public static void dismiss() {
        if (popup != null) {
            popup.dismiss();
        }
        if (myLoadingView != null) {
            myLoadingView.setVisibility(View.GONE);
        }
    }
}
