package com.getdirectory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tvg.AutoWrapViewGroup;
import com.txkj.contentbrowser2.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gm.com.dosya.utils.FileTransactions;

//FIXME:changeDir, listView.setOnItemClickListener
//FIXME:listRoots()
//FIXME:listFiles(new File(extStorage));
public class DirectoryFragment2 extends Fragment {
    private final static boolean SHOW_DOUBLE_DOTS = false;
    private final static boolean USE_RECEIVER = false;

    boolean clickMode = true;
    boolean cpy = false;
    boolean paste = false;
    boolean tasi = false;

    private AutoWrapViewGroup autoWrapViewGroup;
    private View fragmentView;
    private boolean receiverRegistered = false;
    private File currentDir;

    private GridView listView;
    private ListAdapter listAdapter;
    private TextView emptyView;
    private LinearLayout loadingContent1;

    private DocumentSelectActivityDelegate delegate;

    private static String title_ = "";
    private ArrayList<ListItem> items = new ArrayList<ListItem>();
    private ArrayList<HistoryEntry> history = new ArrayList<HistoryEntry>();
    private HashMap<String, ListItem> selectedFiles = new HashMap<String, ListItem>();
    private long sizeLimit = 0;//1024 * 1024 * 1024;

    private String[] chhosefileType = {
            ".pdf", ".epub"
    };
    //".doc", ".docx",

    private class HistoryEntry {
        int scrollItem, scrollOffset;
        File dir;
        String title;
    }

    public static abstract interface DocumentSelectActivityDelegate {
        public void didSelectFiles(DirectoryFragment2 activity, ArrayList<String> files);

        public void startDocumentSelectActivity();

        public void updateToolBarName(String name);
    }


    public boolean onBackPressed_() {
        if (history.size() > 0) {
            HistoryEntry he = history.remove(history.size() - 1);
            title_ = he.title;
            updateName(title_);
            if (he.dir != null) {
                listFiles(he.dir);
            } else {
                listRoots();
            }
            listView.setSelectionFromTop(he.scrollItem, he.scrollOffset);
            return false;
        } else {
            return true;
        }
    }

    private void updateName(String title_) {
        if (delegate != null) {
            delegate.updateToolBarName(title_);
        }
    }

