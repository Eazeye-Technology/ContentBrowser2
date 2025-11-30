package com.txkj.contentbrowser;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.foobnix.dao2.FileMeta;
import com.foobnix.model.AppData;
import com.foobnix.model.SimpleMeta;
import com.txkj.contentbrowser2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.dworks.apps.anexplorer.provider.AppsProviderMy;

public class AppsFragment extends Fragment {
    private final static String STR_NO_ITEMS = "Nothing here yet";//"No items.";
    private final static String STR_LOADING = "Loading...";

    private List<FileMeta> pageList;
    private GridView recyclerView;
    private LibraryGridAdapter3 bookGridAdapter;
    private TextView tvEmpty1;
    private LinearLayout llEmpty1;

    private ExecutorService newFixedThreadPool;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        newFixedThreadPool = Executors.newFixedThreadPool(6);

        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        if (false) {
            List<AppsProviderMy.MyResult> results = AppsProviderMy.getUserApps(getContext(), true);
        }

        pageList = new ArrayList<FileMeta>();
//        for (int i = 0; i < 20; ++i) {
//            FileMeta m = new FileMeta();
//            m.setTitle("page " + i);
//            pageList.add(m);
//        }
        recyclerView = (GridView) view.findViewById(R.id.bookgridview);
        recyclerView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //recyclerView.setBackgroundColor(Color.WHITE);
        bookGridAdapter = new LibraryGridAdapter3(this.getContext(), pageList);
        recyclerView.setAdapter(bookGridAdapter);
        tvEmpty1 = view.findViewById(R.id.tvEmpty1);
        tvEmpty1.setText(STR_LOADING);
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

        GetBookListTask task = new GetBookListTask();
        if (android.os.Build.VERSION.SDK_INT < 11) {
            task.execute();
        } else {
            task.executeOnExecutor(newFixedThreadPool);
        }
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public class GetBookListTask extends AsyncTask<Void, Void, Void> {
        List<FileMeta> fileMetas = new ArrayList<>();

        public GetBookListTask() {

        }

        @Override
        protected Void doInBackground(Void... objects) {
            List<AppsProviderMy.MyResult> results = AppsProviderMy.getUserApps(getContext(), true);
            for (AppsProviderMy.MyResult item : results) {
                if (item != null) {
                    FileMeta meta = new FileMeta();
                    meta.setPathTxt(item.displayName);
                    meta.setTitle(item.path);
                    fileMetas.add(meta);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result_) {
            super.onPostExecute(result_);

            pageList.clear();
            pageList.addAll(fileMetas);
            bookGridAdapter.notifyDataSetChanged();
            tvEmpty1.setText(STR_NO_ITEMS);
        }
    }
}