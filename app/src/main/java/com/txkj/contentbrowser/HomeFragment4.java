package com.txkj.contentbrowser;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.BaseExtractor;
import com.bumptech.glide.Glide;
import com.foobnix.LibreraApp;
import com.foobnix.android.utils.Apps;
import com.foobnix.android.utils.BaseItemLayoutAdapter;
import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.Keyboards;
import com.foobnix.android.utils.LOG;
import com.foobnix.android.utils.ResultResponse;
import com.foobnix.android.utils.StringDB;
import com.foobnix.android.utils.TxtUtils;
import com.foobnix.dao2.FileMeta;
import com.foobnix.model.AppData;
import com.foobnix.model.AppState;
import com.foobnix.model.SimpleMeta;
import com.foobnix.pdf.info.AppsConfig;
import com.foobnix.pdf.info.ExtUtils;
import com.foobnix.pdf.info.IMG;
import com.foobnix.pdf.info.Prefs;
import com.foobnix.pdf.info.TintUtil;
import com.foobnix.pdf.info.view.EditTextHelper;
import com.foobnix.pdf.info.view.KeyCodeDialog;
import com.foobnix.pdf.info.view.MyPopupMenu;
import com.foobnix.pdf.info.widget.DialogTranslateFromTo;
import com.foobnix.pdf.info.wrapper.PopupHelper;
import com.foobnix.pdf.search.activity.msg.NotifyAllFragments;
import com.foobnix.pdf.search.activity.msg.OpenDirMessage;
import com.foobnix.pdf.search.activity.msg.OpenTagMessage;
import com.foobnix.pdf.search.activity.msg.UpdateAllFragments;
import com.foobnix.sys.TempHolder;
import com.foobnix.ui2.AppDB;
import com.foobnix.ui2.BooksService;
import com.foobnix.ui2.MainTabs2;
import com.foobnix.ui2.adapter.AuthorsAdapter2;
import com.foobnix.ui2.adapter.FileMetaAdapter;
import com.foobnix.work.CheckDeletedBooksWorker;
import com.foobnix.work.SearchAllBooksWorker;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.txkj.contentbrowser2.R;
import com.txkj.drawingapp.db.NoteItem;
import com.txkj.drawingapp.db.SDNotesDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.librera.JSONArray;
import org.librera.LinkedJSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;

public class HomeFragment4 extends Fragment {
    public final static boolean USE_AUTO_SEARCH_PDF = true;

    private final static boolean USE_LAST_OPEN = true;
    private final static boolean MY_FILTER = true;

    private boolean filterChooseNote = true;
    private boolean filterChooseEpub = true;
    private boolean filterChoosePDFs = true;
    private Date filterBeginDateCreation;
    private Date filterEndDateCreation;
    private Date filterBeginDateLastOpen;
    private Date filterEndDateLastOpen;
    private final static int FILTER_SORT_TYPE_NAME1 = 0;
    private final static int FILTER_SORT_TYPE_NAME2 = 1;
    private final static int FILTER_SORT_TYPE_TYPE = 2;
    private final static int FILTER_SORT_TYPE_CREATION_DATE1 = 3;
    private final static int FILTER_SORT_TYPE_CREATION_DATE2 = 4;
    private final static int FILTER_SORT_TYPE_LAST_OPENED_DATE1 = 5;
    private final static int FILTER_SORT_TYPE_LAST_OPENED_DATE2 = 6;
    private final static int FILTER_SORT_TYPE_LAST_MODIFIED_DATE1 = 7;
    private final static int FILTER_SORT_TYPE_LAST_MODIFIED_DATE2 = 8;
    private int filterSortType = 0;
    //----------------------
    //new
    private int filterCreationButton = 0;
    private int filterLastOpenButton = 0;
    private String filterCreationButtonName = "";
    private String filterLastOpenButtonName = "";
    private void loadFilter() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        try {
            this.filterChooseNote = preferences.getBoolean("filterChooseNote", true);
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterChooseNote = true;
        }
        if (filterChooseNote) {
            g_view.findViewById(R.id.rlFileType1).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlFileType1).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else {
            g_view.findViewById(R.id.rlFileType1).findViewWithTag("btnOn").setVisibility(View.GONE);
            g_view.findViewById(R.id.rlFileType1).findViewWithTag("btnOff").setVisibility(View.VISIBLE);
        }
        try {
            this.filterChooseEpub = preferences.getBoolean("filterChooseEpub", true);
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterChooseEpub = true;
        }
        if (filterChooseEpub) {
            g_view.findViewById(R.id.rlFileType2).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlFileType2).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else {
            g_view.findViewById(R.id.rlFileType2).findViewWithTag("btnOn").setVisibility(View.GONE);
            g_view.findViewById(R.id.rlFileType2).findViewWithTag("btnOff").setVisibility(View.VISIBLE);
        }
        try {
            this.filterChoosePDFs = preferences.getBoolean("filterChoosePDFs", true);
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterChoosePDFs = true;
        }
        if (filterChoosePDFs) {
            g_view.findViewById(R.id.rlFileType3).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlFileType3).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else {
            g_view.findViewById(R.id.rlFileType3).findViewWithTag("btnOn").setVisibility(View.GONE);
            g_view.findViewById(R.id.rlFileType3).findViewWithTag("btnOff").setVisibility(View.VISIBLE);
        }
        try {
            Long filterBeginDateCreation_ = preferences.getLong("filterBeginDateCreation", 0L);
            this.filterBeginDateCreation = filterBeginDateCreation_ != 0L ? new Date(filterBeginDateCreation_) : null;
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterBeginDateCreation = null;
        }
        try {
            Long filterEndDateCreation_ = preferences.getLong("filterEndDateCreation", 0L);
            this.filterEndDateCreation = filterEndDateCreation_ != 0L ? new Date(filterEndDateCreation_) : null;
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterEndDateCreation = null;
        }
        try {
            Long filterBeginDateLastOpen_ = preferences.getLong("filterBeginDateLastOpen", 0L);
            this.filterBeginDateLastOpen = filterBeginDateLastOpen_ != 0L ? new Date(filterBeginDateLastOpen_) : null;
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterBeginDateLastOpen = null;
        }
        try {
            Long filterEndDateLastOpen_ = preferences.getLong("filterEndDateLastOpen", 0L);
            this.filterEndDateLastOpen = filterEndDateLastOpen_ != 0L ? new Date(filterEndDateLastOpen_) : null;
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterEndDateLastOpen = null;
        }
        try {
            this.filterSortType = preferences.getInt("filterSortType", 0);
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterSortType = 0;
        }
        TextView textView = (TextView) g_view.findViewById(R.id.rlSortedBy).findViewWithTag("tvName1");
        if (this.filterSortType == FILTER_SORT_TYPE_NAME1) {
            textView.setText("Name / Title, A-Z");
        } else if (this.filterSortType == FILTER_SORT_TYPE_NAME2) {
            textView.setText("Name / Title, Z-A");
        } else if (this.filterSortType == FILTER_SORT_TYPE_TYPE) {
            textView.setText("Type");
        } else if (this.filterSortType == FILTER_SORT_TYPE_CREATION_DATE1) {
            textView.setText("Creation Date, newest first");
        } else if (this.filterSortType == FILTER_SORT_TYPE_CREATION_DATE2) {
            textView.setText("Creation Date, oldest first");
        } else if (this.filterSortType == FILTER_SORT_TYPE_LAST_OPENED_DATE1) {
            textView.setText("Last opened, newest first");
        } else if (this.filterSortType == FILTER_SORT_TYPE_LAST_OPENED_DATE2) {
            textView.setText("Last opened, oldest first");
        } else if (this.filterSortType == FILTER_SORT_TYPE_LAST_MODIFIED_DATE1) {
            textView.setText("Last modified, newest first");
        } else if (this.filterSortType == FILTER_SORT_TYPE_LAST_MODIFIED_DATE2) {
            textView.setText("Last modified, oldest first");
        }
        try {
            this.filterCreationButton = preferences.getInt("filterCreationButton", 0);
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterCreationButton = 0;
        }
        if (filterCreationButton == 0) {
            //skip
        } else if (filterCreationButton == 1) {
            g_view.findViewById(R.id.rlCreationDate1).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlCreationDate1).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else if (filterCreationButton == 2) {
            g_view.findViewById(R.id.rlCreationDate2).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlCreationDate2).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else if (filterCreationButton == 3) {
            g_view.findViewById(R.id.rlCreationDate3).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlCreationDate3).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else if (filterCreationButton == 4) {
            g_view.findViewById(R.id.rlCreationDate4).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlCreationDate4).findViewWithTag("btnOff").setVisibility(View.GONE);
        }
        try {
            this.filterLastOpenButton = preferences.getInt("filterLastOpenButton", 0);
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterLastOpenButton = 0;
        }
        if (filterLastOpenButton == 0) {
            //skip
        } else if (filterLastOpenButton == 1) {
            g_view.findViewById(R.id.rlLastOpen1).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlLastOpen1).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else if (filterLastOpenButton == 2) {
            g_view.findViewById(R.id.rlLastOpen2).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlLastOpen2).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else if (filterLastOpenButton == 3) {
            g_view.findViewById(R.id.rlLastOpen3).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlLastOpen3).findViewWithTag("btnOff").setVisibility(View.GONE);
        } else if (filterLastOpenButton == 4) {
            g_view.findViewById(R.id.rlLastOpen4).findViewWithTag("btnOn").setVisibility(View.VISIBLE);
            g_view.findViewById(R.id.rlLastOpen4).findViewWithTag("btnOff").setVisibility(View.GONE);
        }

        try {
            this.filterCreationButtonName = preferences.getString("filterCreationButtonName", "");
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterCreationButtonName = "";
        }
        if (filterCreationButtonName != null && filterCreationButtonName.length() > 0) {
            ((TextView) g_view.findViewById(R.id.tvCreationDate4_on)).setText(filterCreationButtonName);
            ((TextView) g_view.findViewById(R.id.tvCreationDate4_off)).setText(filterCreationButtonName);
        }
        try {
            this.filterLastOpenButtonName = preferences.getString("filterLastOpenButtonName", "");
        } catch (Throwable eee) {
            eee.printStackTrace();
            this.filterLastOpenButtonName = "";
        }
        if (filterLastOpenButtonName != null && filterLastOpenButtonName.length() > 0) {
            ((TextView) g_view.findViewById(R.id.tvLastOpenDate4_off)).setText(filterLastOpenButtonName);
            ((TextView) g_view.findViewById(R.id.tvLastOpenDate4_on)).setText(filterLastOpenButtonName);
        }
    }

    private void saveFilter() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("filterChooseNote", filterChooseNote);
        editor.putBoolean("filterChooseEpub", filterChooseEpub);
        editor.putBoolean("filterChoosePDFs", filterChoosePDFs);
        editor.putLong("filterBeginDateCreation", filterBeginDateCreation != null ?
                filterBeginDateCreation.getTime() : 0L);
        editor.putLong("filterEndDateCreation", filterEndDateCreation != null ?
                filterEndDateCreation.getTime() : 0L);
        editor.putLong("filterBeginDateLastOpen", filterBeginDateLastOpen != null ?
                filterBeginDateLastOpen.getTime() : 0L);
        editor.putLong("filterEndDateLastOpen", filterEndDateLastOpen != null ?
                filterEndDateLastOpen.getTime() : 0L);
        editor.putInt("filterSortType", filterSortType);

        editor.putInt("filterCreationButton", filterCreationButton);
        editor.putInt("filterLastOpenButton", filterLastOpenButton);
        editor.putString("filterCreationButtonName", filterCreationButtonName);
        editor.putString("filterLastOpenButtonName", filterLastOpenButtonName);

        editor.apply();
    }
    //----------------------

    private final static String STR_NO_ITEMS = "Nothing here yet";//"No items.";
    private final static String STR_LOADING = "Loading...";

    //find-add-folder
    private final static boolean IS_LOG = AppsConfig.IS_LOG;
    //AppState.get().isSkipFolderWithNOMEDIA;
    public final static boolean FORCE_SKIP_NOMEDIA = false;

    //for override
    public int getInitIndex() {
        return 0; //0:all, 1:pdf, 2:epub(book)
    }
    private int currentSeg = getInitIndex(); //0:all, 1:pdf, 2:epub(book)
    private int getSegment() {
        return currentSeg;
    }
    private void setSegment(int pos) {
        switch (pos) {
            case 0:
                tvTabAll.setBackgroundResource(R.drawable.button_top2_black);
                tvTabPdf.setBackgroundResource(R.drawable.button_top3_white);
                tvTabEpub.setBackgroundResource(R.drawable.button_top4_white);
                tvTabAll.setTextColor(0xFFFFFFFF);
                tvTabPdf.setTextColor(0xFF000000);
                tvTabEpub.setTextColor(0xFF000000);
                currentSeg = 0;
                break;

            case 1:
                tvTabAll.setBackgroundResource(R.drawable.button_top2_white);
                tvTabPdf.setBackgroundResource(R.drawable.button_top3_black);
                tvTabEpub.setBackgroundResource(R.drawable.button_top4_white);
                tvTabAll.setTextColor(0xFF000000);
                tvTabPdf.setTextColor(0xFFFFFFFF);
                tvTabEpub.setTextColor(0xFF000000);
                currentSeg = 1;
                break;

            case 2:
                tvTabAll.setBackgroundResource(R.drawable.button_top2_white);
                tvTabPdf.setBackgroundResource(R.drawable.button_top3_white);
                tvTabEpub.setBackgroundResource(R.drawable.button_top4_black);
                tvTabAll.setTextColor(0xFF000000);
                tvTabPdf.setTextColor(0xFF000000);
                tvTabEpub.setTextColor(0xFFFFFFFF);
                currentSeg = 2;
                break;
        }
        //populate();
        searchAndOrderAsync();
    }

    private TextView tvTabAll, tvTabPdf, tvTabEpub;
    private List<FileMeta> pageList;
    private GridView recyclerView;
    private LibraryGridAdapter2 bookGridAdapter;
    private TextView tvEmpty1;
    private View progressLoading1, loadingContent1;
    private LinearLayout llEmpty1;

    private TextView tvEmpty2;
    private ImageView ivEmpty1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_4, container, false);

        onCreateViewFilter(view);
        loadFilter();

        //View view = inflater.inflate(R.layout.fragment_search2, container, false);