    public void onFragmentDestroy() {
        if (USE_RECEIVER) {
            try {
                if (receiverRegistered) {
                    getActivity().unregisterReceiver(receiver);
                }
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        if (currentDir == null) {
                            listRoots();
                        } else {
                            listFiles(currentDir);
                        }
                    } catch (Exception e) {
                        Log.e("tmessages", e.toString());
                    }
                }
            };
            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }
    };

    public void setDelegate(DocumentSelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private class ListItem {
        int icon;
        String title;
        String subtitle = "";
        String ext = "";
        String thumb;
        File file;

        boolean visible = false; //if checkbox is shown
        boolean check = false; //if checkbox is checked
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (USE_RECEIVER) {
            if (!receiverRegistered) {
                receiverRegistered = true;
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
                filter.addAction(Intent.ACTION_MEDIA_CHECKING);
                filter.addAction(Intent.ACTION_MEDIA_EJECT);
                filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
                filter.addAction(Intent.ACTION_MEDIA_NOFS);
                filter.addAction(Intent.ACTION_MEDIA_REMOVED);
                filter.addAction(Intent.ACTION_MEDIA_SHARED);
                filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
                filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
                filter.addDataScheme("file");
                getActivity().registerReceiver(receiver, filter);
            }
        }
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.document_select_layout2,
                    container, false);
            autoWrapViewGroup = (AutoWrapViewGroup) fragmentView.findViewById(R.id.autoWrapViewGroup);
            autoWrapViewGroup.setOnItemClickListener(new AutoWrapViewGroup.OnItemClickListener() {
                @Override
                public void onItemClick(String title, String id) {
                    for (int count = 0; count < items.size(); count++) {
                        items.get(count).visible = false;
                    }
                    clickMode = true;
                    File file = null;
                    //if id==/, show storages instead of root '/' path
                    if (id != null && id.length() > 0 && !id.equals("/")) {
                        file = new File(id);
                    }
                    changeDir(file, title);
                }
            });
            updatePathView();
            listAdapter = new ListAdapter(getActivity());
            emptyView = (TextView) fragmentView.findViewById(R.id.searchEmptyView);
            emptyView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            loadingContent1 = (LinearLayout) fragmentView.findViewById(R.id.loadingContent1);
            listView = (GridView) fragmentView.findViewById(R.id.listView);
            //listView.setEmptyView(emptyView);
            listView.setEmptyView(loadingContent1);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,
                                        int i, long l) {
                    if (i < 0 || i >= items.size()) {
                        return;
                    }
                    if (clickMode) {
                        ListItem item = items.get(i);
                        changeDir(item.file, item.title);
                    } else {
                        if (items.get(i).check) {
                            items.get(i).check = false;
                            counter--;
                        } else {
                            items.get(i).check = true;
                            counter++;
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    counter = 0;
                    for (int count = 0; count < items.size(); count++) {
                        items.get(count).visible = true;
                        cpy = true;
                    }
                    clickMode = false;
                    listAdapter.notifyDataSetChanged();
                    return false;
                }
            });
            if (true) {
                String extStorage = "/";
                ListItem ext;
                try {
                    extStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
                //Internal Storage
                listFiles(new File(extStorage));
            } else {
                listRoots();
            }
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        initMenu();
        return fragmentView;
    }

    public void changeDir(File file, String title) {
        if (file == null) {
            HistoryEntry he = new HistoryEntry();
            he.scrollItem = listView.getFirstVisiblePosition();
            if (listView.getChildCount() > 0 && listView.getChildAt(0) != null) {
                he.scrollOffset = listView.getChildAt(0).getTop();
            } else {
                he.scrollOffset = 0;
            }
            he.dir = currentDir;
            he.title = title_.toString();
            listRoots();
            history.add(he);
            title_ = title;
            updateName(title_);
            listView.setSelection(0);
            updatePathView();
        } else if (file.isDirectory()) {
            HistoryEntry he = new HistoryEntry();
            he.scrollItem = listView.getFirstVisiblePosition();
            if (listView.getChildCount() > 0 && listView.getChildAt(0) != null) {
                he.scrollOffset = listView.getChildAt(0).getTop();
            } else {
                he.scrollOffset = 0;
            }
            he.dir = currentDir;
            he.title = title_.toString();
            updateName(title_);
            if (!listFiles(file)) {
                return;
            }
            history.add(he);
            title_ = title;
            updateName(title_);
            listView.setSelection(0);
            updatePathView();
        } else {
            if (!file.canRead()) {
                showErrorBox("Access Error");//"AccessError");
                return;
            }
            if (sizeLimit != 0) {
                if (file.length() > sizeLimit) {
                    showErrorBox("File Upload Limit");//"FileUploadLimit");
                    return;
                }
            }
            if (file.length() == 0) {
                return;
            }
            boolean isGoodExt = false;
            for (String str : chhosefileType) {
                if (str != null &&
                        file.toString().toLowerCase().endsWith(str)) {
                    isGoodExt = true;
                    break;
                }
            }
            if (isGoodExt) {
                if (delegate != null) {
                    ArrayList<String> files = new ArrayList<String>();
                    files.add(file.getAbsolutePath());
                    delegate.didSelectFiles(DirectoryFragment2.this, files);
                }
            } else {
                //showErrorBox("Choose correct file.");
                showErrorBox("Choose .epub or .pdf file.");
                return;
            }
        }
    }

    private void listRoots() {
        currentDir = null;
        items.clear();
        String extStorage;
        ListItem ext;
        {
            //添加外部存储
            extStorage = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            ext = new ListItem();
            if (Build.VERSION.SDK_INT < 9 || Environment.isExternalStorageRemovable()) {
                ext.title = "SdCard";
            } else {
                ext.title = "Internal Storage";
            }
            ext.icon = Build.VERSION.SDK_INT < 9
                    || Environment.isExternalStorageRemovable() ? R.drawable.ic_external_storage
                    : R.drawable.ic_storage;
            ext.subtitle = getRootSubtitle(extStorage);
            ext.file = Environment.getExternalStorageDirectory();
            items.add(ext);
        }
        {
            //添加外部挂载
            try {
                BufferedReader reader = new BufferedReader(new FileReader(
                        "/proc/mounts"));
                String line;
                HashMap<String, ArrayList<String>> aliases = new HashMap<String, ArrayList<String>>();
                ArrayList<String> result = new ArrayList<String>();
                String extDevice = null;
                while ((line = reader.readLine()) != null) {
                    if ((!line.contains("/mnt") && !line.contains("/storage") && !line
                            .contains("/sdcard"))
                            || line.contains("asec")
                            || line.contains("tmpfs") || line.contains("none")) {
                        continue;
                    }
                    String[] info = line.split(" ");
                    if (!aliases.containsKey(info[0])) {
                        aliases.put(info[0], new ArrayList<String>());
                    }
                    aliases.get(info[0]).add(info[1]);
                    if (info[1].equals(extStorage)) {
                        extDevice = info[0];
                    }
                    result.add(info[1]);
                }
                reader.close();
                if (extDevice != null) {
                    result.removeAll(aliases.get(extDevice));
                    for (String path : result) {
                        try {
                            ListItem item = new ListItem();
                            if (path.toLowerCase().contains("sd")) {
                                ext.title = "SdCard";
                            } else {
                                ext.title = "ExternalStorage";
                            }
                            item.icon = R.drawable.ic_external_storage;
                            item.subtitle = getRootSubtitle(path);
                            item.file = new File(path);
                            items.add(item);
                        } catch (Exception e) {
                            Log.e("tmessages", e.toString());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
        if (false) {
            //添加系统根目录
            ListItem fs = new ListItem();
            fs.title = "/";
            fs.subtitle = "SystemRoot";
            fs.icon = R.drawable.ic_directory;
            fs.file = new File("/");
            items.add(fs);
        }

        // try {
        // File telegramPath = new
        // File(Environment.getExternalStorageDirectory(), "Telegram");
        // if (telegramPath.exists()) {
        // fs = new ListItem();
        // fs.title = "Telegram";
        // fs.subtitle = telegramPath.toString();
        // fs.icon = R.drawable.ic_directory;
        // fs.file = telegramPath;
        // items.add(fs);
        // }
        // } catch (Exception e) {
        // FileLog.e("tmessages", e);
        // }

        // AndroidUtilities.clearDrawableAnimation(listView);
        // scrolling = true;
        listAdapter.notifyDataSetChanged();
    }

    private boolean listFiles(File dir) {
//        if (currentDir == null) {
//            listRoots();
//            return true;
//        }
        if (!dir.canRead()) {
            if (dir.getAbsolutePath().startsWith(
                    Environment.getExternalStorageDirectory().toString())
                    || dir.getAbsolutePath().startsWith("/sdcard")
                    || dir.getAbsolutePath().startsWith("/mnt/sdcard")) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)
                        && !Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED_READ_ONLY)) {
                    currentDir = dir;
                    items.clear();
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_SHARED.equals(state)) {
                        emptyView.setText("UsbActive");
                    } else {
                        emptyView.setText("NotMounted");
                    }
                    clearDrawableAnimation(listView);
                    // scrolling = true;
                    listAdapter.notifyDataSetChanged();
                    return true;
                }
            }
            showErrorBox("Access Error");//"AccessError");
            return false;
        }
        emptyView.setText("NoFiles");
        File[] files = null;
        try {
            files = dir.listFiles();
        } catch (Exception e) {
            showErrorBox(e.getLocalizedMessage());
            return false;
        }
        if (files == null) {
            showErrorBox("Unknown Error");//"UnknownError");
            return false;
        }
        currentDir = dir;
        items.clear();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.isDirectory() != rhs.isDirectory()) {
                    return lhs.isDirectory() ? -1 : 1;
                }
                return lhs.getName().compareToIgnoreCase(rhs.getName());
                /*
                 * long lm = lhs.lastModified(); long rm = lhs.lastModified();
				 * if (lm == rm) { return 0; } else if (lm > rm) { return -1; }
				 * else { return 1; }
				 */
            }
        });
        for (File file : files) {
            if (file.getName().startsWith(".")) {
                continue;
            }
            ListItem item = new ListItem();
            item.title = file.getName();
            item.file = file;
            if (file.isDirectory()) {
                item.icon = R.drawable.ic_directory;
                item.subtitle = "Folder";
            } else {
                String fname = file.getName();
                String[] sp = fname.split("\\.");
                item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                item.subtitle = formatFileSize(file.length());
                fname = fname.toLowerCase();
                if (fname.endsWith(".jpg") || fname.endsWith(".png")
                        || fname.endsWith(".gif") || fname.endsWith(".jpeg")) {
                    item.thumb = file.getAbsolutePath();
                }
            }
            items.add(item);
        }
        if (SHOW_DOUBLE_DOTS) {
            ListItem item = new ListItem();
            item.title = "..";
            item.subtitle = "Folder";
            item.icon = R.drawable.ic_directory;
            item.file = dir.getParentFile(); //null;
            items.add(0, item);
        }
        clearDrawableAnimation(listView);
        // scrolling = true;
        listAdapter.notifyDataSetChanged();
        return true;
    }

    public static String formatFileSize(long size) {
        if (size < 1024) {
            return String.format("%d B", size);
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0f);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / 1024.0f / 1024.0f);
        } else {
            return String.format("%.1f GB", size / 1024.0f / 1024.0f / 1024.0f);
        }
    }

    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT < 21 || view == null) {
            return;
        }
        Drawable drawable = null;
        if (view instanceof ListView) {
            drawable = ((ListView) view).getSelector();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
            }
        } else {
            drawable = view.getBackground();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
                drawable.jumpToCurrentState();
            }
        }
    }

    public void showErrorBox(String error) {
        if (getActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.app_name))
                .setMessage(error).setPositiveButton("OK", null).show();
    }

    private String getRootSubtitle(String path) {
        StatFs stat = new StatFs(path);
        long total = (long) stat.getBlockCount() * (long) stat.getBlockSize();
        long free = (long) stat.getAvailableBlocks()
                * (long) stat.getBlockSize();
        if (total == 0) {
            return "";
        }
        return "Free " + formatFileSize(free) + " of " + formatFileSize(total);
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public int getItemViewType(int pos) {
            return items.get(pos).subtitle.length() > 0 ? 0 : 1;
        }

        private GridViewHolder gridholder;
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                //com.getdirectory.TextDetailDocumentsCell
                convertView = View.inflate(mContext, R.layout.item_text_detail_documents_cell, null);
                gridholder = new GridViewHolder();
                gridholder.textView = (TextView) convertView.findViewById(R.id.textView);
                gridholder.valueTextView = (TextView) convertView.findViewById(R.id.valueTextView);
                gridholder.typeTextView = (TextView) convertView.findViewById(R.id.typeTextView);
                gridholder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                gridholder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

                gridholder.textView.setText("");
                gridholder.valueTextView.setText("");
                gridholder.typeTextView.setText("");

                convertView.setTag(gridholder);
            } else {
                gridholder = (GridViewHolder) convertView.getTag();
            }
            //TextDetailDocumentsCell textDetailCell = (TextDetailDocumentsCell) convertView;
            ListItem item = items.get(position);
            if (item.icon != 0) {
                setTextAndValueAndTypeAndThumb(gridholder, item.title, item.subtitle, null, null, item.icon);
            } else {
                String type = item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4));
                setTextAndValueAndTypeAndThumb(gridholder, item.title, item.subtitle, type, item.thumb, 0);
            }
