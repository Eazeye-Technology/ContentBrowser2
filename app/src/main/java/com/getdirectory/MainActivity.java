package com.getdirectory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.getdirectory.DirectoryFragment.DocumentSelectActivityDelegate;
import com.txkj.contentbrowser2.R;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private DirectoryFragment mDirectoryFragment;

    public void hideNavigation() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0x00CCCCCC);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        View findViewById = window.findViewById(Window.ID_ANDROID_CONTENT);
        if (findViewById != null) {
            View childAt = ((ViewGroup) findViewById).getChildAt(0);
            if (childAt != null) {
                ViewCompat.setFitsSystemWindows(childAt, false);
                ViewCompat.requestApplyInsets(childAt);
            }
        }
    }
    public static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_filemanager);
        //dibakarece_AndroidFileExplorer-master.zip

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Directory");
//        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        mDirectoryFragment = new DirectoryFragment();
        mDirectoryFragment.setDelegate(new DocumentSelectActivityDelegate() {

            @Override
            public void startDocumentSelectActivity() {

            }

            @Override
            public void didSelectFiles(DirectoryFragment activity,
                                       ArrayList<String> files) {
                if (false) {
                    mDirectoryFragment.showErrorBox(files.get(0).toString());
                } else {
                    if (files != null && files.size() > 0) {
                        try {
                            //https://github.com/microsoft/Visual-Audience-Polling/blob/db9536339145aa87a526c1a5fbf207e730ca7859/src/RosterList.java#L379
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
                            intent.setData(Uri.fromFile(
                                    new File(files.get(0))));
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
                toolbar.setTitle(name);

            }
        });
        fragmentTransaction.add(R.id.fragment_container, mDirectoryFragment, "" + mDirectoryFragment.toString());
        fragmentTransaction.commit();
//        toolbar.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //hideNavigation();
//                int height = getStatusBarHeight(MainActivity.this);
//                if (height > 0) {
//                    View vBox = findViewById(R.id.vBox);
//                    vBox.setLayoutParams(new LinearLayout.LayoutParams(height, height));
//                }
//            }
//        }, 2/*2000*/);
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

}