//        AutoWrapViewGroup autoWrapViewGroup = (AutoWrapViewGroup) view.findViewById(R.id.autoWrapViewGroup);
//        if (getInitIndex() == 1) {
//            autoWrapViewGroup.output("PDFs", ""); //"PDFs: "
//        } else {
//            autoWrapViewGroup.output("Books", ""); //"Books: "
//        }
        tvTabAll = (TextView) view.findViewById(R.id.tvTabAll);
        tvTabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSegment(0);
            }
        });
        tvTabPdf = (TextView) view.findViewById(R.id.tvTabPdf);
        tvTabPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSegment(1);
            }
        });
        tvTabEpub = (TextView) view.findViewById(R.id.tvTabEpub);
        tvTabEpub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSegment(2);
            }
        });


//        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Intent intent = new Intent();
////                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    intent.setClassName("com.foobnix.pdf.info",
//                            "com.foobnix.ui2.MainTabs2");
//                    startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        pageList = new ArrayList<FileMeta>();
//        for (int i = 0; i < 20; ++i) {
//            FileMeta m = new FileMeta();
//            m.setTitle("page " + i);
//            pageList.add(m);
//        }
        recyclerView = (GridView) view.findViewById(R.id.bookgridview);
        recyclerView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //recyclerView.setBackgroundColor(Color.WHITE);

        DisplayMetrics DM = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(DM);
        if (NoteFragment2.USE_COLUMN_NUM) {
            //note
        }
        if (DM.heightPixels > DM.widthPixels) {
            recyclerView.setNumColumns(3);//4);//3);
        } else {
            recyclerView.setNumColumns(4);//6);//4);
        }


        bookGridAdapter = new LibraryGridAdapter2(this.getContext(), pageList);
        recyclerView.setAdapter(bookGridAdapter);
        tvEmpty1 = view.findViewById(R.id.tvEmpty1);
        tvEmpty1.setText(STR_LOADING);
        tvEmpty2 = view.findViewById(R.id.tvEmpty2);
        ivEmpty1 = view.findViewById(R.id.ivEmpty1);
        progressLoading1 = view.findViewById(R.id.progressLoading1);
        loadingContent1 = view.findViewById(R.id.loadingContent1);
        progressLoading1.setVisibility(View.VISIBLE);
        loadingContent1.setVisibility(View.GONE);
        llEmpty1 = (LinearLayout) view.findViewById(R.id.llEmpty1);
        recyclerView.setEmptyView(llEmpty1);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    FileMeta meta = pageList.get(position);
                    if (meta != null && meta.isNote) {
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
                        String APP_FILE = meta.getPathTxt();
                        intent.putExtra("APP_FILE", APP_FILE);
                        Log.d(TAG, "APP_FILE: " + APP_FILE);

                        //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                    } else {
                        //int bookType = bookInfoList.get(position).getBookType();
//                int pageIdx = position;
//                Intent intent = new Intent(BookListActivity.this, BookActivity.class);
//                intent.setData(((FastFile)files.get(pageIdx)).getUri());
//                startActivity(intent);

                        Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
                        if (false) {
                            intent.setClassName("com.txkj.pdfreader",
                                    "org.ebookdroid.ui.viewer.VerticalViewActivity");
                        } else {
                            intent.setClassName("com.txkj.readingapp",
                                    "org.ebookdroid.ui.viewer.VerticalViewActivity");
                        }

                        AppData.get().addRecent(new SimpleMeta(meta.getPath(), System.currentTimeMillis()));

                        File file = new File(meta.getPath());
                        intent.setData(Uri.fromFile(file));
                        //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                    }
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        });




        LOG.d("Context-SF-getContext", getContext());
        LOG.d("Context-SF-getApplicationContext", getActivity().getApplicationContext());
        LOG.d("Context-SF-getBaseContext", getActivity().getBaseContext());
        LOG.d("Context-SF-LibreraApp.context", LibreraApp.context);

        LOG.d("SearchFragment2 onCreateView");

        NO_SERIES = " (" + getString(R.string.without_series) + ")";

        handler = new Handler();


        searchEditText = (AutoCompleteTextView) view.findViewById(R.id.filterLine_Library);
        llTab = (LinearLayout) view.findViewById(R.id.llTab);
        llTabWrapper = (LinearLayout) view.findViewById(R.id.llTabWrapper);
        spaceVertical = (View) view.findViewById(R.id.spaceVertical);
        llTab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (llTab.getMeasuredWidth() >= 0 && llTabWrapper.getMeasuredWidth() >= 0) {
                    if (llTabWrapper.getMeasuredWidth() >= llTab.getMeasuredWidth() / 2) {
                        llTab.setOrientation(LinearLayout.VERTICAL);
                        spaceVertical.setVisibility(View.VISIBLE);
                    } else {
                        llTab.setOrientation(LinearLayout.HORIZONTAL);
                        spaceVertical.setVisibility(View.GONE);
                    }
                }
            }
        });

        if (AppState.get().appTheme == AppState.THEME_DARK_OLED || (AppState.get().appTheme == AppState.THEME_DARK && TintUtil.color == Color.BLACK)) {
            searchEditText.setBackgroundResource(R.drawable.bg_search_edit_night);
        }

        searchEditText.addTextChangedListener(filterTextWatcher);
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        EditTextHelper.enableKeyboardSearch(searchEditText, new Runnable() {

            @Override
            public void run() {
                Keyboards.close(searchEditText);
                Keyboards.hideNavigation(getActivity());
            }
        });

        searchAdapter = new LibraryGridAdapter2(getActivity(), pageList);
        bindAdapter(searchAdapter);
//        searchAdapter.setOnAuthorClickListener(onAuthorClick);
//        searchAdapter.setOnSeriesClickListener(onSeriesClick);