//             if (item.file != null && actionBar.isActionModeShowed()) {
//             textDetailCell.setChecked(selectedFiles.containsKey(item.file.toString()),
//             !scrolling);
//             } else {
//             textDetailCell.setChecked(false, !scrolling);
//             }
            if (item.visible) {
                gridholder.checkBox.setVisibility(View.VISIBLE);
            } else {
                gridholder.checkBox.setVisibility(View.GONE);
            }
            if (item.check) {
                gridholder.checkBox.setChecked(true);
            } else {
                gridholder.checkBox.setChecked(false);
            }
            return convertView;
        }
    }

    public void finishFragment() {

    }

    private final static class GridViewHolder {
        private TextView textView, valueTextView, typeTextView;
        private ImageView imageView;
        private CheckBox checkBox;
    }

    public void setTextAndValueAndTypeAndThumb(GridViewHolder holder, String text, String value, String type, String thumb, int resId) {
        holder.textView.setText(text);
        holder.valueTextView.setText(value);
        if (type != null) {
            holder.typeTextView.setVisibility(View.VISIBLE);
            holder.typeTextView.setText(type);
        } else {
            holder.typeTextView.setVisibility(View.GONE);
        }
        if (thumb != null || resId != 0) {
            if (thumb != null) {
//                imageView.setImage(thumb, "40_40", null);
            } else  {
                holder.imageView.setImageResource(resId);
            }
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }
    public void setChecked(GridViewHolder holder, boolean checked, boolean animated) {
        if (holder.checkBox.getVisibility() != View.VISIBLE) {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        holder.checkBox.setChecked(checked);
    }

    public void updatePathView() {
        //internal storage
        String extStorage = "/";
        try {
            extStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        if (autoWrapViewGroup != null) {
            if (currentDir == null || currentDir.getAbsolutePath() == null) {
                autoWrapViewGroup.clearViews();
                autoWrapViewGroup.output("Storage: ", extStorage);//"/");
            } else {
                autoWrapViewGroup.clearViews();
                autoWrapViewGroup.output("Storage: ", extStorage);//"/");
                String absPath = currentDir.getAbsolutePath();
                if (absPath != null) {
                    boolean isStartWithExtStorage = false;
                    if (true) {
                        //internal storage
                        try {
                            //String extStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (extStorage != null && absPath.startsWith(extStorage)) {
                                absPath = absPath.substring(extStorage.length());
                                isStartWithExtStorage = true;
                            }
                        } catch (Throwable eee) {
                            eee.printStackTrace();
                        }
                        if (absPath.startsWith("/")) {
                            absPath = absPath.substring(1);
                        }
                    } else {
                        if (absPath.startsWith("/")) {
                            absPath = absPath.substring(1);
                        }
                    }
                    String[] pathStr = absPath.split("/");
                    String strPath = "";
                    if (isStartWithExtStorage) {
                        strPath += extStorage + strPath;
                    }
                    for (int i = 0; i < pathStr.length; ++i) {
                        String str = pathStr[i];
                        strPath += "/" + str;
                        autoWrapViewGroup.output(str, strPath);
                    }
                }
            }
        }
    }
















    public void initMenu() {
        copyList = new ArrayList<File>();
        zipList = new ArrayList<>();
        infoList = new ArrayList<>();
        progress = new ProgressDialog(getActivity());
        newFixedThreadPool = Executors.newFixedThreadPool(6);
    }
    private ExecutorService newFixedThreadPool;
    private ProgressDialog progress;
    public ArrayList<File> copyList;
    private ArrayList<ListItem> zipList;
    private ArrayList<String> infoList;
    int counter = 0;
    public final static String OPERATION_TYPE_PASTE = "paste";
    public final static String OPERATION_TYPE_ZIP = "zip";
    public final static String OPERATION_TYPE_DELETE = "delete";
    String operationType = "";
    MenuItem deletemenu;
    MenuItem pastemenu;
    MenuItem copymenu;
    MenuItem editmenu;
    MenuItem cutmenu;
    MenuItem createmenu;
    MenuItem zipmenu;
    MenuItem infomenu;
    MenuItem cancelmenu;
    public void showPopupMenuDirectoryFragment2(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(android.view.MenuItem item) {
//                return true;
//            }
//        });
        Menu menu = popupMenu.getMenu();
        deletemenu = menu.findItem(R.id.delete);
        pastemenu = menu.findItem(R.id.paste);
        copymenu = menu.findItem(R.id.copy);
        editmenu = menu.findItem(R.id.edit);
        cutmenu = menu.findItem(R.id.cut);
        createmenu = menu.findItem(R.id.create);
        zipmenu = menu.findItem(R.id.zip);
        infomenu = menu.findItem(R.id.info);
        cancelmenu = menu.findItem(R.id.cancelmenu);
        if (counter == 1) {
            editmenu.setVisible(true);
            infomenu.setVisible(true);
        } else {
            editmenu.setVisible(false);
            infomenu.setVisible(false);
        }
        if (copyList.size() > 0) {
            pastemenu.setVisible(true);
        } else {
            pastemenu.setVisible(false);
        }
        if (counter > 0) {
            createmenu.setVisible(false);
            zipmenu.setVisible(true);
        } else {
            createmenu.setVisible(true);
            zipmenu.setVisible(false);
        }

        //debug, hide not used menu
        if (true) {
            editmenu.setVisible(false);
            createmenu.setVisible(false);
            zipmenu.setVisible(false);
            infomenu.setVisible(false);
        }
        cancelmenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                try {
                    listFiles(currentDir);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        copymenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boolean CHANGE_VISIBLE = false;
                try {
                    performCopy();
                    if (cpy && copyList.size() > 0) {
                        if (CHANGE_VISIBLE) {
                            pastemenu.setVisible(true);
                        }
                        for (int count = 0; count < items.size(); count++) {
                            items.get(count).visible = false;
                        }
                        listAdapter.notifyDataSetChanged();
                        clickMode = true;
                        cpy = false;
                        if (CHANGE_VISIBLE) {
                            copymenu.setVisible(false);
                        }
                    }
                    return false;
                } catch (Exception e) {
                    showErrorBox("The copy operation could not be performed");
                    return false;
                }
            }
        });
        cutmenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boolean CHANGE_VISIBLE = false;
                try {
                    performCopy();
                    if (cpy && copyList.size() > 0) {
                        if (CHANGE_VISIBLE) {
                            pastemenu.setVisible(true);
                        }
                        for (int count = 0; count < items.size(); count++) {
                            items.get(count).visible = false;
                        }
                        listAdapter.notifyDataSetChanged();
                        clickMode = true;
                        cpy = false;
                        if (CHANGE_VISIBLE) {
                            copymenu.setVisible(false);
                            cutmenu.setVisible(false);
                        }
                        tasi = true;
                    }
                    return false;
                } catch (Exception e) {
                    showErrorBox("The cut operation could not be completed");
                    return false;
                }
            }
        });
        pastemenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                operationType = OPERATION_TYPE_PASTE;
                AsyncClass task = new AsyncClass();
                if (android.os.Build.VERSION.SDK_INT < 11) {
                    task.execute();
                } else {
                    task.executeOnExecutor(newFixedThreadPool);
                }
                return false;
            }
        });
        infomenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
