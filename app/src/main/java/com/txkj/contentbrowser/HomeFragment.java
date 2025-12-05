package com.txkj.contentbrowser;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.BaseExtractor;
import com.bumptech.glide.Glide;
import com.foobnix.LibreraApp;
import com.foobnix.android.utils.Apps;
import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.LOG;
import com.foobnix.android.utils.ResultResponse;
import com.foobnix.android.utils.Safe;
import com.foobnix.android.utils.TxtUtils;
import com.foobnix.dao2.FileMeta;
import com.foobnix.ext.CacheZipUtils;
import com.foobnix.model.AppData;
import com.foobnix.model.AppState;
import com.foobnix.model.SimpleMeta;
import com.foobnix.pdf.info.AppsConfig;
import com.foobnix.pdf.info.ExtUtils;
import com.foobnix.pdf.info.IMG;
import com.foobnix.pdf.info.TintUtil;
import com.foobnix.pdf.info.view.AlertDialogs;
import com.foobnix.pdf.info.view.MyPopupMenu;
import com.foobnix.pdf.info.view.MyProgressBar;
import com.foobnix.pdf.info.wrapper.DocumentController;
import com.foobnix.pdf.info.wrapper.PopupHelper;
import com.foobnix.pdf.search.activity.msg.NotifyAllFragments;
import com.foobnix.pdf.search.activity.msg.OpenDirMessage;
import com.foobnix.pdf.search.activity.msg.UpdateAllFragments;
import com.foobnix.sys.ImageExtractor;
import com.foobnix.sys.TempHolder;
import com.foobnix.ui2.AppDB;
import com.foobnix.ui2.MainTabs2;
import com.foobnix.ui2.adapter.AuthorsAdapter2;
import com.foobnix.ui2.adapter.DefaultListeners;
import com.foobnix.ui2.adapter.FileMetaAdapter;
import com.foobnix.ui2.fast.FastScrollRecyclerView;
import com.foobnix.ui2.fast.FastScrollStateChangeListener;
import com.txkj.contentbrowser2.R;
import com.txkj.contentbrowser2.activity.MainActivity2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.librera.JSONArray;
import org.librera.LinkedJSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
DefaultListeners
ExtUtils.openFile(a, result);
->
ExtUtils:
LOG.d("openFile isExteralSD normal");
File file = new File(meta.getPath());
ExtUtils.showDocumentWithoutDialog2(a, file);
->
if (MainTabs2.MOD_VERSION) {
	AppSP.get().readingMode = AppState.READING_MODE_SCROLL;
	showDocumentWithoutDialog(c, file, null);
	return true;
}
->
public static void showDocumentWithoutDialog(final Context c, final File file, String playlist) {
	showDocumentWithoutDialog2(c, Uri.fromFile(file), 0.0f, playlist);
}
->
Safe.run(new Runnable() {
	@Override
	public void run() {
		showDocumentInner(c, uri, percent, playList);
	}
}, true);
->
final Intent intent = new Intent(c, VerticalViewActivity.class);
intent.setData(checkPlaylisturi(uri, intent, playlist));
c.startActivity(intent);
return uri;
 */
public class HomeFragment extends Fragment {
    private final static String STR_NO_ITEMS = "Nothing here yet";//"No items.";
    private final static String STR_LOADING = "Loading...";

    private TextView tvEmpty1, tvEmpty2;
    private LinearLayout llEmpty1, llEmpty2;
    private View progressLoading1, loadingContent1;
    private View progressLoading2, loadingContent2;

    private final static int MAX_RECENT = 10;

    private final static boolean USE_EXTERNAL_FILE = true;
//    private final static String APPNAME = "txkjnote";

    private static final String SHARED_PREFERENCES_NAME = "FlutterSharedPreferences";

    private static final String SHARE_PACKAGE_NAME = "com.txkj.notemobile";//"online.xournal.mobile";
    //PreferencesKeys.kRecentFiles
    /*
class PreferencesKeys {
  static const String kRecentFiles = 'recentFiles';
}
     */
//    private static final String KEY_RECENT_FILES = "recentFiles";

    private final static String TAG = "HomeFragment";
    private final static boolean TEST_GRID = false;
	 //FIXME:
    private final static int SINGLE_GRID_DP_WIDTH = 240;//120; //FIXME:动态指定近期PDF文件的格子宽度
    //这个宽度参考pagegridviewitem_library的最大宽度，例如封面的dp宽度（可以稍微设置大一点）