//        tvPageInfo = (TextView) view.findViewById(R.id.tvPageInfo);

        if (AppState.get().isRestoreSearchQuery && !TxtUtils.isEmpty(AppState.get().searchQuery)) {
            searchEditText.setText(AppState.get().searchQuery);
        }

        onGridList();

        if (Prefs.get().isErrorExist(SearchAllBooksWorker.SEARCH_ERRORS, 0)) {
            searchAndOrderAsync();
        } else {
            if (AppDB.get().getCount() == 0) {
                seachAll();
            } else {
                if (HomeFragment4.USE_AUTO_SEARCH_PDF) {
                    seachAll(); //FIXME:added, 2026/07/02
                }
                checkForDeleteBooks();
                searchAndOrderAsync();
            }
        }

        initAutocomplition();
        onTintChanged();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(BooksService.INTENT_NAME));

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewCreated2(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveFilter();
    }



























    //------------------

    public static String INTENT_TINT_CHANGE = "INTENT_TINT_CHANGE";
    protected volatile com.foobnix.pdf.info.view.MyProgressBar MyProgressBar;
    //    protected RecyclerView recyclerView;
    Handler handler_;
    View adFrame;
    SwipeRefreshLayout swipeRefreshLayout;
    int listHash = 0;
    BroadcastReceiver broadcastReceiver_ = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String txt = intent.getStringExtra(MainTabs2.EXTRA_SEACH_TEXT);
            if (TxtUtils.isNotEmpty(txt)) {
                onTextRecive(txt);
            } else {
                onTintChanged();
            }
        }
    };
    AsyncTask<Object, Object, List<FileMeta>> execute;
    volatile boolean inProgress = false;

    //public abstract Pair<Integer, Integer> getNameAndIconRes();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        handler_ = new Handler();
    }

    public void onViewCreated2(View view, Bundle savedInstanceState) {
        //TxtUtils.updateAllLinks(view);

        //FIXME:????
        if (AppState.get().appTheme == AppState.THEME_INK) {
            TxtUtils.setInkTextView(view);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler_.removeCallbacksAndMessages(null);
    }

//    public boolean isBackPressed() {
//        return false;
//    }

    //public abstract void notifyFragment();

    //public abstract void resetFragment();

//    public void onDoubleClick(){
//
//    }

    public final void onSelectFragment() {
        if (getActivity() == null) {
            return;
        }
        if (listHash != TempHolder.listHash) {
            LOG.d("TempHolder.listHash", listHash, TempHolder.listHash);
            resetFragment();
            listHash = TempHolder.listHash;
        } else {
            //TODO ???
            //notifyFragment();

            try {
                if (adFrame == null) {
                    adFrame = getActivity().findViewById(R.id.adFrame);
                }

                if (adFrame != null && adFrame.getVisibility() == View.INVISIBLE) {
                    adFrame.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                LOG.e(e);
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyUpdateFragment(UpdateAllFragments event) {
        TempHolder.listHash++;
        onSelectFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyUpdateFragment(NotifyAllFragments event) {
        notifyFragment();
    }

    public void bindAdapter(LibraryGridAdapter2 searchAdapter) {
//        searchAdapter.setOnItemClickListener(getOnItemClickListener(a));
//        searchAdapter.setOnItemLongClickListener(getOnItemLongClickListener(a, searchAdapter));
//        searchAdapter.setOnMenuClickListener(getOnMenuClick(a, searchAdapter));
//        searchAdapter.setOnStarClickListener(getOnStarClick(a));
//        searchAdapter.setOnTagClickListner(new ResultResponse<String>() {
//            @Override
//            public boolean onResultRecive(String result) {
//                showBooksByTag(a, result);
//                return false;
//            }
//        });
    }

//    public void bindAuthorsSeriesAdapter(FileMetaAdapter searchAdapter) {
//        DefaultListeners.bindAdapterAuthorSerias(getActivity(), searchAdapter);
//    }

    private List<FileMeta> prepareDataInBackgroundSync() {
        return prepareDataInBackground();
    }

//    public List<FileMeta> prepareDataInBackground() {
//        return null;
//    }
//
//    public void populateDataInUI(List<FileMeta> items) {
//
//    }
//
//    public void onTintChanged() {
//
//    }
//
//    public void sendNotifyTintChanged() {
//        Intent itent = new Intent(INTENT_TINT_CHANGE);
//        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(itent);
//        DocumentController.setNavBarTintColor(getActivity());
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        TempHolder.listHash++; //FIXME:????
        onSelectFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Safe.clearAll();
        try {
            Glide.with(LibreraApp.context).resumeRequests();
        } catch (Exception e) {
            LOG.e(e);
        }
        notifyFragment();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver_, new IntentFilter(INTENT_TINT_CHANGE));
        EventBus.getDefault().register(this);
        onResume2();

        setSegment(currentSeg);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver_);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onReviceOpenDir(OpenDirMessage msg) {
        onReviceOpenDir(msg.getPath());
    }

    public void onReviceOpenDir(String path) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onDestroy2();
    }

//    public void onTextRecive(String txt) {
//
//    }

    public boolean isInProgress() {
        return MyProgressBar != null && MyProgressBar.getVisibility() == View.VISIBLE;
    }

    public void populate() {
        if (inProgress) {
            LOG.d("IN_PROGRESS");
            return;
        }

        final Runnable target = () -> {

            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (MyProgressBar != null) {
                        handler_.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                MyProgressBar.setVisibility(View.VISIBLE);
                            }
                        }, 100);
                    }
                }
            });


            final List<FileMeta> result;
            try {
                inProgress = true;
                result = prepareDataInBackgroundSync();
            } finally {
                inProgress = false;

            }
            if (isDetached() || Apps.isDestroyedActivity(getActivity())) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAdded()) {
                        if (MyProgressBar != null) {
                            handler_.removeCallbacksAndMessages(null);
                            MyProgressBar.setVisibility(View.GONE);
                        }
                        try {
                            populateDataInUI(result);
                        } catch (Exception e) {
                            LOG.e(e);
                        }
                    }

                }
            });
        };
        AppsConfig.executorService.submit(target);
    }

    public void onGridList(int mode, ImageView onGridlList, final LibraryGridAdapter2 searchAdapter, AuthorsAdapter2 authorsAdapter) {
        if (searchAdapter == null) {
            return;
        }
        if (onGridlList != null) {
            PopupHelper.updateGridOrListIcon(onGridlList, mode);
        }

        recyclerView.setAdapter(searchAdapter);
    }

    public boolean onKeyDown(int keyCode) {
        return false;
    }

    //-------------------
















    public static final String EMPTY_ID = "\u00A0";
    public static final Pair<Integer, Integer> PAIR = new Pair<Integer, Integer>(R.string.library, R.drawable.glyphicons_589_book_open);
    private static final String CMD_KEYCODE = "@@keycode_config";
    private static final String CMD_EDIT_AUTO_COMPLETE = "@@edit_autocomple";
    private static final String CMD_MARGIN = "@@keycode_margin";
    public static int NONE = -1;
    public static List<FileMeta> cacheItems;
    final Set<String> autocomplitions = new HashSet<String>();
    public int prevLibModeFileMeta = AppState.MODE_GRID;
    public int prevLibModeAuthors = NONE;
    public int rememberPos = 0;
    LibraryGridAdapter2 searchAdapter;
//    TextView tvPageInfo;
    //    AuthorsAdapter2 authorsAdapter;
//    TextView countBooks, sortBy;
    Handler handler;
    //    ImageView sortOrder;
//    ImageView myAutoCompleteImage;
//    ImageView cleanFilter;
//    ImageView menu2;
    //View onRefresh, secondTopPanel;
    AutoCompleteTextView searchEditText;
    LinearLayout llTab, llTabWrapper;
    View spaceVertical;
    int countTitles = 0;
    Runnable hideKeyboard = new Runnable() {

        @Override
        public void run() {
            Keyboards.close(searchEditText);
            Keyboards.hideNavigation(getActivity());
        }
    };
