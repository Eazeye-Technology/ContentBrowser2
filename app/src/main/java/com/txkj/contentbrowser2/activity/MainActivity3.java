package com.txkj.contentbrowser2.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codeteenager.systemsettings.SystemSettingFragment;
import com.foobnix.model.AppProfile;
import com.foobnix.pdf.info.Android6;
import com.foobnix.pdf.info.Android6Mod;
import com.getdirectory.DirectoryFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.android.material.snackbar.Snackbar;
import com.txkj.contentbrowser.AppsFragment;
import com.txkj.contentbrowser.BrowserFragment;
import com.txkj.contentbrowser.ChatFragment;
import com.txkj.contentbrowser.FirstFragment;
import com.txkj.contentbrowser.HomeFragment2;
import com.txkj.contentbrowser.HomeFragment3;
import com.txkj.contentbrowser.LibraryFragment2Book;
import com.txkj.contentbrowser.LibraryFragment2Pdf;
import com.txkj.contentbrowser.NoteFragment2;
import com.txkj.contentbrowser.SecondFragment;
import com.txkj.contentbrowser.SettingFragment;
import com.txkj.contentbrowser2.R;
import com.txkj.smartanswer.AnswerFragment;

import java.io.File;
import java.util.ArrayList;

import io.material.catalog.navigationrail.NavigationRailSubMenuDemoFragment;

