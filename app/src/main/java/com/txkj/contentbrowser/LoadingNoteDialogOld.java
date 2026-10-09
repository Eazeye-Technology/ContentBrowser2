package com.txkj.contentbrowser;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import com.dseink.EinkUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.txkj.contentbrowser2.R;

public class LoadingNoteDialogOld {
    public static int getCenteredTitleThemeOverlay() {
        //return com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered;
        return R.style.MyThemeOverlayAlertDialog;
    }

    //private final static int WIN_WIDTH = 312 + 24 * 2;//312;

    private static Activity mContext;
    private static Runnable mRunnable;
    private static AlertDialog dialog;

    public static void show(Activity context, View anchor, Runnable runnable) {
        mContext = context;
        mRunnable = runnable;
        dialog = new MaterialAlertDialogBuilder(mContext, getCenteredTitleThemeOverlay())
                .setView(R.layout.activity_book4_popup_loading_note)
                .setCancelable(true)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (mRunnable != null) {
                    mRunnable.run();
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        try {
            Window window = dialog.getWindow();
            if (window != null) {
                //int WIN_WIDTH = mContext.getResources().getDimensionPixelSize(R.dimen.activity_dialog_upgrade_min_width);
                window.setLayout(350, 350);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        dialog.show();
        EinkUtils.centerToRightScreen400(context, dialog);
    }

    public static void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