//    Runnable saveAutoComplete = new Runnable() { //memory leak ??? //com.txkj.contentbrowser.HomeFragment4$11 instance
//
//        @Override
//        public void run() {
//            String txt = searchEditText.getText().toString().trim();
//            if (TxtUtils.isNotEmpty(txt) && !txt.startsWith("@@") && !StringDB.contains(AppState.get().myAutoCompleteDb, txt)) {
//                if (!searchAdapter.getItemsList().isEmpty()) {
//                    StringDB.add(AppState.get().myAutoCompleteDb, txt, (db) -> AppState.get().myAutoCompleteDb = db);
//                    autocomplitions.add(txt);
//                    updateFilterListAdapter();
//                }
//            }
//
//        }
//    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (BooksService.RESULT_SEARCH_FINISH.equals(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                searchAndOrderAsync();
                searchEditText.setHint(R.string.search);
                //onRefresh.setActivated(true);

                if (AppsConfig.IS_LOG) {
                    searchEditText.setHint(Apps.getApplicationName(getContext()));
                }


            } else if (BooksService.RESULT_SEARCH_COUNT.equals(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                int count = intent.getIntExtra("android.intent.extra.INDEX", 0);
                if (count > 0) {
//                    countBooks.setText("" + count);
                }
                searchEditText.setHint(R.string.searching_please_wait_);
            } else if (BooksService.RESULT_BUILD_LIBRARY.equals(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                searchEditText.setHint(R.string.extracting_information_from_books);
            } else if (BooksService.RESULT_SEARCH_MESSAGE_TXT.equals(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                searchEditText.setHint(intent.getStringExtra("TEXT"));
            } else if (BooksService.RESULT_NOTIFY_ALL.equals(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                TempHolder.listHash++;
                EventBus.getDefault().post(new NotifyAllFragments());
            }
        }

    };
    Runnable sortAndSeach = new Runnable() {

        @Override
        public void run() {
            searchAndOrderAsync();
        }
    };
    private final TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(final Editable s) {
            AppState.get().searchQuery = s.toString();
        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            if (//
                    AppState.get().libraryMode == AppState.MODE_GRID || //
                            AppState.get().libraryMode == AppState.MODE_LIST || //
                            AppState.get().libraryMode == AppState.MODE_LIST_COMPACT || //
                            AppState.get().libraryMode == AppState.MODE_COVERS//
            ) {
                handler.removeCallbacks(sortAndSeach);
                handler.removeCallbacks(hideKeyboard);
                if (s.toString().trim().length() == 0) {
                    handler.postDelayed(sortAndSeach, 250);
                    handler.postDelayed(hideKeyboard, 2000);
                } else {
                    handler.postDelayed(sortAndSeach, 1000);
                }
            }
//            handler.removeCallbacks(saveAutoComplete);
//
//            if (StringDB.contains(AppState.get().myAutoCompleteDb, s.toString().trim())) {
//                //skip
//            } else {
//                handler.postDelayed(saveAutoComplete, 10000);
//            }

        }

    };
    boolean isOnTop = false;
    private String NO_SERIES = ":no-series";
    private Stack<String> prevText = new Stack<String>();
    ResultResponse<String> onAuthorClick = new ResultResponse<String>() {
        @Override
        public boolean onResultRecive(String result) {
            onMetaInfoClick(AppDB.SEARCH_IN.AUTHOR, result);
            return false;
        }
    };
    ResultResponse<String> onSeriesClick = new ResultResponse<String>() {

        @Override
        public boolean onResultRecive(String result) {
            if (result.contains(NO_SERIES)) {
                onMetaInfoClick(AppDB.SEARCH_IN.getByPrefix(searchEditText.getText().toString()), result);
            } else {
                onMetaInfoClick(AppDB.SEARCH_IN.SERIES, result);
            }
            return false;
        }
    };

    //@Override
    public Pair<Integer, Integer> getNameAndIconRes() {
        return PAIR;
    }

    //@Override
    public void onTintChanged() {
//        int color = (AppState.get().appTheme == AppState.THEME_DARK_OLED ||
//                AppState.get().appTheme == AppState.THEME_DARK)
//                ? Color.WHITE : TintUtil.color;
//        int colorTheme = TintUtil.getColorInDayNighth();
//        colorTheme = ColorUtils.setAlphaComponent(colorTheme, 230);
//        if (AppState.get().appTheme == AppState.THEME_DARK_OLED || (AppState.get().appTheme == AppState.THEME_DARK && TintUtil.color == Color.BLACK)) {
//            searchEditText.setBackgroundResource(R.drawable.bg_search_edit_night);
//        } else {
//            searchEditText.setBackgroundResource(R.drawable.bg_search_edit);
//        }
//        TintUtil.setStrokeColor(searchEditText, TintUtil.color);
//        TintUtil.setUITextColor(searchEditText, colorTheme);
//        if (AppState.get().appTheme == AppState.THEME_INK) {
//            searchEditText.setBackgroundResource(R.drawable.bg_search_edit);
//            TintUtil.setStrokeColor(searchEditText, Color.BLACK);
//            TintUtil.setUITextColor(searchEditText, Color.BLACK);
//        }
    }

    public void onGridList() {
        onGridList(AppState.get().libraryMode, null, searchAdapter, null);
    }

    public void initAutocomplition() {
        autocomplitions.clear();
        for (AppDB.SEARCH_IN search : AppDB.SEARCH_IN.values()) {
            if (search == AppDB.SEARCH_IN.SERIES) {
                autocomplitions.add(search.getDotPrefix() + " *");
            } else {
                autocomplitions.add(search.getDotPrefix());
            }
        }

        autocomplitions.add(CMD_KEYCODE);
        autocomplitions.add(CMD_MARGIN);
        autocomplitions.add(CMD_EDIT_AUTO_COMPLETE);

        autocomplitions.addAll(StringDB.asList(AppState.get().myAutoCompleteDb));

        updateFilterListAdapter();
    }

    public void updateFilterListAdapter() {
        try {
            ArrayList<String> list = new ArrayList<String>(autocomplitions);
            Collections.sort(list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
            searchEditText.setAdapter(adapter);
            searchEditText.setThreshold(1);
        } catch (Exception e) {
            LOG.e(e);
        }

    }

    public void showAutoCompleteDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.search_history_and_autocomplete);

        final ListView list = new ListView(getActivity());

        final List<String> items = new ArrayList<String>(StringDB.asList(AppState.get().myAutoCompleteDb));
        Collections.sort(items);
        BaseItemLayoutAdapter<String> adapter = new BaseItemLayoutAdapter<String>(getActivity(), R.layout.path_item, items) {
            @Override
            public void populateView(View layout, int position, final String item) {
                TextView text = layout.findViewById(R.id.browserPath);
                ImageView delete = layout.findViewById(R.id.delete);

                text.setText(item);

                delete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        autocomplitions.remove(item);
                        items.remove(item);

                        StringDB.delete(AppState.get().myAutoCompleteDb, item, (db) -> AppState.get().myAutoCompleteDb = db);
                        notifyDataSetChanged();
                    }
                });
                layout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        searchEditText.setText(item);
                        searchAndOrderAsync();
                    }
                });

            }
        };

        list.setAdapter(adapter);

        builder.setView(list);

        builder.setPositiveButton(R.string.close, new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog create = builder.create();
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Keyboards.hideNavigation(getActivity());
            }
        });
        create.show();

        Keyboards.close(getActivity());

    }

    //@Override
    public void onResume2() {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void onMetaInfoClick(AppDB.SEARCH_IN mode, String result) {
        if (mode == AppDB.SEARCH_IN.SERIES && !result.startsWith(EMPTY_ID)) {
            result = StringDB.EXACTMATCHCHAR + result + StringDB.EXACTMATCHCHAR;
        }

        searchEditText.setText(mode.getDotPrefix() + " " + result);
        AppState.get().libraryMode = prevLibModeFileMeta;
        onGridList();
        searchAndOrderAsync();
    }

    private void seachAll() {
        try {
            searchAdapter.clearItems();
            searchAdapter.notifyDataSetChanged();
            IMG.clearMemoryCache();
            IMG.clearDiscCache();
            //BooksService.startForeground(getActivity(), BooksService.ACTION_SEARCH_ALL);

            SearchAllBooksWorker.run(getActivity());

        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public void checkForDeleteBooks() {
        try {
            //BooksService.startForeground(getActivity(), BooksService.ACTION_REMOVE_DELETED);
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                    .Builder(CheckDeletedBooksWorker.class).build();
            WorkManager.getInstance(getContext()).enqueueUniqueWork("search", ExistingWorkPolicy.KEEP, workRequest);
            LOG.d("MessageWorker-Status checkForDeleteBooks");
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    //@Override
    public void onTextRecive(String txt) {
        searchAndOrderExteral(txt);
    }

    public void searchAndOrderExteral(String text) {
        if (searchEditText != null) {
            searchEditText.setText(text);
            searchAndOrderAsync();
        }

    }

    public void searchAndOrderAsync() {
        if (Apps.isDestroyedActivity(getActivity())) {
            return;
        }
        searchEditText.setHint(R.string.msg_loading);
        String order = getString(AppState.get().isSortAsc ? R.string.ascending : R.string.descending);

        populate();

    }

    @Subscribe
    public void onShowTag(OpenTagMessage msg) {
        if (searchEditText != null) {
            searchEditText.setText("@tags " + msg.getTagName());
        }
        searchAndOrderAsync();

    }

    //@Override
    public List<FileMeta> prepareDataInBackground() {
        String txt = searchEditText.getText().toString().trim();
        countTitles = 0;
        if (Arrays.asList(AppState.MODE_GRID, AppState.MODE_COVERS, AppState.MODE_LIST, AppState.MODE_LIST_COMPACT).contains(AppState.get().libraryMode)) {

            if (!prevText.contains(txt)) {
                prevText.push(txt);
            }
            if (TxtUtils.isEmpty(txt)) {
                prevText.clear();
            }

            boolean isSearchOnlyEmpy = txt.contains(NO_SERIES);
            if (isSearchOnlyEmpy) {
                txt = txt.replace(NO_SERIES, "");
            }

            List<FileMeta> searchBy = AppDB.get().searchBy(txt, AppDB.SORT_BY.getByID(AppState.get().sortBy), AppState.get().isSortAsc);

            ExtUtils.removeReadBooks(searchBy);
            ExtUtils.removeNotFound(searchBy);


            List<String> result = new ArrayList<String>();
            boolean byGenre = txt.startsWith(AppDB.SEARCH_IN.GENRE.getDotPrefix());
            boolean byAuthor = txt.startsWith(AppDB.SEARCH_IN.AUTHOR.getDotPrefix());
            if (!txt.contains("::") && (byGenre || byAuthor)) {
                if (isSearchOnlyEmpy) {
                    Iterator<FileMeta> iterator = searchBy.iterator();
                    while (iterator.hasNext()) {
                        if (TxtUtils.isNotEmpty(iterator.next().getSequence())) {
                            iterator.remove();
                        }
                    }
                    return searchBy;
                }


                boolean hasEmpySeries = false;
                for (FileMeta it : searchBy) {
                    String sequence = it.getSequence();
                    TxtUtils.addFilteredGenreSeries(sequence, result, true);
                    if (!hasEmpySeries && TxtUtils.isEmpty(sequence)) {
                        hasEmpySeries = true;
                    }
                }

                Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
                Collections.reverse(result);


                String genreName = txt.replace(byGenre ? "@genre " : "@author ", "");
                for (String it : result) {
                    FileMeta fm = new FileMeta();
                    fm.setCusType(FileMetaAdapter.DISPALY_TYPE_SERIES);
                    fm.setSequence(it);
                    searchBy.add(0, fm);
                }
                if (hasEmpySeries && !result.isEmpty()) {
                    FileMeta fm = new FileMeta();
                    fm.setCusType(FileMetaAdapter.DISPALY_TYPE_SERIES);
                    fm.setSequence(genreName + NO_SERIES);
                    searchBy.add(result.size(), fm);
                }
            }

            if (//
                    AppState.get().sortBy == AppDB.SORT_BY.PATH.getIndex() ||//
                            AppState.get().sortBy == AppDB.SORT_BY.LANGUAGE.getIndex() ||//
                            AppState.get().sortBy == AppDB.SORT_BY.PUBLICATION_YEAR.getIndex() ||
                            AppState.get().sortBy == AppDB.SORT_BY.SERIES.getIndex() ||
                            AppState.get().sortBy == AppDB.SORT_BY.PUBLISHER.getIndex()) {//

                List<FileMeta> res = new ArrayList<FileMeta>();
                String last = null;

                String extDir = Environment.getExternalStorageDirectory().getPath();

                int count = 0;
                FileMeta fm = null;

                ///

                for (FileMeta it : searchBy) {
                    String parentName = "";
                    if (AppState.get().sortBy == AppDB.SORT_BY.PUBLISHER.getIndex()) {
                        parentName = "" + it.getPublisher();
                    } else if (AppState.get().sortBy == AppDB.SORT_BY.SERIES.getIndex()) {
                        parentName = "" + TxtUtils.nullToEmpty(it.getSequence());
                        parentName = parentName.replace(",", "");
                        if (parentName.isEmpty()) {
                            parentName = "---";
                        }
                    } else if (AppState.get().sortBy == AppDB.SORT_BY.PUBLICATION_YEAR.getIndex()) {
                        parentName = "" + it.getYear();
                    } else if (AppState.get().sortBy == AppDB.SORT_BY.PATH.getIndex()) {
                        parentName = it.getParentPath();
                        if (parentName != null) {
                            parentName = parentName.replace(extDir, "");
                        }
                    } else if (AppState.get().sortBy == AppDB.SORT_BY.LANGUAGE.getIndex()) {
                        String lang = it.getLang();
                        if (TxtUtils.isEmpty(lang)) {
                            parentName = "---";
                        } else {
                            parentName = DialogTranslateFromTo.getLanuageByCode(lang);
                        }
                    }
                    count++;
                    if (parentName != null && !parentName.equals(last)) {
                        if (fm != null) {
                            fm.setTitle(fm.getTitle() + " (" + count + ")");
                        }
                        fm = new FileMeta();
                        fm.setCusType(FileMetaAdapter.DISPALY_TYPE_LAYOUT_TITLE_DIVIDER);
                        fm.setTitle(parentName);
                        count = 0;
                        last = parentName;
                        res.add(fm);
                        countTitles++;
                    }
                    res.add(it);
                }
                if (fm != null) {
                    fm.setTitle(fm.getTitle() + " (" + (count + 1) + ")");
                }
                searchBy = res;
            }

            List<FileMeta> notes = prepareDataInBackground_note();
            searchBy.addAll(notes);
            return searchBy;
        } else {
            return null;
        }
    }

    public void toastState(String command, boolean state) {
        Toast.makeText(getContext(), command + " [" + (state ? "ON" : "OFF") + "]", Toast.LENGTH_LONG).show();
    }

    //@Override
    public void populateDataInUI(List<FileMeta> items) {
        tvEmpty1.setText(STR_NO_ITEMS);
        progressLoading1.setVisibility(View.GONE);
        loadingContent1.setVisibility(View.VISIBLE);

        cacheItems = items;
        handler.removeCallbacks(sortAndSeach);

        String txt = searchEditText.getText().toString().trim();
        searchEditText.setHint(R.string.search); //"SearchFragment2xxx"

        //if(AppsConfig.IS_LOG){
        //searchEditText.setHint(Apps.getApplicationName(getContext()));
        //}


        if (CMD_KEYCODE.equals(txt)) {
            new KeyCodeDialog(getActivity(), null);
            searchEditText.setText("");
        }
        if (CMD_MARGIN.equals(txt)) {
            SwipeRefreshLayout layout = getActivity().findViewById(R.id.swipeRefreshLayout);
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
            layoutParams.rightMargin = Dips.screenWidth() / 4;
            layout.setLayoutParams(layoutParams);
            searchEditText.setText("");
        }


        if (CMD_EDIT_AUTO_COMPLETE.equals(txt)) {
            searchEditText.setText("");
            showAutoCompleteDialog();
        }

        if (Arrays.asList(AppState.MODE_GRID, AppState.MODE_COVERS, AppState.MODE_LIST, AppState.MODE_LIST_COMPACT).contains(AppState.get().libraryMode)) {
            prevLibModeFileMeta = AppState.get().libraryMode;
            searchEditText.setEnabled(true);

            searchAdapter.clearItems();
            int segment = getSegment();
            if (MY_FILTER) {
                List<FileMeta> result = new ArrayList<>();
                for (int i = 0; i < items.size(); ++i) {
                    FileMeta meta = items.get(i);
                    boolean isGood = false;
                    boolean isNote = false;
                    if (filterChooseEpub && meta != null && meta.getPathTxt() != null &&
                            meta.getPathTxt().toLowerCase().contains(
                                    searchEditText.getText().toString().toLowerCase()) &&
                            (meta.getPathTxt().toLowerCase().endsWith(".epub"))) {
                        isGood = true;
                        isNote = false;
                    } else if (filterChoosePDFs && meta != null && meta.getPathTxt() != null &&
                            meta.getPathTxt().toLowerCase().contains(
                                    searchEditText.getText().toString().toLowerCase()) &&
                            (meta.getPathTxt().toLowerCase().endsWith(".pdf"))) {
                        isGood = true;
                        isNote = false;
                    } else if (filterChooseNote && meta != null && meta.isNote &&
                            meta.getTitle() != null &&
                            meta.getTitle().toLowerCase().contains(
                                    searchEditText.getText().toString().toLowerCase())
                        ) {
                        isGood = true;
                        isNote = true;
                    }
                    if (isGood) {
                        Date updateTimeDate = null;
                        Date createTimeDate = null;
                        if (isNote) {
                            try {
                                updateTimeDate = new Date(Long.parseLong(meta.updateTime));
                            } catch (Throwable eeee) {
                                eeee.printStackTrace();
                            }
                            try {
                                createTimeDate = new Date(Long.parseLong(meta.createTime));
                            } catch (Throwable eeee) {
                                eeee.printStackTrace();
                            }
                        } else {
                            if (false) {
                                File f = new File(meta.getPath());
                                if (USE_LAST_OPEN) {
                                    //mark
                                }
                                updateTimeDate = new Date(f.lastModified()); //FIXME: read from db
                                Path path = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    path = Paths.get(meta.getPath());
                                    try {
                                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                                        //System.out.println("Creation Time: " + attrs.creationTime());
                                        createTimeDate = new Date(attrs.creationTime().toMillis());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (meta.getDate() != null) {
                                    createTimeDate = new Date(meta.getDate());
                                }
                                if (meta.getIsRecentTime() != null) {
                                    updateTimeDate = new Date(meta.getIsRecentTime());
                                }
                                try {
                                    meta.lastModified = new File(meta.getPath()).lastModified();
                                } catch (Throwable eee) {
                                    eee.printStackTrace();
                                }
                            }
                        }
                        boolean isGood2 = false, isGood3 = false;
                        if (filterBeginDateCreation != null && createTimeDate != null) {
                            if (filterBeginDateCreation.before(createTimeDate)) {
                                isGood2 = true;
                            }
                        } else {
                            isGood2 = true;
                        }
                        if (filterEndDateCreation != null && createTimeDate != null) {
                            if (addDays(filterEndDateCreation, 1).after(createTimeDate)) {
                                isGood3 = true;
                            }
                        } else {
                            isGood3 = true;
                        }

                        boolean isGood4 = false, isGood5 = false;
                        if (filterBeginDateLastOpen != null && updateTimeDate != null) {
                            if (filterBeginDateLastOpen.before(updateTimeDate)) {
                                isGood4 = true;
                            }
                        } else {
                            isGood4 = true;
                        }
                        if (filterEndDateLastOpen != null && updateTimeDate != null) {
                            if (addDays(filterEndDateLastOpen, 1).after(updateTimeDate)) {
                                isGood5 = true;
                            }
                        } else {
                            isGood5 = true;
                        }
                        if (isGood2 && isGood3 && isGood4 && isGood5) {
                            result.add(meta);
                        }
                    }
                }
                if (filterSortType == FILTER_SORT_TYPE_NAME1) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return -1;
                            } else if (f2 == null) {
                                return 1;
                            }
                            String name1 = "", name2 = "";
                            if (f1.isNote) {
                                name1 = f1.getTitle();
                            } else {
                                String path = f1.getPathTxt() != null ? f1.getPathTxt() : "";
                                if (path.toLowerCase().endsWith(".pdf")) {
                                    path = path.substring(0, path.length() - ".pdf".length());
                                } else if (path.toLowerCase().endsWith(".epub")) {
                                    path = path.substring(0, path.length() - ".epub".length());
                                }
                                name1 = path;
                            }
                            if (f2.isNote) {
                                name2 = f2.getTitle();
                            } else {
                                String path = f2.getPathTxt() != null ? f2.getPathTxt() : "";
                                if (path.toLowerCase().endsWith(".pdf")) {
                                    path = path.substring(0, path.length() - ".pdf".length());
                                } else if (path.toLowerCase().endsWith(".epub")) {
                                    path = path.substring(0, path.length() - ".epub".length());
                                }
                                name2 = path;
                            }
                            if (name1 == null) {
                                return -1;
                            } else if (name2 == null) {
                                return 1;
                            }
                            return name1.compareToIgnoreCase(name2);
                        }
                    });
                } else if (filterSortType == FILTER_SORT_TYPE_NAME2) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return 1;
                            } else if (f2 == null) {
                                return -1;
                            }
                            String name1 = "", name2 = "";
                            if (f1.isNote) {
                                name1 = f1.getTitle();
                            } else {
                                String path = f1.getPathTxt() != null ? f1.getPathTxt() : "";
                                if (path.toLowerCase().endsWith(".pdf")) {
                                    path = path.substring(0, path.length() - ".pdf".length());
                                } else if (path.toLowerCase().endsWith(".epub")) {
                                    path = path.substring(0, path.length() - ".epub".length());
                                }
                                name1 = path;
                            }
                            if (f2.isNote) {
                                name2 = f2.getTitle();
                            } else {
                                String path = f2.getPathTxt() != null ? f2.getPathTxt() : "";
                                if (path.toLowerCase().endsWith(".pdf")) {
                                    path = path.substring(0, path.length() - ".pdf".length());
                                } else if (path.toLowerCase().endsWith(".epub")) {
                                    path = path.substring(0, path.length() - ".epub".length());
                                }
                                name2 = path;
                            }
                            if (name1 == null) {
                                return 1;
                            } else if (name2 == null) {
                                return -1;
                            }
                            return -name1.compareToIgnoreCase(name2);
                        }
                    });
                } else if (filterSortType == FILTER_SORT_TYPE_TYPE) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return -1;
                            } else if (f2 == null) {
                                return 1;
                            }
                            String type1 = "", type2 = "";
                            String name1 = "", name2 = "";
                            if (f1.isNote) {
                                name1 = f1.getTitle();
                                type1 = "1note";
                            } else {
                                String path = f1.getPathTxt() != null ? f1.getPathTxt() : "";
                                if (path.toLowerCase().endsWith(".pdf")) {
                                    path = path.substring(0, path.length() - ".pdf".length());
                                    type1 = "3pdf";
                                } else if (path.toLowerCase().endsWith(".epub")) {
                                    path = path.substring(0, path.length() - ".epub".length());
                                    type1 = "2epub";
                                }
                                name1 = path;
                            }
                            if (f2.isNote) {
                                name2 = f2.getTitle();
                                type2 = "1note";
                            } else {
                                String path = f2.getPathTxt() != null ? f2.getPathTxt() : "";
                                if (path.toLowerCase().endsWith(".pdf")) {
                                    path = path.substring(0, path.length() - ".pdf".length());
                                    type2 = "3pdf";
                                } else if (path.toLowerCase().endsWith(".epub")) {
                                    path = path.substring(0, path.length() - ".epub".length());
                                    type2 = "2epub";
                                }
                                name2 = path;
                            }
                            if (type1.compareTo(type2) != 0) {
                                return type1.compareTo(type2);
                            }
                            if (name1 == null) {
                                return -1;
                            } else if (name2 == null) {
                                return 1;
                            }
                            return name1.compareToIgnoreCase(name2);
                        }
                    });
                } else if (filterSortType == FILTER_SORT_TYPE_CREATION_DATE1) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return 1;
                            } else if (f2 == null) {
                                return -1;
                            }
                            Long date1 = f1.getDate();
                            Long date2 = f2.getDate();
                            if (date1 == null) {
                                return 1;
                            } else if (date2 == null) {
                                return -1;
                            }
                            return -date1.compareTo(date2);
                        }
                    });
                } else if (filterSortType == FILTER_SORT_TYPE_CREATION_DATE2) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return -1;
                            } else if (f2 == null) {
                                return 1;
                            }
                            Long date1 = f1.getDate();
                            Long date2 = f2.getDate();
                            if (date1 == null) {
                                return -1;
                            } else if (date2 == null) {
                                return 1;
                            }
                            return date1.compareTo(date2);
                        }
                    });
                } else if (filterSortType == FILTER_SORT_TYPE_LAST_OPENED_DATE1) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return 1;
                            } else if (f2 == null) {
                                return -1;
                            }
                            Long date1 = f1.getIsRecentTime();
                            Long date2 = f2.getIsRecentTime();
                            if (date1 == null) {
                                return 1;
                            } else if (date2 == null) {
                                return -1;
                            }
                            return -date1.compareTo(date2);
                        }
                    });
                } else if (filterSortType == FILTER_SORT_TYPE_LAST_OPENED_DATE2) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return -1;
                            } else if (f2 == null) {
                                return 1;
                            }
                            Long date1 = f1.getIsRecentTime();
                            Long date2 = f2.getIsRecentTime();
                            if (date1 == null) {
                                return -1;
                            } else if (date2 == null) {
                                return 1;
                            }
                            return date1.compareTo(date2);
                        }
                    });
                } else if (filterSortType == FILTER_SORT_TYPE_LAST_MODIFIED_DATE1) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return 1;
                            } else if (f2 == null) {
                                return -1;
                            }
                            Long date1 = f1.lastModified;
                            Long date2 = f2.lastModified;
                            if (date1 == null) {
                                return 1;
                            } else if (date2 == null) {
                                return -1;
                            }
                            return -date1.compareTo(date2);
                        }
                    });
                } else if (filterSortType == FILTER_SORT_TYPE_LAST_MODIFIED_DATE2) {
                    Collections.sort(result, new Comparator<FileMeta>() {
                        @Override
                        public int compare(FileMeta f1, FileMeta f2) {
                            if (f1 == null) {
                                return -1;
                            } else if (f2 == null) {
                                return 1;
                            }
                            Long date1 = f1.lastModified;
                            Long date2 = f2.lastModified;
                            if (date1 == null) {
                                return -1;
                            } else if (date2 == null) {
                                return 1;
                            }
                            return date1.compareTo(date2);
                        }
                    });
                } else {
                    //skip
                }
                searchAdapter.getItemsList().addAll(result);
            } else if (false) {
                searchAdapter.getItemsList().addAll(items);
            } else {
                List<FileMeta> result = new ArrayList<>();
                if (segment == 0) {
                    for (int i = 0; i < items.size(); ++i) {
                        FileMeta meta = items.get(i);
                        if (meta != null && meta.getPathTxt() != null &&
                                meta.getPathTxt().toLowerCase().contains(
                                        searchEditText.getText().toString().toLowerCase()) &&
                                (meta.getPathTxt().toLowerCase().endsWith(".epub") ||
                                        meta.getPathTxt().toLowerCase().endsWith(".pdf"))) {
                            result.add(meta);
                        }
                    }
                    searchAdapter.getItemsList().addAll(result);
                } else if (segment == 1) {
                    for (int i = 0; i < items.size(); ++i) {
                        FileMeta meta = items.get(i);
                        if (meta != null && meta.getPathTxt() != null &&
                                (meta.getPathTxt().toLowerCase().endsWith(".pdf"))) {
                            result.add(meta);
                        }
                    }
                    searchAdapter.getItemsList().addAll(result);
                } else if (segment == 2) {
                    for (int i = 0; i < items.size(); ++i) {
                        FileMeta meta = items.get(i);
                        if (meta != null && meta.getPathTxt() != null &&
                                (meta.getPathTxt().toLowerCase().endsWith(".epub"))) {
                            result.add(meta);
                        }
                    }
                    searchAdapter.getItemsList().addAll(result);
                }
            }
            searchAdapter.notifyDataSetChanged();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    searchAdapter.notifyDataSetChanged();

                }
            }, 1000);

            // recyclerView.scrollToPosition(0);

        } else {
            prevLibModeAuthors = AppState.get().libraryMode;
            searchEditText.setEnabled(false);

            String empty = "";
            if (AppState.get().libraryMode == AppState.MODE_AUTHORS) {
                searchEditText.setHint(R.string.author);
                empty = EMPTY_ID + getString(R.string.no_author);
            } else if (AppState.get().libraryMode == AppState.MODE_SERIES) {
                searchEditText.setHint(R.string.serie);
                empty = EMPTY_ID + getString(R.string.no_serie);
            } else if (AppState.get().libraryMode == AppState.MODE_GENRE) {
                searchEditText.setHint(R.string.genre);
                empty = EMPTY_ID + getString(R.string.no_genre);
            } else if (AppState.get().libraryMode == AppState.MODE_KEYWORDS) {
                searchEditText.setHint(R.string.keywords);
                empty = EMPTY_ID + getString(R.string.no_keywords);
            } else if (AppState.get().libraryMode == AppState.MODE_USER_TAGS) {
                searchEditText.setHint(R.string.my_tags);
                empty = EMPTY_ID + getActivity().getString(R.string.no_tag);
            } else if (AppState.get().libraryMode == AppState.MODE_LANGUAGES) {
                searchEditText.setHint(R.string.language);
                empty = EMPTY_ID + getActivity().getString(R.string.no_language);
            } else if (AppState.get().libraryMode == AppState.MODE_PUBLICATION_DATE) {
                searchEditText.setHint(R.string.publication_date);
                empty = EMPTY_ID + getActivity().getString(R.string.empy);
            } else if (AppState.get().libraryMode == AppState.MODE_PUBLISHER) {
                searchEditText.setHint(R.string.publisher);
                empty = EMPTY_ID + getActivity().getString(R.string.empy);
            }

            List<String> list = AppDB.get().getAll(AppDB.SEARCH_IN.getByMode(AppState.get().libraryMode));
            if (AppState.get().libraryMode == AppState.MODE_LANGUAGES) {
                List<String> res = new ArrayList<String>();
                String prev = null;
                for (String ln : list) {
                    if (TxtUtils.isEmpty(ln)) {
                        ln = "";
                    } else if (ln.length() > 2) {
                        ln = ln.substring(0, 2);
                    }

                    String full = DialogTranslateFromTo.getLanuageByCode(ln);
                    String lnLow = ln.toLowerCase(Locale.US);

                    if (!lnLow.equals(prev)) {
                        prev = lnLow;
                        res.add(full + " (" + lnLow + ")");
                    }
                }
                list = res;

            }
            if (AppState.get().libraryMode == AppState.MODE_PUBLICATION_DATE) {
                Collections.reverse(list);
            }
            list.add(0, empty);
        }

        showBookCount();