public class MainActivity3 extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context context) {
        AppProfile.init(context); //for recent files search
        super.attachBaseContext(context);
    }

    NavigationRailView navigationRailView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_3);
        this.getSupportActionBar().hide();

        navigationRailView = this.findViewById(R.id.cat_navigation_rail);
        // Add extended floating action button
        navigationRailView.addHeaderView(R.layout.fragment_home_3_header_view);
        //navigationRailView.expand();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) navigationRailView.getHeaderView().getLayoutParams();
        lp.gravity = Gravity.CENTER_HORIZONTAL; //Gravity.START;
        navigationRailView.getHeaderView().findViewById(R.id.cat_navigation_rail_efab_container)
                .setPadding(
                        0, //navigationRailView.getItemActiveIndicatorExpandedMarginHorizontal(),
                        0,
                        0, //navigationRailView.getItemActiveIndicatorExpandedMarginHorizontal(),
                        0);
        ExtendedFloatingActionButton efab = navigationRailView.getHeaderView().findViewById(R.id.cat_navigation_rail_efab);
        efab.setAnimationEnabled(false);
        efab.setExtended(false);
        efab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, R.string.cat_navigation_rail_efab_message, Snackbar.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    if (false) {
                        intent.setClassName("com.txkj.notemobile2",
                                "com.txkj.notemobile2.BookListActivity");
                    } else if (false) {
                        intent.setClassName("com.txkj.notemobile",//"online.xournal.mobile",
                                "com.txkj.notemobile.MainActivity");//"online.xournal.mobile.MainActivity");
                    } else {
                        intent.setClassName("com.txkj.drawingapp",
                                "com.txkj.notemobile2.BookListActivity");
                    }

                    intent.putExtra("APP_OPEN", "NEW");

                    //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        ImageView button = this.findViewById(R.id.cat_navigation_rail_expand_button);
        button.setContentDescription(getResources().getString(R.string.cat_navigation_rail_expand_button_description));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (efab.isExtended()) {
                    efab.shrink();
                    navigationRailView.collapse();
                    button.setContentDescription(getResources().getString(R.string.cat_navigation_rail_expand_button_description));
                    button.setImageResource(R.drawable.ic_drawer_menu_24px);
                } else {
                    efab.extend();
                    navigationRailView.expand();
                    button.setContentDescription(getResources().getString(R.string.cat_navigation_rail_collapse_button_description));
                    button.setImageResource(R.drawable.ic_drawer_menu_open_24px);
                }
            }
        });
        navigationRailView.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.performClick();
            }
        }, 0);




        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.ll_search_wx_global).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.ll_search_wx_global).setVisibility(View.GONE);
                    findViewById(R.id.tvTitleText).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.ll_search_wx_global).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvTitleText).setVisibility(View.GONE);
                }
            }
        });
        navigationRailView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onClick2(item.getItemId(), true);
                return false;
            }
        });



        //https://github.com/dibakarece/AndroidFileExplorer
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();

        mFirstFragment = new FirstFragment();
        mSecondFragment = new SecondFragment();

        mHomeFragment = new HomeFragment2();
        mBrowserFragment = new BrowserFragment();
        mSettingFragment = new SettingFragment();
        mNoteFragment = new NoteFragment2();
        mLibraryFragment2Book = new LibraryFragment2Book();
        mLibraryFragment2Pdf = new LibraryFragment2Pdf();
        mChatFragment = new ChatFragment();
        mAppsFragment = new AppsFragment();
        mHome3Fragment = new HomeFragment3();
        mAnswerFragment = new AnswerFragment();
        mSystemSettingFragment = new SystemSettingFragment();

        mDirectoryFragment = new DirectoryFragment();
        mDirectoryFragment.setDelegate(new DirectoryFragment.DocumentSelectActivityDelegate() {

            @Override
            public void startDocumentSelectActivity() {

            }

            @Override
            public void didSelectFiles(DirectoryFragment activity,
                                       ArrayList<String> files) {
//                mDirectoryFragment.showErrorBox(files.get(0).toString());
                if (false) {
                    mDirectoryFragment.showErrorBox(files.get(0).toString());
                } else {
                    if (files != null && files.size() > 0) {
                        try {
                            //https://github.com/microsoft/Visual-Audience-Polling/blob/db9536339145aa87a526c1a5fbf207e730ca7859/src/RosterList.java#L379
                            // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
                            // file
                            // browser.
                            Intent intent = new Intent(Intent.ACTION_VIEW); //(Intent.ACTION_OPEN_DOCUMENT);
                            // Filter to only show results that can be "opened", such as a
                            // file (as opposed to a list of contacts or timezones)
                            //----//intent.addCategory(Intent.CATEGORY_OPENABLE);
                            // Filter to show only images, using the image MIME data type.
                            // If one wanted to search for ogg vorbis files, the type would be
                            // "audio/ogg".
                            // To search for all documents available via installed storage
                            // providers,
                            // it would be "*/*".
                            // Mime Types : https://developers.google.com/drive/web/mime-types
                            intent.setType("*/*");
                            intent.setData(Uri.fromFile(
                                    new File(files.get(0))));

                            //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            // Intent resultData = intent;
                            startActivity(intent);
                        } catch (Throwable eee) {
                            eee.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void updateToolBarName(String name) {
                getSupportActionBar().setTitle(name);
            }
        });

        this.currentTabId = R.id.action_page_1;
        if (savedInstanceState != null) {
            this.currentTabId = savedInstanceState.getInt(KEY_CURRENT_TAB_ID, R.id.action_page_1);
        }
        if (false) {
            fragmentTransaction.add(R.id.content_layout, mDirectoryFragment, "" + mDirectoryFragment.toString());
            fragmentTransaction.commit();
        }
        onClick2(this.currentTabId, false);

        int stateStarted = 0;
        if (savedInstanceState != null) {
            stateStarted = savedInstanceState.getInt(STATE_STARTED, 0);
        }

        if (stateStarted == 0) {
            checkPermission();
        }
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
    //原文链接：https://blog.csdn.net/zuo_er_lyf/article/details/82659426
    //https://www.dev2qa.com/android-read-write-external-storage-file-example/
    private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;
    private void checkPermission(){
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // If do not grant write external storage permission.
        if (writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Android6Mod.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    // 权限已授予
                } else {
                    // 权限未授予
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


    private final static String KEY_CURRENT_TAB_ID = "KEY_CURRENT_TAB_ID";
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_TAB_ID, currentTabId);
        outState.putInt(STATE_STARTED, 1);
    }

    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private DirectoryFragment mDirectoryFragment;
    private FirstFragment mFirstFragment;
    private SecondFragment mSecondFragment;

    private HomeFragment2 mHomeFragment;
    private BrowserFragment mBrowserFragment;
    private SettingFragment mSettingFragment;
    private NoteFragment2 mNoteFragment;
    private LibraryFragment2Book mLibraryFragment2Book;
    private LibraryFragment2Pdf mLibraryFragment2Pdf;
    private ChatFragment mChatFragment;
    private AppsFragment mAppsFragment;
    private HomeFragment3 mHome3Fragment;
    private AnswerFragment mAnswerFragment;
    private SystemSettingFragment mSystemSettingFragment;

    private int currentTabId = R.id.action_page_1;
    //@Override
    public void onClick2(int id, boolean isClick) {
        this.currentTabId = id;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        TextView tvTitleText = (TextView) findViewById(R.id.tvTitleText);
        if (id == R.id.action_page_1) {
            tvTitleText.setText("Home");
            fragmentTransaction.replace(R.id.content_layout, mAnswerFragment);//mHome3Fragment); //mHomeFragment
            fragmentTransaction.commit();
        } else if (id == R.id.action_page_2) {
            tvTitleText.setText("Notes");
            fragmentTransaction.replace(R.id.content_layout, mNoteFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.action_page_3) {
            tvTitleText.setText("Books");
            fragmentTransaction.replace(R.id.content_layout, mLibraryFragment2Book);
            fragmentTransaction.commit();
        } else if (id == R.id.action_page_4) {
            tvTitleText.setText("PDFs");
            fragmentTransaction.replace(R.id.content_layout, mLibraryFragment2Pdf);
            fragmentTransaction.commit();
        } else if (id == R.id.action_page_5) {
            tvTitleText.setText("Storage");
            fragmentTransaction.replace(R.id.content_layout, mDirectoryFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.action_page_6) {
            tvTitleText.setText("Apps");
            fragmentTransaction.replace(R.id.content_layout, mAppsFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.action_page_7) {
            tvTitleText.setText("Settings");
            fragmentTransaction.replace(R.id.content_layout, mSystemSettingFragment);//mSettingFragment);
            fragmentTransaction.commit();
        }
    }

    //https://github.com/microsoft/Visual-Audience-Polling/blob/db9536339145aa87a526c1a5fbf207e730ca7859/src/RosterList.java#L379
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
        // file
        // browser.

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be
        // "audio/ogg".
        // To search for all documents available via installed storage
        // providers,
        // it would be "*/*".

        // Mime Types : https://developers.google.com/drive/web/mime-types
        intent.setType("*/*");
        // Intent resultData = intent;
//        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    @Override
    protected void onDestroy() {
        mDirectoryFragment.onFragmentDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDirectoryFragment.onBackPressed_()) {
            super.onBackPressed();
        }
    }

    public void jumpNote() {
        onClick2(R.id.menu_bar_item_2, true);
    }

    public void jumpViewPdf() {
        onClick2(R.id.menu_bar_item_4, true);
    }





















}