    private List<FileMeta> pageList;
    private GridView recyclerView;
    private LinearLayout llLibrary;
    private ScrollView svOuter;

    private List<FileMeta> recentNoteList;
    private GridView recentNoteView;
    NoteListAdapter recentNoteAdapter;

    //    private LibraryGridAdapter bookGridAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        svOuter = view.findViewById(R.id.svOuter);
        llLibrary = view.findViewById(R.id.llLibrary);
        llLibrary.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (svOuter.getMeasuredHeight() > svOuter.getMeasuredWidth()) {
                    //竖屏
                    llLibrary.setMinimumHeight(svOuter.getMeasuredHeight() / 2);
                    llLibrary.requestLayout();
                } else {
                    llLibrary.setMinimumHeight(svOuter.getMeasuredHeight() / 2);
                    llLibrary.requestLayout();
                }
            }
        });

        pageList = new ArrayList<FileMeta>();
        recyclerView = (GridView) view.findViewById(R.id.bookgridview_home);
        recyclerView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        recyclerView.setBackgroundColor(Color.WHITE);
        recentAdapter = new LibraryListAdapter(this.getContext(), pageList);
        recyclerView.setAdapter(recentAdapter);
        tvEmpty1 = view.findViewById(R.id.tvEmpty1);
        tvEmpty1.setText(STR_LOADING);
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
                    }
                    FileMeta meta = pageList.get(position);
                    File file = new File(meta.getPath());
                    AppData.get().addRecent(new SimpleMeta(meta.getPath(), System.currentTimeMillis()));

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
        refreshGrid(recentAdapter);

        view.findViewById(R.id.imgViewPdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Activity mainActivity = getActivity();
                if (mainActivity != null && mainActivity instanceof MainActivity) {
                    ((MainActivity) mainActivity).jumpViewPdf();
                } else if (mainActivity != null && mainActivity instanceof MainActivity2) {
                    ((MainActivity2) mainActivity).jumpViewPdf();
                }
            }
        });







//        recyclerView = (GridView) view.findViewById(R.id.bookgridview_home);


//        panelRecent = view.findViewById(R.id.panelRecent);
//        recentName = view.findViewById(R.id.recentName);
//
//        onListGrid = (ImageView) view.findViewById(R.id.onListGrid);
//        if (MainTabs2.MOD_VERSION) {
//            //FIXME: recent tab page
//            onListGrid.setVisibility(View.GONE);
//        }
//        onListGrid.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                popupMenu(onListGrid);
//            }
//        });
//FIXME:清空最近
//        TxtUtils.underlineTextView((TextView) view.findViewById(R.id.clearAllRecent)).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                AlertDialogs.showDialog(getActivity(), getString(R.string.do_you_want_to_clear_everything_), getString(R.string.ok), new Runnable() {
//
//                    @Override
//                    public void run() {
//                        clearAllRecent.run();
//
//                    }
//                });
//
//            }
//        });

        recentAdapter = new LibraryListAdapter(this.getContext(), pageList);
//        recentAdapter.tempValue = FileMetaAdapter.TEMP_VALUE_FOLDER_PATH;
        bindAdapter(recentAdapter);
//        bindAuthorsSeriesAdapter(recentAdapter);

//        recentAdapter.setOnDeleteClickListener(onDeleteRecentClick);