//                try {
//                    infoliste();
//                    MaterialDialog builder = new MaterialDialog.Builder(getActivity())
//                            .titleColorAttr(android.R.attr.colorAccent)
//                            .itemsColorRes(R.color.md_black_1000)
//                            .title("Information")
//                            .items(infoList)
//                            .itemsCallback(new MaterialDialog.ListCallback() {
//                                @Override
//                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                }
//                            })
//                            .positiveText("OK")
//                            .show();
//                    listFiles(currentDir);
//                    infoList.clear();
//                    click = true;
//                    return false;
//                } catch (Exception e) {
//                    showErrorBox("The operation could not be performed.");
//                    return false;
//                }
                return false;
            }
        });
        deletemenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                try {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                    ad.setTitle("Delete the file");
                    ad.setMessage("Are you sure ?");
                    ad.setCancelable(false);
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            operationType = OPERATION_TYPE_DELETE;
                            AsyncClass task = new AsyncClass();
                            if (android.os.Build.VERSION.SDK_INT < 11) {
                                task.execute();
                            } else {
                                task.executeOnExecutor(newFixedThreadPool);
                            }
                        }
                    });
                    ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                        }
                    });
                    AlertDialog alertDialog = ad.create();
                    alertDialog.show();
                    clickMode = true;
                    return false;
                } catch (Exception e) {
                    showErrorBox("The deletion operation could not be performed.");
                    return false;
                }
            }
        });
        editmenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