//        if (searchAdapter != null) {
//            tvPageInfo.setText("Total item(s) : " + searchAdapter.getCount() + "");
//        }

        updateSearchEmpty();
    }

    private void sortByPopup(final View view) {


        MyPopupMenu popup = new MyPopupMenu(getActivity(), view);
        for (final AppDB.SORT_BY sortBy : AppDB.SORT_BY.values()) {
            popup.getMenu().add(sortBy.getResName()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AppState.get().sortBy = sortBy.getIndex();
                    searchAndOrderAsync();
                    return false;
                }
            });
        }
        popup.show();
    }

    //@Override
    public void onDoubleClick() {
        isOnTop = !isOnTop;
    }

    public void showBookCount() {

    }

    //@Override
    public boolean isBackPressed() {
        AppState.get().libraryMode = prevLibModeFileMeta;
        onGridList();
        searchAndOrderAsync();
        return true;
    }

    //@Override
    public void notifyFragment() {
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
        if (searchEditText != null) {
            if (!BooksService.isRunning) {
                searchEditText.setHint(R.string.search);
            }
        }
    }

    //@Override
    public void resetFragment() {
        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onGridList();
                    searchAndOrderAsync();
                }
            }, 150);
        }
    }


    //@Override
    public void onDestroy2() {
        //super.onDestroy();
        LOG.d("SearchFragment2 onDestroy");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        cacheItems = null;
    }

    public void setSearch(String text) {
        if (text != null && searchEditText != null) {
            searchEditText.setText(text);
        }
    }

    private void updateSearchEmpty() {
        if (true) { //searchEditText != null && searchEditText.getText().toString().length() > 0) {
            ivEmpty1.setImageResource(R.drawable.glyphicons_28_search);
            tvEmpty1.setText("No results");
            tvEmpty2.setText("We couldn’t find any results for that. Check your spelling or try a different search term.");
        } else {
            ivEmpty1.setImageResource(R.drawable.ic_baseline_folder_copy_24);
            tvEmpty1.setText("Nothing here yet");
            tvEmpty2.setText("This space is empty. Add files to get started—drag and drop files, upload from device, or create a new one.");
        }
    }

















    //------------------


    private int[] rlFileType ={
            R.id.rlFileType1,
            R.id.rlFileType2,
            R.id.rlFileType3,
    };
    private int[] rlCreationDate ={
            R.id.rlCreationDate1,
            R.id.rlCreationDate2,
            R.id.rlCreationDate3,
            R.id.rlCreationDate4,
    };
    private int[] rlLastOpen ={
            R.id.rlLastOpen1,
            R.id.rlLastOpen2,
            R.id.rlLastOpen3,
            R.id.rlLastOpen4,
    };
    View g_view = null;
    private void onClickFileType(View view, int fileId) {
        g_view = view;
        View btnOff = view.findViewById(fileId).findViewWithTag("btnOff");
        View btnOn = view.findViewById(fileId).findViewWithTag("btnOn");
        if (btnOff != null && btnOn != null) {
            if (btnOff.getVisibility() == View.VISIBLE) {
//                for (int id_ : rlFileType) {
//                    view.findViewById(id_).findViewWithTag("btnOff").setVisibility(View.VISIBLE);
//                    view.findViewById(id_).findViewWithTag("btnOn").setVisibility(View.GONE);
//                }
                btnOff.setVisibility(View.GONE);
                btnOn.setVisibility(View.VISIBLE);
            } else {
                btnOff.setVisibility(View.VISIBLE);
                btnOn.setVisibility(View.GONE);
            }
        }
    }
    private void onClickCreationDate(View view, int fileId) {
        View btnOff = view.findViewById(fileId).findViewWithTag("btnOff");
        View btnOn = view.findViewById(fileId).findViewWithTag("btnOn");
        if (btnOff != null && btnOn != null) {
            if (btnOff.getVisibility() == View.VISIBLE) {
                for (int id_ : rlCreationDate) {
                    view.findViewById(id_).findViewWithTag("btnOff").setVisibility(View.VISIBLE);
                    view.findViewById(id_).findViewWithTag("btnOn").setVisibility(View.GONE);
                }
                btnOff.setVisibility(View.GONE);
                btnOn.setVisibility(View.VISIBLE);
                if (fileId == R.id.rlCreationDate4) {
                    creationDate4_open();
                } else {
                    updateFilter();
                }
            } else {
                btnOff.setVisibility(View.VISIBLE);
                btnOn.setVisibility(View.GONE);
                updateFilter();
            }
        }
    }
    private void onClickLastOpen(View view, int fileId) {
        View btnOff = view.findViewById(fileId).findViewWithTag("btnOff");
        View btnOn = view.findViewById(fileId).findViewWithTag("btnOn");
        if (btnOff != null && btnOn != null) {
            if (btnOff.getVisibility() == View.VISIBLE) {
                for (int id_ : rlLastOpen) {
                    view.findViewById(id_).findViewWithTag("btnOff").setVisibility(View.VISIBLE);
                    view.findViewById(id_).findViewWithTag("btnOn").setVisibility(View.GONE);
                }
                btnOff.setVisibility(View.GONE);
                btnOn.setVisibility(View.VISIBLE);
                if (fileId == R.id.rlLastOpen4) {
                    lastOpenDate4_open();
                } else {
                    updateFilter();
                }
            } else {
                btnOff.setVisibility(View.VISIBLE);
                btnOn.setVisibility(View.GONE);
                updateFilter();
            }
        }
    }


    private void onCreateViewFilter(View view) {
        for (int fileId : rlFileType) {
            view.findViewById(fileId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view_) {
                    onClickFileType(view, fileId);
                    updateFilter();
                }
            });
            onClickFileType(view, fileId); //init check all
        }
        for (int fileId : rlCreationDate) {
            view.findViewById(fileId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view_) {
                    onClickCreationDate(view, fileId);
                }
            });
        }
        for (int fileId : rlLastOpen) {
            view.findViewById(fileId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view_) {
                    onClickLastOpen(view, fileId);
                }
            });
        }
        view.findViewById(R.id.rlSortedBy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view_) {
                if (false) {
                    PopupMenu popup = new PopupMenu(getActivity(), view_);
                    int menuId = R.menu.popup_home4_sorted; //.popup_directory;
                    popup.getMenuInflater().inflate(menuId, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            TextView textView = (TextView) view.findViewById(R.id.rlSortedBy).findViewWithTag("tvName1");
                            if (menuItem != null && textView != null) {
                                if (menuItem.getItemId() == R.id.menu_name) {
                                    textView.setText("Name");
                                    filterSortType = FILTER_SORT_TYPE_NAME1;
                                    updateFilter();
                                    return true;
                                } else if (menuItem.getItemId() == R.id.menu_last_opened_date) {
                                    textView.setText("Last opened date");
                                    filterSortType = FILTER_SORT_TYPE_NAME1;
                                    updateFilter();
                                    return true;
                                } else if (menuItem.getItemId() == R.id.menu_size) {
                                    textView.setText("Size");
                                    filterSortType = FILTER_SORT_TYPE_NAME1;
                                    updateFilter();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                    popup.show();
                } else {
                    HomeFragment4MenuDialog.show(getActivity(), view_, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView textView = (TextView) g_view.findViewById(R.id.rlSortedBy).findViewWithTag("tvName1");
                            if (view == null) {
                                return;
                            }
                            if (view.getId() == R.id.llSortedName1) {
                                textView.setText("Name / Title, A-Z");
                                filterSortType = FILTER_SORT_TYPE_NAME1;
                                updateFilter();
                            } else if (view.getId() == R.id.llSortedName2) {
                                textView.setText("Name / Title, Z-A");
                                filterSortType = FILTER_SORT_TYPE_NAME2;
                                updateFilter();
                            } else if (view.getId() == R.id.llSortedType1) {
                                textView.setText("Type");
                                filterSortType = FILTER_SORT_TYPE_TYPE;
                                updateFilter();
                            } else if (view.getId() == R.id.llSortedCreationDate1) {
                                textView.setText("Creation Date, newest first");
                                filterSortType = FILTER_SORT_TYPE_CREATION_DATE1;
                                updateFilter();
                            } else if (view.getId() == R.id.llSortedCreationDate2) {
                                textView.setText("Creation Date, oldest first");
                                filterSortType = FILTER_SORT_TYPE_CREATION_DATE2;
                                updateFilter();
                            } else if (view.getId() == R.id.llSortedLastOpened1) {
                                textView.setText("Last opened, newest first");
                                filterSortType = FILTER_SORT_TYPE_LAST_OPENED_DATE1;
                                updateFilter();
                            } else if (view.getId() == R.id.llSortedLastOpened2) {
                                textView.setText("Last opened, oldest first");
                                filterSortType = FILTER_SORT_TYPE_LAST_OPENED_DATE2;
                                updateFilter();
                            } else if (view.getId() == R.id.llSortedLastModified1) {
                                textView.setText("Last modified, newest first");
                                filterSortType = FILTER_SORT_TYPE_LAST_MODIFIED_DATE1;
                                updateFilter();
                            } else if (view.getId() == R.id.llSortedLastModified2) {
                                textView.setText("Last modified, oldest first");
                                filterSortType = FILTER_SORT_TYPE_LAST_MODIFIED_DATE2;
                                updateFilter();
                            }
                        }
                    });
                }
            }
        });
    }

    public void updateFilter() {
        if (g_view != null) {
            filterChooseNote = g_view.findViewById(R.id.rlFileType1).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE;
            filterChooseEpub = g_view.findViewById(R.id.rlFileType2).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE;
            filterChoosePDFs = g_view.findViewById(R.id.rlFileType3).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE;

            if (g_view.findViewById(R.id.rlCreationDate1).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE) {
                //today
                filterBeginDateCreation = beginOfDay(new Date());
                filterEndDateCreation = null;
                filterCreationButton = 1;
            } else if (g_view.findViewById(R.id.rlCreationDate2).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE) {
                //yesterday
                filterBeginDateCreation = addDays(beginOfDay(new Date()), -1);
                filterEndDateCreation = beginOfDay(new Date());  //today
                filterCreationButton = 2;
            } else if (g_view.findViewById(R.id.rlCreationDate3).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE) {
                //this week
                filterBeginDateCreation = addDays(beginOfDay(new Date()), -7);
                filterEndDateCreation = null;
                filterCreationButton = 3;
            } else if (g_view.findViewById(R.id.rlCreationDate4).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE) {
                //range
//                filterBeginDateCreation = null;
//                filterEndDateCreation = null;
                filterCreationButton = 4;
            } else {
                filterBeginDateCreation = null;
                filterEndDateCreation = null;
                filterCreationButton = 0;
            }


            if (g_view.findViewById(R.id.rlLastOpen1).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE) {
                //today
                filterBeginDateLastOpen = beginOfDay(new Date());
                filterEndDateLastOpen = null;
                filterLastOpenButton = 1;
            } else if (g_view.findViewById(R.id.rlLastOpen2).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE) {
                //yesterday
                filterBeginDateLastOpen = addDays(beginOfDay(new Date()), -1);
                filterEndDateLastOpen = beginOfDay(new Date());  //today
                filterLastOpenButton = 2;
            } else if (g_view.findViewById(R.id.rlLastOpen3).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE) {
                //this week
                filterBeginDateLastOpen = addDays(beginOfDay(new Date()), -7);
                filterEndDateLastOpen = null;
                filterLastOpenButton = 3;
            } else if (g_view.findViewById(R.id.rlLastOpen4).findViewWithTag("btnOn")
                    .getVisibility() == View.VISIBLE) {
                //range
//                filterBeginDateLastOpen = null;
//                filterEndDateLastOpen = null;
                filterLastOpenButton = 4;
            } else {
                filterBeginDateLastOpen = null;
                filterEndDateLastOpen = null;
                filterLastOpenButton = 0;
            }
        }
        saveFilter();
        searchAndOrderAsync();
    }
    private void creationDate4_open() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        if (filterBeginDateCreation != null && filterEndDateCreation != null) {
            builder.setSelection(new Pair<Long, Long>(filterBeginDateCreation.getTime(), filterEndDateCreation.getTime()));
        }
        builder.setTitleText("Creation Date");
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
//                String dateStr = null;
//                if (selection != null) {
//                    Date date = new Date(selection);
//                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
//                    dateStr = sdf.format(date);
//                }
//                if (dateStr != null) {
//                    BookActivity4Utils.editMeetingDate(mAct, dateStr, selection);
//                }
                //see https://github.com/moonazn/java-tourrand-contest/blob/main/app/src/main/java/com/tourbus/tourrand/DateQActivity.java
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()); //Locale.ENGLISH);
                if (false) {
                    //FIXME:MUST BE "UTC", because it's always UTC timezone
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //sdf.setTimeZone(TimeZone.getDefault());
                } else {
                    //use beginOfDayUTC();
                }
                if (selection.first != null) {
                    filterBeginDateCreation = beginOfDayUTC(new Date(selection.first));
                } else {
                    filterBeginDateCreation = null;
                }
                if (selection.second != null) {
                    filterEndDateCreation = beginOfDayUTC(new Date(selection.second));
                } else {
                    filterEndDateCreation = null;
                }
                String str =
                        (filterBeginDateCreation != null ? sdf.format(filterBeginDateCreation) : "") +
                        " - " +
                        (filterEndDateCreation != null ? sdf.format(filterEndDateCreation) : "");
                if (filterBeginDateCreation == null && filterEndDateCreation == null) {
                    str = "Custom day range";
                }
                ((TextView)g_view.findViewById(R.id.tvCreationDate4_off)).setText(str);
                ((TextView)g_view.findViewById(R.id.tvCreationDate4_on)).setText(str);
                filterCreationButtonName = str;
                updateFilter();
            }
        });
        materialDatePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
