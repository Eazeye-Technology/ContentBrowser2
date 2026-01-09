package com.txkj.contentbrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;

import com.foobnix.model.AppProfile;
import com.getdirectory.DirectoryFragment;
import com.txkj.contentbrowser2.R;
import com.txkj.smartanswer.AnswerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context context) {
        AppProfile.init(context); //for recent files search
        super.attachBaseContext(context);
    }

    private final static int icons[] = {
            R.id.function_bar_item_home,
            R.id.function_bar_item_library,
            //R.id.function_bar_item_shop,
            R.id.function_bar_item_note,
            R.id.function_bar_item_storage,
            //R.id.function_bar_item_apps,
            R.id.function_bar_item_browser,
            R.id.function_bar_item_chat,
            R.id.function_bar_item_setting,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            this.getSupportActionBar().hide();
        }

        this.findViewById(R.id.function_bar_item_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick2(R.id.function_bar_item_home, true);
            }
        });
        this.findViewById(R.id.function_bar_item_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick2(R.id.function_bar_item_library, true);
            }
        });
        //this.findViewById(R.id.function_bar_item_shop).setOnClickListener(this);
        this.findViewById(R.id.function_bar_item_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick2(R.id.function_bar_item_note, true);
            }
        });
        this.findViewById(R.id.function_bar_item_storage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick2(R.id.function_bar_item_storage, true);
            }
        });
        //this.findViewById(R.id.function_bar_item_apps).setOnClickListener(this);
        this.findViewById(R.id.function_bar_item_browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick2(R.id.function_bar_item_browser, true);
            }
        });
        this.findViewById(R.id.function_bar_item_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick2(R.id.function_bar_item_setting, true);
            }
        });
        this.findViewById(R.id.function_bar_item_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick2(R.id.function_bar_item_chat, true);
            }
        });

        View viewIcon = this.findViewById(icons[0]);
        if (viewIcon != null) {
            viewIcon.findViewWithTag("binding_5").setVisibility(View.VISIBLE);
            viewIcon.findViewWithTag("binding_1").setActivated(true);
        }



        //https://github.com/dibakarece/AndroidFileExplorer
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();

        mFirstFragment = new FirstFragment();
        mSecondFragment = new SecondFragment();

        mHomeFragment = new HomeFragment();
        mBrowserFragment = new BrowserFragment();
        mSettingFragment = new SettingFragment();
        mNoteFragment = new NoteFragment();
        mLibraryFragment = new LibraryFragment();
        mChatFragment = new ChatFragment();
        mAnswerFragment = new AnswerFragment();

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
                if (getSupportActionBar() != null) {
                    MainActivity.this.getSupportActionBar().setTitle(name);
                }
            }
        });

        if (false) {
            fragmentTransaction.add(R.id.content_layout, mDirectoryFragment, "" + mDirectoryFragment.toString());
            fragmentTransaction.commit();
        }
        this.currentTabId = R.id.function_bar_item_home;
        if (savedInstanceState != null) {
            this.currentTabId = savedInstanceState.getInt(KEY_CURRENT_TAB_ID, R.id.function_bar_item_home);
        }
        onClick2(this.currentTabId, false);

        this.findViewById(R.id.viewCollapse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View function_bar = findViewById(R.id.function_bar);
                if (function_bar != null) {
                    if (function_bar.getVisibility() == View.VISIBLE) {
                        function_bar.setVisibility(View.GONE);
                    } else {
                        function_bar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        int stateStarted = 0;
        if (savedInstanceState != null) {
            stateStarted = savedInstanceState.getInt(STATE_STARTED, 0);
        }

        if (stateStarted == 0) {
            checkPermissioin();
        }
    }
    private static final String STATE_STARTED = "STATE_STARTED";
    //原文链接：https://blog.csdn.net/zuo_er_lyf/article/details/82659426
    //https://www.dev2qa.com/android-read-write-external-storage-file-example/
    private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;
    private void checkPermissioin(){
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
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
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

    private AnswerFragment mAnswerFragment;
    private HomeFragment mHomeFragment;
    private BrowserFragment mBrowserFragment;
    private SettingFragment mSettingFragment;
    private NoteFragment mNoteFragment;
    private LibraryFragment mLibraryFragment;
    private ChatFragment mChatFragment;

    private int currentTabId = R.id.function_bar_item_home;
    //@Override
    public void onClick2(int id, boolean isClick) {
        this.currentTabId = id;
        View view = findViewById(id);
        for (int i = 0; i < icons.length; ++i) {
            View viewIcon = this.findViewById(icons[i]);
            if (viewIcon != null) {
                viewIcon.findViewWithTag("binding_5").setVisibility(View.GONE);
                viewIcon.findViewWithTag("binding_1").setActivated(false);
            }
        }
        if (view != null) {
            //if (Arrays.asList(icons).contains(view.getId())) {
            if (view.findViewWithTag("binding_5") != null &&
                    view.findViewWithTag("binding_1") != null) {
                view.findViewWithTag("binding_5").setVisibility(View.VISIBLE);
                view.findViewWithTag("binding_1").setActivated(true);
            }
        }
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (id == R.id.function_bar_item_home) {
            fragmentTransaction.replace(R.id.content_layout, mHomeFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.function_bar_item_library) {
            fragmentTransaction.replace(R.id.content_layout, mLibraryFragment);
            fragmentTransaction.commit();

//                if (false) { //isClick) {
//                    try {
//                        Intent intent = new Intent();
////                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                        intent.setClassName("com.foobnix.pdf.info",
//                                "com.foobnix.ui2.MainTabs2");
//
//                        //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
//                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                        startActivity(intent);
//                    } catch (ActivityNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
        } else if (id == R.id.function_bar_item_note) {
            //R.id.function_bar_item_shop,
            fragmentTransaction.replace(R.id.content_layout, mNoteFragment);
            fragmentTransaction.commit();

            if (isClick) {
//                    try {
//                        Intent intent = new Intent();
////                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                        intent.setClassName("online.xournal.mobile",
//                                "online.xournal.mobile.MainActivity");
//                        startActivity(intent);
//                    } catch (ActivityNotFoundException e) {
//                        e.printStackTrace();
//                    }
            }
        } else if (id == R.id.function_bar_item_storage) {
            //R.id.function_bar_item_apps,
            fragmentTransaction.replace(R.id.content_layout, mDirectoryFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.function_bar_item_browser) {
            fragmentTransaction.replace(R.id.content_layout, mBrowserFragment);
            fragmentTransaction.commit();

            if (isClick) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri targetUrl = Uri.parse("about:blank");
                    intent.setData(targetUrl);

                    //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.function_bar_item_chat) {
            fragmentTransaction.replace(R.id.content_layout, mChatFragment);
            fragmentTransaction.commit();

            if (isClick) {
                try {
                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setClassName("com.txkj.smartanswer",
                            "com.txkj.smartanswer.MainActivity");

                    //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.function_bar_item_setting) {
                fragmentTransaction.replace(R.id.content_layout, mSettingFragment);
                fragmentTransaction.commit();

                if (isClick) {
                    try {
                        //https://blog.csdn.net/netwalk/article/details/139489074
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);

                        //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
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
        onClick2(R.id.function_bar_item_note, true);
    }

    public void jumpViewPdf() {
        onClick2(R.id.function_bar_item_library, true);
    }

    public void jumpSettings() {
        onClick2(R.id.menu_bar_item_7, true);
    }

    public void jumpAiChat() {
        //fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, mAnswerFragment);
        fragmentTransaction.commit();
    }
}