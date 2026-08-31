package com.txkj.contentbrowser2.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codeteenager.systemsettings.SystemSettingFragment;
import com.dseink.DualScreenConstant;
import com.dseink.EinkUtils;
import com.foobnix.model.AppProfile;
import com.foobnix.pdf.info.Android6Mod;
import com.foobnix.pdf.info.ExtUtils;
import com.getdirectory.DirectoryFragment2;
import com.txkj.contentbrowser.AppsFragment;
import com.txkj.contentbrowser.BrowserFragment;
import com.txkj.contentbrowser.ChatFragment;
import com.txkj.contentbrowser.FirstFragment;
import com.txkj.contentbrowser.HomeFragment;
import com.txkj.contentbrowser.HomeFragment2;
import com.txkj.contentbrowser.HomeFragment3;
import com.txkj.contentbrowser.HomeFragment4;
import com.txkj.contentbrowser.HomeFragment61;
import com.txkj.contentbrowser.HomeFragment62;
import com.txkj.contentbrowser.LibraryFragment2Book;
import com.txkj.contentbrowser.LibraryFragment2Pdf;
import com.txkj.contentbrowser.NoteFragment2;
import com.txkj.contentbrowser.SecondFragment;
import com.txkj.contentbrowser.SettingFragment;
import com.txkj.contentbrowser2.R;
import com.txkj.smartanswer.AnswerFragment;
import com.upgradetool.upgrade.UpgradeUtil;

import java.io.File;
import java.util.ArrayList;

public class MainActivity6 extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context context) {
        AppProfile.init(context); //for recent files search
        super.attachBaseContext(context);
    }

    public UpgradeUtil upgradeUtil;

    private void hideNavigation() {
        final View decorView = this.getWindow().getDecorView();
        decorView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 19) {
                    decorView.setSystemUiVisibility(//
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE //
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN//
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);//
                } else {
                    decorView.setSystemUiVisibility( //
                            View.SYSTEM_UI_FLAG_LOW_PROFILE //
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN); //
                }
            }
        }, 100);
    }

    //FIXME:added
    private void setupPadding() {
        if (true) {
            //root_layout
            LinearLayout rootLayout = findViewById(R.id.root_layout);
            rootLayout.setPadding(0, 50, 0, 0);
        }
    }

    HomeFragment61 mHomeFragment61;
    HomeFragment62 mHomeFragment62;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction1 = null;
    private FragmentTransaction fragmentTransaction2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home6);
        setupPadding();
        //hideNavigation();
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().hide();
        }
        if (UpgradeUtil.USE_UPGRADE) {
            upgradeUtil = new UpgradeUtil(this);
            upgradeUtil.onCreate_upgrade();
        }

        mHomeFragment61 = new HomeFragment61();
        mHomeFragment62 = new HomeFragment62();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction2 = fragmentManager.beginTransaction();

        fragmentTransaction1.replace(R.id.content_layout1, mHomeFragment61);//mHomeFragment); //mHomeFragment, mHomeFragment2
        fragmentTransaction1.commit();

        findViewById(R.id.content_layout2).postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentTransaction2.replace(R.id.content_layout2, mHomeFragment62);//mHomeFragment); //mHomeFragment, mHomeFragment2
                fragmentTransaction2.commit();
            }
        }, 100);

        int stateStarted = 0;
        if (savedInstanceState != null) {
            stateStarted = savedInstanceState.getInt(STATE_STARTED, 0);
        }

        if (stateStarted == 0) {
            checkPermission();

            if (UpgradeUtil.USE_UPGRADE) {
                if (upgradeUtil != null) {
                    upgradeUtil.onCreateUpdateReceiver();
                    upgradeUtil.checkVersion();
                }
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (UpgradeUtil.USE_UPGRADE) {
            Dialog dialog = upgradeUtil.onCreateDailog_upgrade(id);
            if (dialog != null) {
                return dialog;
            }
        }
        return super.onCreateDialog(id);
    }


    /*
com.foobnix.pdf.info.Android6
if (!Android6.canWrite(this)) {
	Android6.checkPermissions(this, true);
	return;
}
@Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
	Android6.onRequestPermissionsResult(this, i, strArr, iArr);
}
     */
    private static final String STATE_STARTED = "STATE_STARTED";
    //https://blog.csdn.net/zuo_er_lyf/article/details/82659426
    //https://www.dev2qa.com/android-read-write-external-storage-file-example/
    private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;
    private void checkPermission(){
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int writeExternalStoragePermission2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        int writeExternalStoragePermission3 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.REQUEST_INSTALL_PACKAGES);
        // If do not grant write external storage permission.
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED ||
                writeExternalStoragePermission2 != PackageManager.PERMISSION_GRANTED ||
                writeExternalStoragePermission3 != PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES,
            }, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            getPermission2();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Android6Mod.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0  &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
                getPermission2();
            } else {
                //Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
                getPermission2();
            }
        }
    }

    private final static int REQUEST_CODE = 1111;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {

                }
            }
        }
    }
    private void getPermission2(){
        if (false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        } else {
            if (!Android6Mod.canWrite(this)) {
                Android6Mod.checkPermissions(this, true);
                return;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_STARTED, 1);
    }

    @Override
    public void onDestroy() {
        ExtUtils.setContext(null);
        super.onDestroy();
        if (UpgradeUtil.USE_UPGRADE && upgradeUtil != null) {
            upgradeUtil.onDestroyUpdateReceiver();
        }
    }

    @Override
    public void onBackPressed() {
        if (mHomeFragment61 != null && mHomeFragment61.onBackPressed()) {
            super.onBackPressed();
        } else if (mHomeFragment62 != null && mHomeFragment62.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
