package com.txkj.contentbrowser2.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.foobnix.model.AppProfile;
import com.getdirectory.DirectoryFragment;
import com.txkj.contentbrowser.BrowserFragment;
import com.txkj.contentbrowser.ChatFragment;
import com.txkj.contentbrowser.FirstFragment;
import com.txkj.contentbrowser.HomeFragment;
import com.txkj.contentbrowser.HomeFragment2;
import com.txkj.contentbrowser.LibraryFragment2Book;
import com.txkj.contentbrowser.LibraryFragment2Pdf;
import com.txkj.contentbrowser.NoteFragment2;
import com.txkj.contentbrowser.SecondFragment;
import com.txkj.contentbrowser.SettingFragment;
import com.txkj.contentbrowser2.R;

import java.io.File;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context context) {
        AppProfile.init(context); //for recent files search
        super.attachBaseContext(context);
    }

    private final static int icons[] = {
            R.id.menu_bar_item_1,
            R.id.menu_bar_item_2,
            R.id.menu_bar_item_3,
            R.id.menu_bar_item_4,
            R.id.menu_bar_item_5,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home2);
        this.getSupportActionBar().hide();

        for (int id : icons) {
            this.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick2(id, true);
                }
            });
        }

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

        mHomeFragment = new HomeFragment2();
        mBrowserFragment = new BrowserFragment();
        mSettingFragment = new SettingFragment();
        mNoteFragment = new NoteFragment2();
        mLibraryFragment2Book = new LibraryFragment2Book();
        mLibraryFragment2Pdf = new LibraryFragment2Pdf();
        mChatFragment = new ChatFragment();

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

        if (false) {
            fragmentTransaction.add(R.id.content_layout, mDirectoryFragment, "" + mDirectoryFragment.toString());
            fragmentTransaction.commit();
        }
        this.currentTabId = icons[0];
        if (savedInstanceState != null) {
            this.currentTabId = savedInstanceState.getInt(KEY_CURRENT_TAB_ID, R.id.function_bar_item_home);
        }
        onClick2(this.currentTabId, false);

        this.findViewById(R.id.top_left_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.left_menu) != null) {
                    if (findViewById(R.id.left_menu).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.left_menu).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.left_menu).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        View button = this.findViewById(R.id.btnTitleNewBook);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private HomeFragment2 mHomeFragment;
    private BrowserFragment mBrowserFragment;
    private SettingFragment mSettingFragment;
    private NoteFragment2 mNoteFragment;
    private LibraryFragment2Book mLibraryFragment2Book;
    private LibraryFragment2Pdf mLibraryFragment2Pdf;
    private ChatFragment mChatFragment;

    private int currentTabId = R.id.function_bar_item_home;
    //@Override
    public void onClick2(int id, boolean isClick) {
        this.currentTabId = id;
        View view = findViewById(id);
        for (int i = 0; i < icons.length; ++i) {
            View viewIcon = this.findViewById(icons[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0x00888888);
                ((CardView) viewIcon).setCardElevation(0.0f);
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((AppCompatImageView)viewIcon.findViewWithTag("binding_1")).setBackgroundResource(getActiveIconId(icons[i], false));
                }
                if (viewIcon.findViewWithTag("binding_5") != null) {
                    ((TextView)viewIcon.findViewWithTag("binding_5")).setTextColor(0xFF000000);
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF888888);
            ((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((AppCompatImageView)view.findViewWithTag("binding_1")).setBackgroundResource(getActiveIconId(id, true));
            }
            if (view.findViewWithTag("binding_5") != null) {
                ((TextView)view.findViewWithTag("binding_5")).setTextColor(0xFFFFFFFF);
            }
        }
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        TextView tvTitleText = (TextView) findViewById(R.id.tvTitleText);
        switch (id) {
            case R.id.menu_bar_item_1:
                tvTitleText.setText("Recent");
                fragmentTransaction.replace(R.id.content_layout, mHomeFragment);
                fragmentTransaction.commit();
                break;

            case R.id.menu_bar_item_2:
                tvTitleText.setText("Notes");
                fragmentTransaction.replace(R.id.content_layout, mNoteFragment);
                fragmentTransaction.commit();
                break;

            case R.id.menu_bar_item_3:
                tvTitleText.setText("Books");
                fragmentTransaction.replace(R.id.content_layout, mLibraryFragment2Book);
                fragmentTransaction.commit();
                break;

            case R.id.menu_bar_item_4:
                tvTitleText.setText("PDFs");
                fragmentTransaction.replace(R.id.content_layout, mLibraryFragment2Pdf);
                fragmentTransaction.commit();
                break;

            case R.id.menu_bar_item_5:
                tvTitleText.setText("Paper 2");
                fragmentTransaction.replace(R.id.content_layout, mBrowserFragment);
                fragmentTransaction.commit();
                break;
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























    public int getActiveIconId(int id, boolean isActive) {
        switch (id) {
            case R.id.menu_bar_item_1: return isActive ? R.drawable.ic_baseline_access_time_24_white: R.drawable.ic_baseline_access_time_24;
            case R.id.menu_bar_item_2: return isActive ? R.drawable.ic_outline_sticky_note_2_24_white: R.drawable.ic_outline_sticky_note_2_24;
            case R.id.menu_bar_item_3: return isActive ? R.drawable.ic_baseline_book_24_white: R.drawable.ic_baseline_book_24;
            case R.id.menu_bar_item_4: return isActive ? R.drawable.ic_baseline_picture_as_pdf_24_white: R.drawable.ic_baseline_picture_as_pdf_24;
            case R.id.menu_bar_item_5: return isActive ? R.drawable.ic_baseline_phone_android_24_white: R.drawable.ic_baseline_phone_android_24;
        }
        return 0;
    }

}