//        ImageView imgViewPdf = view.findViewById(R.id.imgViewPdf);
//        imgViewPdf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });


        //笔记历史记录加载
        ImageView ivJumpNote = view.findViewById(R.id.ivJumpNote);
        ivJumpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity mainActivity = getActivity();
                if (mainActivity != null && mainActivity instanceof MainActivity) {
                    ((MainActivity) mainActivity).jumpViewPdf();
                } else if (mainActivity != null && mainActivity instanceof MainActivity2) {
                    ((MainActivity2) mainActivity).jumpViewPdf();
                }
            }
        });
        recentNoteList = new ArrayList<FileMeta>();
        recentNoteView = (GridView) view.findViewById(R.id.notegridview_home);
        recentNoteView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        recentNoteView.setBackgroundColor(Color.WHITE);
        recentNoteAdapter = new NoteListAdapter(this.getContext(), recentNoteList);
        recentNoteView.setAdapter(recentNoteAdapter);
        tvEmpty2 = view.findViewById(R.id.tvEmpty2);
        tvEmpty2.setText(STR_LOADING);
        llEmpty2 = (LinearLayout) view.findViewById(R.id.llEmpty2);
        recentNoteView.setEmptyView(llEmpty2);
        recentNoteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                         intent.setClassName("com.txkj.notemobile2",
                                 "com.txkj.notemobile2.BookListActivity");
                    } else if (false) {
                         intent.setClassName("com.txkj.notemobile",//"online.xournal.mobile",
                                 "com.txkj.notemobile.MainActivity");//"online.xournal.mobile.MainActivity");
                    } else {
                         intent.setClassName("com.txkj.drawingapp",
                                 "com.txkj.notemobile2.BookListActivity");
                    }

                    FileMeta meta = recentNoteList.get(position);
                    String APP_FILE = meta.getPathTxt();
                    intent.putExtra("APP_FILE", APP_FILE);
                    Log.d(TAG, "APP_FILE: " + APP_FILE);

                    //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                     startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        CardView cardView = view.findViewById(R.id.ideasCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        CardView settingsCardView = view.findViewById(R.id.settingsCard);
        settingsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        if (true) { //FIXME:小心，注释此处无法阻止监听器执行populate
            onGridList();
            populate();
        }

//        TintUtil.setBackgroundFillColor(panelRecent, TintUtil.color);
























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



















    //---------------


    public static String INTENT_TINT_CHANGE = "INTENT_TINT_CHANGE";
    protected volatile com.foobnix.pdf.info.view.MyProgressBar MyProgressBar;
//    protected RecyclerView recyclerView;
    Handler handler;
    View adFrame;
    SwipeRefreshLayout swipeRefreshLayout;
    int listHash = 0;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

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

