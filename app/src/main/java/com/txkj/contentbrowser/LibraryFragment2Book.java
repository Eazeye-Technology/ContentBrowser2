package com.txkj.contentbrowser;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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

import com.bumptech.glide.Glide;
import com.dseink.EinkUtils;
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
import com.tvg.AutoWrapViewGroup;
import com.txkj.contentbrowser2.R;
import com.dseink.DualScreenConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

//FIXME:WorkManager.getInstance(getContext(),

public class LibraryFragment2Book extends Fragment {
    private final static String STR_NO_ITEMS = "Nothing here yet";//"No items.";
    private final static String STR_LOADING = "Loading...";

    //find-add-folder
    private final static boolean IS_LOG = AppsConfig.IS_LOG;
    //AppState.get().isSkipFolderWithNOMEDIA;
    public final static boolean FORCE_SKIP_NOMEDIA = false;
    //Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);

    //for override
    public int getInitIndex() {
        return 2; //0:all, 1:pdf, 2:epub(book)
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
        View view = inflater.inflate(R.layout.fragment_library2_book, container, false);
        //View view = inflater.inflate(R.layout.fragment_search2, container, false);
        AutoWrapViewGroup autoWrapViewGroup = (AutoWrapViewGroup) view.findViewById(R.id.autoWrapViewGroup);
        if (getInitIndex() == 1) {
            autoWrapViewGroup.output("PDFs", ""); //"PDFs: "
        } else {
            autoWrapViewGroup.output("Books", ""); //"Books: "
        }
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
        if (EinkUtils.getCurrentScreenPos(getActivity(), 0) ==
                DualScreenConstant.EXTRA_LAUNCH_SCREEN_PANEL_BOTH) {
            recyclerView.setNumColumns(3);
        } else {
            if (DM.heightPixels > DM.widthPixels) {
                recyclerView.setNumColumns(3);//4);//3);
            } else {
                recyclerView.setNumColumns(4);//6);//4);
            }
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
                //int bookType = bookInfoList.get(position).getBookType();
//                int pageIdx = position;
//                Intent intent = new Intent(BookListActivity.this, BookActivity.class);
//                intent.setData(((FastFile)files.get(pageIdx)).getUri());
//                startActivity(intent);

                try {

                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    if (false) {
                        intent.setClassName("com.txkj.pdfreader",
                                "org.ebookdroid.ui.viewer.VerticalViewActivity");
                    } else {
                        intent.setClassName("com.txkj.readingapp",
                                "org.ebookdroid.ui.viewer.VerticalViewActivity");
                        DualScreenConstant.launchFull(intent, false, false);
                    }
                    FileMeta meta = pageList.get(position);
                    AppData.get().addRecent(new SimpleMeta(meta.getPath(), System.currentTimeMillis()));

                    File file = new File(meta.getPath());
                    intent.setData(Uri.fromFile(file));
                    //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
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

        tvPageInfo = (TextView) view.findViewById(R.id.tvPageInfo);

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
    TextView tvPageInfo;
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
    Runnable saveAutoComplete = new Runnable() {

        @Override
        public void run() {
            String txt = searchEditText.getText().toString().trim();
            if (TxtUtils.isNotEmpty(txt) && !txt.startsWith("@@") && !StringDB.contains(AppState.get().myAutoCompleteDb, txt)) {
                if (!searchAdapter.getItemsList().isEmpty()) {
                    StringDB.add(AppState.get().myAutoCompleteDb, txt, (db) -> AppState.get().myAutoCompleteDb = db);
                    autocomplitions.add(txt);
                    updateFilterListAdapter();
                }
            }

        }
    };
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
            handler.removeCallbacks(saveAutoComplete);

            if (StringDB.contains(AppState.get().myAutoCompleteDb, s.toString().trim())) {
                //skip
            } else {
                handler.postDelayed(saveAutoComplete, 10000);
            }

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

//long lastTime = System.currentTimeMillis();
            List<FileMeta> searchBy = AppDB.get().searchBy(txt, AppDB.SORT_BY.getByID(AppState.get().sortBy), AppState.get().isSortAsc);
//Log.e("LibraryFragment", "LibraryFragment:" + (System.currentTimeMillis() - lastTime));

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
            if (false) {
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

        if (searchAdapter != null) {
            tvPageInfo.setText("Total item(s) : " + searchAdapter.getCount() + "");
        }

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
        if (searchEditText != null && searchEditText.getText().toString().length() > 0) {
            ivEmpty1.setImageResource(R.drawable.glyphicons_28_search);
            tvEmpty1.setText("No results");
            tvEmpty2.setText("We couldn’t find any results for that. Check your spelling or try a different search term.");
        } else {
            ivEmpty1.setImageResource(R.drawable.ic_baseline_folder_copy_24);
            tvEmpty1.setText("Nothing here yet");
            tvEmpty2.setText("This space is empty. Add files to get started—drag and drop files, upload from device, or create a new one.");
        }
    }
}