//                filterBeginDateCreation = null;
//                filterEndDateCreation = null;
//                ((TextView)g_view.findViewById(R.id.tvCreationDate4_off)).setText("Custom day range");
//                ((TextView)g_view.findViewById(R.id.tvCreationDate4_on)).setText("Custom day range");
//                updateFilter();
            }
        });
        materialDatePicker.show(getActivity().getSupportFragmentManager(), "CREATION_DATE_PICKER_TAG");
    }

    private void lastOpenDate4_open() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        if (filterBeginDateLastOpen != null && filterEndDateLastOpen != null) {
            builder.setSelection(new Pair<Long, Long>(filterBeginDateLastOpen.getTime(), filterEndDateLastOpen.getTime()));
        }
        builder.setTitleText("Last Opened Date");
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
//                String dateStr = null;
//                if (selection != null) {
//                    Date date = new Date(selection);
//                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
//                    dateStr = sdf.format(date);
//                }
//                if (dateStr != null) {
//                    BookActivity4Utils.editMeetingDate(mAct, dateStr, selection);
//                }
                //see https://github.com/moonazn/java-tourrand-contest/blob/main/app/src/main/java/com/tourbus/tourrand/DateQActivity.java
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()); //Locale.ENGLISH);
//                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                if (false) {
                    //FIXME:MUST BE "UTC", because it's always UTC timezone
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //sdf.setTimeZone(TimeZone.getDefault());
                } else {
                    //use beginOfDayUTC();
                }
                if (selection.first != null) {
                    filterBeginDateLastOpen = beginOfDayUTC(new Date(selection.first));
                } else {
                    filterBeginDateLastOpen = null;
                }
                if (selection.second != null) {
                    filterEndDateLastOpen = beginOfDayUTC(new Date(selection.second));
                } else {
                    filterEndDateLastOpen = null;
                }
                String str =
                        (filterBeginDateLastOpen != null ? sdf.format(filterBeginDateLastOpen) : "") +
                                " - " +
                                (filterEndDateLastOpen != null ? sdf.format(filterEndDateLastOpen) : "");
                if (filterBeginDateLastOpen == null && filterEndDateLastOpen == null) {
                    str = "Custom day range";
                }
                ((TextView)g_view.findViewById(R.id.tvLastOpenDate4_off)).setText(str);
                ((TextView)g_view.findViewById(R.id.tvLastOpenDate4_on)).setText(str);
                filterLastOpenButtonName = str;
                updateFilter();
            }
        });
        materialDatePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