//                try {
//                    String isim = null;
//                    for (ListItem gecici : items) {
//                        if (gecici.getCheck()) {
//                            isim = gecici.getTitle();
//                        }
//                    }
//                    MaterialDialog builder = new MaterialDialog.Builder(getActivity())
//                            .title("Add Item")
//                            .widgetColor(getResources().getColor(R.color.colorPrimaryDark))
//                            .inputType(InputType.TYPE_CLASS_TEXT)
//                            .input(null, isim, new MaterialDialog.InputCallback() {
//                                @Override
//                                public void onInput(MaterialDialog dialog, CharSequence input) {
//                                    for (int count = 0; count < items.size(); count++) {
//                                        if (items.get(count).getCheck()) {
//                                            String newname = input.toString();
//                                            renamePath = items.get(count).getThumb();
//                                            File konum = new File(renamePath);
//                                            File yeniisim = new File(konum.getParent(), newname);
//                                            konum.renameTo(yeniisim);
//                                        }
//                                    }
//                                    listFiles(currentDir);
//                                }
//                            })
//                            .negativeText("Cancel")
//                            .show();
//                    click = true;
//                    return false;
//                } catch (Exception e) {
//                    showErrorBox("The operation could not be performed.");
//                    return false;
//                }
                return false;
            }
        });
        zipmenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