//    public abstract Pair<Integer, Integer> getNameAndIconRes();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        handler = new Handler();
    }

    //@Override
    public void onViewCreated2(View view, Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        //TxtUtils.updateAllLinks(view);

        //FIXME:????
        if (AppState.get().appTheme == AppState.THEME_INK) {
            TxtUtils.setInkTextView(view);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacksAndMessages(null);
    }

    public boolean isBackPressed() {
        return false;
    }

//    public abstract void notifyFragment();
//
//    public abstract void resetFragment();

    public void onDoubleClick(){

    }

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

    public void bindAdapter(LibraryListAdapter searchAdapter) {
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

//    public List<T> prepareDataInBackground() {
//        return null;
//    }
//
//    public void populateDataInUI(List<T> items) {
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(INTENT_TINT_CHANGE));
        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
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
        if (recyclerView != null) {
            try {
                recyclerView.setAdapter(null);
            } catch (Exception e) {
                LOG.e(e);
            }
        }
    }

    public void onTextRecive(String txt) {

    }

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
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                MyProgressBar.setVisibility(View.VISIBLE);
                            }
                        }, 100);
                    }
                }
            });


            List<FileMeta> result_ = null;
            try {
                inProgress = true;
                result_ = prepareDataInBackgroundSync();
            } catch (Throwable eee) {
                eee.printStackTrace();
            } finally {
                inProgress = false;
            }
            final List<FileMeta> result = result_;
            if (isDetached() || Apps.isDestroyedActivity(getActivity())) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAdded()) {
                        if (MyProgressBar != null) {
                            handler.removeCallbacksAndMessages(null);
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

    public void onGridList(int mode, ImageView onGridlList, final LibraryListAdapter searchAdapter, AuthorsAdapter2 authorsAdapter) {
        if (searchAdapter == null) {
            return;
        }
        if (onGridlList != null) {
            PopupHelper.updateGridOrListIcon(onGridlList, mode);
        }
        if (recyclerView != null && searchAdapter != null) {
            recyclerView.setAdapter(searchAdapter);
            refreshGrid(searchAdapter);
        }
    }
    private void refreshGrid(LibraryListAdapter searchAdapter) {
        if (recyclerView != null) {
            int size = 3;
            if (searchAdapter.getCount() > 0) {
                recyclerView.setNumColumns(searchAdapter.getCount());
                size = searchAdapter.getCount();
            } else {
                recyclerView.setNumColumns(3);
            }

            int gridviewWidth = size * Dips.dpToPx(SINGLE_GRID_DP_WIDTH);
            //https://blog.csdn.net/zhuwentao2150/article/details/70211610
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
        }
    }

    public boolean onKeyDown(int keyCode) {
//        if (recyclerView == null) {
//            return false;
//        }
//        View childAt = recyclerView.getChildAt(0);
//        if (childAt == null) {
//            return false;
//        }
//        int size = childAt.getHeight() + childAt.getPaddingTop() + Dips.dpToPx(2);
//
//        if (AppState.get().getNextKeys().contains(keyCode)) {
//            recyclerView.scrollBy(0, size);
//            return true;
//
//        }
//        if (AppState.get().getPrevKeys().contains(keyCode)) {
//            recyclerView.scrollBy(0, size * -1);
//            return true;
//        }
        //FIXME:?????
        return false;
    }














































    //---------------------------------

    //FIXME:???
    public static final Pair<Integer, Integer> PAIR = new Pair<Integer, Integer>(R.string.recent, R.drawable.glyphicons_422_book_library);
    LibraryListAdapter recentAdapter;
//    ImageView onListGrid;
//    View panelRecent;
//    TextView recentName;
    ResultResponse<FileMeta> onDeleteRecentClick = new ResultResponse<FileMeta>() {

        @Override
        public boolean onResultRecive(FileMeta result) {
            result.setIsRecent(false);
            AppDB.get().update(result);

            if (result.getPath().startsWith(CacheZipUtils.CACHE_RECENT.getPath())) {
                new File(result.getPath()).delete();
                LOG.d("Delete cache recent file", result.getPath());
            }


            AppData.get().removeRecent(result);

            populate();


            return false;
        }
    };
    Runnable clearAllRecent = new Runnable() {

        @Override
        public void run() {
            AppDB.get().clearAllRecent();


            CacheZipUtils.removeFiles(CacheZipUtils.CACHE_RECENT.listFiles());

            AppData.get().clearRecents();

            populate();
        }
    };
    int count = 0;

    //@Override
    public Pair<Integer, Integer> getNameAndIconRes() {
        return PAIR;
    }

    //@Override
    public void onTintChanged() {
//        TintUtil.setBackgroundFillColor(panelRecent, TintUtil.color);
    }

    public boolean onBackAction() {
        return false;
    }

    //@Override
    public List<FileMeta> prepareDataInBackground() {
        {
            if (false) {
                Context useCount = null;
                try {
                    // 获取其他程序对应的Context
                    useCount = getActivity().createPackageContext(SHARE_PACKAGE_NAME,
                            Context.CONTEXT_IGNORE_SECURITY);

                    // 使用其他程序的COntext获取对应的SharedPreferences
                    SharedPreferences ps = useCount.getSharedPreferences(SHARED_PREFERENCES_NAME,
                            Context.MODE_WORLD_READABLE);

                    // 读取数据
                    String recentFiles = ps.getString(NoteFragment.KEY_RECENT_FILES, "");
                    Log.e(TAG, "recentFiles: " + recentFiles);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                String recentFiles = "";
                if (USE_EXTERNAL_FILE) {
                    try {
                        String rootPath = null;
                        if (NoteFragment.USE_NEW_NOTE) {
                            rootPath = new File(Environment.getExternalStorageDirectory(), NoteFragment.APPNAME_NEW).toString();
                        } else {
                            rootPath = new File(Environment.getExternalStorageDirectory(), NoteFragment.APPNAME).toString();
                        }
                        boolean kkk = new File(rootPath).mkdirs();
                        if (new File(rootPath, "flutter." + NoteFragment.KEY_RECENT_FILES + ".txt").exists()) {
                            InputStream fis = new FileInputStream(new File(rootPath, "flutter." + NoteFragment.KEY_RECENT_FILES + ".txt"));
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
                Log.e(TAG, "recentFiles: " + recentFiles);

                List<FileMeta> recentNoteList2 = new ArrayList<>();
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
                            //preview字段加上base64头部才能显示出来封面
                            fileMeta.setPath(preview != null ? BaseExtractor.BASE64_PREFIX + preview : null);
                            recentNoteList2.add(fileMeta);
                        }
                    }
                    recentNoteList.clear();
                    if (NoteFragment.USE_NEW_NOTE) {
                        //新版不需要倒序
                        int count_ = 0;
                        for (int i = 0; i < recentNoteList2.size(); ++i) {
                            recentNoteList.add(recentNoteList2.get(i));
                            count_++;
                            if (count_ >= MAX_RECENT) {
                                break;
                            }
                        }
                    } else {
                        //旧版需要倒序
                        int count_ = 0;
                        for (int i = recentNoteList2.size() - 1; i >= 0; --i) {
                            recentNoteList.add(recentNoteList2.get(i));
                            count_++;
                            if (count_ >= MAX_RECENT) {
                                break;
                            }
                        }
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        }

        //FIXME:获取最近的PDF/EPUB列表
        if (false) {
            List<FileMeta> allRecent = AppData.get().getAllRecent(true);
            int oldSize = allRecent.size();
            ExtUtils.removeReadBooks(allRecent);
            count = oldSize - allRecent.size();
            return allRecent;
        } else if (true) {
            List<FileMeta> allRecent = AppData.get().getAllRecent(true);
            count = allRecent.size();
            return allRecent;
        } else {
            List<FileMeta> allRecent = AppDB.get().getRecentDeprecated();
            count = allRecent.size();
            return allRecent;
        }
    }

    //@Override
    public void populateDataInUI(List<FileMeta> items) {
        tvEmpty1.setText(STR_NO_ITEMS);
        tvEmpty2.setText(STR_NO_ITEMS);
        progressLoading1.setVisibility(View.GONE);
        loadingContent1.setVisibility(View.VISIBLE);

        if (recentAdapter != null) {
            recentAdapter.getItemsList().clear();
            if (false) {
                recentAdapter.getItemsList().addAll(items);
            } else {
                List<FileMeta> items2 = new ArrayList<>();
                int count_= 0;
                for (int i = 0; i < items.size(); ++i) {
                    items2.add(items.get(i));
                    count_++;
                    if (count_ >= MAX_RECENT) {
                        break;
                    }
                }
                recentAdapter.getItemsList().addAll(items2);
            }
            if (TEST_GRID) { //测试
                for (int i = 0; i < 10; ++i) {
                    FileMeta m = new FileMeta();
                    m.setPathTxt("page " + i);
                    recentAdapter.getItemsList().add(m);
                }
            }

            recentAdapter.notifyDataSetChanged();
            refreshGrid(recentAdapter);
        }
        //FIXME:count
//        if (AppState.get().isHideReadBook) {
//            recentName.setText(getString(R.string.recent) + " (" + (items.size() + count) + "/" + count + ")");
//        } else {
//            recentName.setText(getString(R.string.recent) + " (" + items.size() + ")");
//        }

        recentNoteAdapter.notifyDataSetChanged();
    }

    public void onGridList() {
        LOG.d("onGridList");
        onGridList(AppState.get().recentMode, null, recentAdapter, null);
    }

    private void popupMenu(final ImageView onGridList) {
        MyPopupMenu p = new MyPopupMenu(getActivity(), onGridList);
        PopupHelper.addPROIcon(p, getActivity());

        List<Integer> names = Arrays.asList(R.string.list, R.string.compact, R.string.grid, R.string.cover);
        final List<Integer> icons = Arrays.asList(R.drawable.my_glyphicons_114_paragraph_justify, R.drawable.my_glyphicons_114_justify_compact, R.drawable.glyphicons_157_thumbnails, R.drawable.glyphicons_158_thumbnails_small);
        final List<Integer> actions = Arrays.asList(AppState.MODE_LIST, AppState.MODE_LIST_COMPACT, AppState.MODE_GRID, AppState.MODE_COVERS);


        p.getMenu().addCheckbox(getString(R.string.hide_read_books), AppState.get().isHideReadBook, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppState.get().isHideReadBook = isChecked;
                TempHolder.listHash++;

                populate();
            }
        });

        for (int i = 0; i < names.size(); i++) {
            final int index = i;
            p.getMenu().add(names.get(i)).setIcon(icons.get(i)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AppState.get().recentMode = actions.get(index);
                    onGridList.setImageResource(icons.get(index));
                    onGridList();
                    return false;
                }
            });
        }


        p.show();
    }

    //@Override
    public void notifyFragment() {
        populate();
    }

    //@Override
    public void resetFragment() {
        onGridList();
        populate();
    }

}