//                filterBeginDateLastOpen = null;
//                filterEndDateLastOpen = null;
//                ((TextView)g_view.findViewById(R.id.tvLastOpenDate4_off)).setText("Custom day range");
//                ((TextView)g_view.findViewById(R.id.tvLastOpenDate4_on)).setText("Custom day range");
//                updateFilter();
            }
        });
        materialDatePicker.show(getActivity().getSupportFragmentManager(), "LAST_OPEN_DATE_PICKER_TAG");
    }

    //https://github.com/avesha/android.fba.toolkit/blob/master/engine/src/main/java/ru/profi1c/engine/util/DateHelper.java
    private Date beginOfDay(Date dt) {
        //package cn.hutool.core.date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date addDays(Date date, int count) {
        Calendar cal = getEmptyCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, count);
        return cal.getTime();
    }

    public static Calendar getEmptyCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        return cal;
    }














    //-------------------------
    private final static String TAG = "HomeFragment4";
    private final static boolean USE_EXTERNAL_FILE = true;

    private static final String SHARED_PREFERENCES_NAME = "FlutterSharedPreferences";
    public final static boolean USE_NEW_NOTE = true;
    public final static String APPNAME = "txkjnote";
    public final static String APPNAME_NEW = "txkjnote2";

    private static final String SHARE_PACKAGE_NAME = "com.txkj.notemobile";//"online.xournal.mobile";
    public static final String KEY_RECENT_FILES = "recentFiles";
    public List<FileMeta> prepareDataInBackground_note() {
        List<FileMeta> recentNoteList2__ = new ArrayList<>();
        String txt = searchEditText.getText().toString().trim();
        if (false) {
            Context useCount = null;
            try {
                useCount = getActivity().createPackageContext(SHARE_PACKAGE_NAME,
                        Context.CONTEXT_IGNORE_SECURITY);

                SharedPreferences ps = useCount.getSharedPreferences(SHARED_PREFERENCES_NAME,
                        Context.MODE_WORLD_READABLE);

                String recentFiles = ps.getString(KEY_RECENT_FILES, "");
                Log.e(TAG, "recentFiles: " + recentFiles);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String dirPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
            boolean kkk2 = new File(dirPath).mkdirs();
            if (new File(dirPath, SDNotesDatabase.DATABASE_NAME).exists()) {
                SDNotesDatabase mDatabase = new SDNotesDatabase(getActivity(), dirPath);
                List<NoteItem> items = mDatabase.getAllItems();
                for (NoteItem itemNote : items) {
                    try {
                        LinkedJSONObject item = new LinkedJSONObject(itemNote.getNoteContent());
                        if (item != null) {
                            String preview = item.optString("preview");
                            String name = item.optString("name");
                            String path = item.optString("path");
                            String createTime = item.optString("createTime");
                            String updateTime = item.optString("updateTime");
                            String dispName = item.optString("dispName");

                            FileMeta fileMeta = new FileMeta();
                            fileMeta.isNote = true;
                            fileMeta.updateTime = updateTime;
                            fileMeta.createTime = createTime;
                            try {
                                fileMeta.setIsRecentTime(Long.parseLong(updateTime));
                                fileMeta.lastModified = Long.parseLong(updateTime);
                            } catch (Throwable eee) {
                                eee.printStackTrace();
                            }
                            try {
                                fileMeta.setDate(Long.parseLong(createTime));
                            } catch (Throwable eee) {
                                eee.printStackTrace();
                            }
                            fileMeta.setPathTxt(path);
                            fileMeta.setTitle((dispName != null && dispName.length() > 0) ? dispName : name);
                            if (updateTime != null) {
                                String updateTimeStr = null;
                                if (updateTime != null && updateTime.length() > 0) {
                                    try {
                                        Date date = new Date(Long.parseLong(updateTime));
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        updateTimeStr = format.format(date);
                                    } catch (Throwable eeee) {
                                        eeee.printStackTrace();
                                    }
                                }
                                fileMeta.setDateTxt(updateTimeStr);
                            }
                            fileMeta.setPath(preview != null ? BaseExtractor.BASE64_PREFIX + preview : null);

                            if (txt != null && txt.length() > 0) {
                                if (name.toLowerCase().contains(txt.toLowerCase())) {
                                    recentNoteList2__.add(fileMeta);
                                }
                            } else {
                                recentNoteList2__.add(fileMeta);
                            }
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            } else {
                if (NoteFragment2.LOAD_OLD_DATA) {
                    String recentFiles = "";
                    if (USE_EXTERNAL_FILE) {
                        try {
                            String rootPath = null;
                            if (USE_NEW_NOTE) {
                                rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
                            } else {
                                rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME).toString();
                            }
                            boolean kkk = new File(rootPath).mkdirs();
                            if (new File(rootPath, "flutter." + KEY_RECENT_FILES + ".txt").exists()) {
                                InputStream fis = new FileInputStream(new File(rootPath, "flutter." + KEY_RECENT_FILES + ".txt"));
                                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                                BufferedReader reader = new BufferedReader(isr);
                                StringBuffer recentFilesBuffer = new StringBuffer();
                                while (true) {
                                    String line = reader.readLine();
                                    if (line != null) {
                                        recentFilesBuffer.append(line);
                                        recentFilesBuffer.append("\n");
                                    } else {
                                        break;
                                    }
                                }
                                recentFiles = recentFilesBuffer.toString();
                                reader.close();
                                isr.close();
                                fis.close();
                            }
                        } catch (Throwable eee) {
                            eee.printStackTrace();
                        }
                    }
                    //Log.e(TAG, "recentFiles: " + recentFiles);

                    List<FileMeta> recentNoteList2_temp = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(recentFiles);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            LinkedJSONObject item = jsonArray.getJSONObject(i);
                            if (item != null) {
                                String preview = item.optString("preview");
                                String name = item.optString("name");
                                String path = item.optString("path");
                                String createTime = item.optString("createTime");
                                String updateTime = item.optString("updateTime");
                                String dispName = item.optString("dispName");

                                FileMeta fileMeta = new FileMeta();
                                fileMeta.isNote = true;
                                fileMeta.updateTime = updateTime;
                                fileMeta.createTime = createTime;
                                try {
                                    fileMeta.setIsRecentTime(Long.parseLong(updateTime));
                                    fileMeta.lastModified = Long.parseLong(updateTime);
                                } catch (Throwable eee) {
                                    eee.printStackTrace();
                                }
                                try {
                                    fileMeta.setDate(Long.parseLong(createTime));
                                } catch (Throwable eee) {
                                    eee.printStackTrace();
                                }
                                fileMeta.setPathTxt(path);
                                fileMeta.setTitle((dispName != null && dispName.length() > 0) ? dispName : name);
                                if (updateTime != null) {
                                    String updateTimeStr = null;
                                    if (updateTime != null && updateTime.length() > 0) {
                                        try {
                                            Date date = new Date(Long.parseLong(updateTime));
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            updateTimeStr = format.format(date);
                                        } catch (Throwable eeee) {
                                            eeee.printStackTrace();
                                        }
                                    }
                                    fileMeta.setDateTxt(updateTimeStr);
                                }
                                fileMeta.setPath(preview != null ? BaseExtractor.BASE64_PREFIX + preview : null);

                                if (txt != null && txt.length() > 0) {
                                    if (name.toLowerCase().contains(txt.toLowerCase())) {
                                        recentNoteList2_temp.add(fileMeta);
                                    }
                                } else {
                                    recentNoteList2_temp.add(fileMeta);
                                }
                            }
                        }
                        //recentNoteList2__.clear();
                        if (USE_NEW_NOTE) {
                            for (int i = 0; i < recentNoteList2_temp.size(); ++i) {
                                recentNoteList2__.add(recentNoteList2_temp.get(i));
                            }
                        } else {
                            for (int i = recentNoteList2_temp.size() - 1; i >= 0; --i) {
                                recentNoteList2__.add(recentNoteList2_temp.get(i));
                            }
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            }
        }

        return recentNoteList2__; //new ArrayList<>();
    }

    private static Date beginOfDayUTC(Date dt) {
        //package cn.hutool.core.date;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTime(dt);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeZone(TimeZone.getDefault());
        cal2.set(Calendar.YEAR, y);
        cal2.set(Calendar.MONTH, m);
        cal2.set(Calendar.DAY_OF_MONTH, d);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        return cal2.getTime();
    }
}