//                try {
//                    if (!catagory) {
//                        islem = "zip";
//                        MaterialDialog builder = new MaterialDialog.Builder(getActivity())
//                                .title("Add Item")
//                                .widgetColor(getResources().getColor(R.color.colorPrimaryDark))
//                                .inputType(InputType.TYPE_CLASS_TEXT)
//                                .input(null, null, new MaterialDialog.InputCallback() {
//                                    @Override
//                                    public void onInput(MaterialDialog dialog, CharSequence input) {
//                                        String anewzipfolder = input.toString();
//                AsyncClass task = new AsyncClass();
//                if (android.os.Build.VERSION.SDK_INT < 11) {
//                    task.execute();
//                } else {
//                    task.executeOnExecutor(newFixedThreadPool);
//                }
//                                        newzipfolder = anewzipfolder;
//                                    }
//                                })
//                                .negativeText("Cancel")
//                                .show();
//                        click = true;
//                    } else {
//                        showErrorBox("You cannot zip here");
//                    }
//                } catch (Exception e) {
//                    showErrorBox("The compression process could not be performed.");
//                }
                return false;
            }
        });
        createmenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
//                try {
//                    if (!catagory) {
//                        MaterialDialog builder = new MaterialDialog.Builder(getActivity())
//                                .title("Add Item")
//                                .widgetColor(getResources().getColor(R.color.colorPrimaryDark))
//                                .inputType(InputType.TYPE_CLASS_TEXT)
//                                .input(null, null, new MaterialDialog.InputCallback() {
//                                    @Override
//                                    public void onInput(MaterialDialog dialog, CharSequence input) {
//                                        String newfolder = input.toString();
//                                        File folder = new File(currentDir.getAbsoluteFile() +
//                                                File.separator + newfolder);
//                                        boolean success = true;
//                                        if (!folder.exists()) {
//                                            success = folder.mkdir();
//                                        }
//                                        if (success) {
//                                            // Do something on success
//                                        } else {
//                                            // Do something else on failure
//                                        }
//                                        listFiles(currentDir);
//                                    }
//                                })
//                                .negativeText("Cancel")
//                                .show();
//                        click = true;
//                    } else {
//                        showErrorBox("You cannot create a folder here");
//                    }
//                    return false;
//                } catch (Exception e) {
//                    showErrorBox("The operation could not be performed");
//                    return false;
//                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void performCopy() {
        if (cpy) {
            for (int count = 0; count < items.size(); count++) {
                if (items.get(count).check) {
                    copyList.add(items.get(count).file);
                }
            }
            if (copyList.size() > 0) {

            } else {
                showErrorBox("Please select the file or folder to be copied.");
            }
        } else {
            cpy = true;
            clickMode = false;
            for (int count = 0; count < items.size(); count++) {
                items.get(count).visible = true;
                cpy = true;
            }
            listAdapter.notifyDataSetChanged();
        }
    }
    private void moveFiles() {
        String path = currentDir.getAbsolutePath();
        for (int count = 0; count < copyList.size(); count++) {
            if (copyList.get(count).getParent().equals(path)) {
                continue;
            }
            FileTransactions.moveFileOrDirectory(copyList.get(count), path);
            if (copyList.get(count).isDirectory()) {
                FileTransactions.DeleteRecursive(copyList.get(count));
            } else {
                copyFiles();
                for (count = 0; count < copyList.size(); count++) {
                    File moving = new File(String.valueOf(copyList.get(count)));
                    FileTransactions.DeleteRecursive(moving);
                }
            }
        }
    }
    private void copyFiles() {
        try {
            String path = currentDir.getAbsolutePath();
            FileTransactions tran = new FileTransactions();
            paste = true;
            for (int count = 0; count < copyList.size(); count++) {
                if (copyList.get(count).getParent().equals(path)) {
                    continue;
                }
                tran.copyFileOrDirectory(copyList.get(count), path);
            }
        } catch (Exception e) {
            showErrorBox("The paste process could not be performed.");
        }
    }
    public class AsyncClass extends AsyncTask<String, String, String> {
        private boolean mIsError = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Processing...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (operationType == null) {
                return null;
            }
            if (operationType.equals(OPERATION_TYPE_PASTE)) {
                if (tasi) {
                    moveFiles();
                } else {
                    copyFiles();
                }
            } else if (operationType.equals(OPERATION_TYPE_ZIP)) {
//                ziple();
            } else if (operationType.equals(OPERATION_TYPE_DELETE)) {
                try {
                    for (int count = 0; count < items.size(); count++) {
                        if (items.get(count) != null && items.get(count).check) {
                            //File cont = new File(items.get(count).thumb);
                            File cont = items.get(count).file;
                            if (cont != null) {
                                FileTransactions.DeleteRecursive(cont);
                            }
                        }
                        if (items.get(count) != null) {
                            items.get(count).visible = false;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    mIsError = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (operationType == null) {
                return;
            }
            if (operationType.equals(OPERATION_TYPE_PASTE)) {
                if (tasi) {
                    pastemenu.setVisible(false);
                    copymenu.setVisible(true);
                    cutmenu.setVisible(true);
                    tasi = false;
                    copyList.clear();
                    listFiles(currentDir);
                } else {
                    pastemenu.setVisible(false);
                    copymenu.setVisible(true);
                    copyList.clear();
                    listFiles(currentDir);
                }
            } else if (operationType.equals(OPERATION_TYPE_ZIP)) {
                listFiles(currentDir);
            } else if (operationType.equals(OPERATION_TYPE_DELETE)) {
                listFiles(currentDir);
            }
            progress.dismiss();
            if (mIsError) {
                showErrorBox("The deletion operation could not be performed");
            }
        }
    }
}
