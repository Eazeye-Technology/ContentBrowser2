package com.txkj.contentbrowser;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.LOG;
import com.foobnix.android.utils.TxtUtils;
import com.foobnix.dao2.FileMeta;
import com.foobnix.pdf.info.IMG;
import com.foobnix.ui2.AppDB;
import com.txkj.contentbrowser2.R;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryGridAdapter2 extends BaseAdapter {
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged(); //used
    }
    public void clearAllMapCache() {
        if (dataList != null) {
            for (FileMeta fileMeta : dataList) {
                if (fileMeta.getPath() != null && fileMeta.getPath().endsWith("/cover.png")) {
                    String path = fileMeta.getPath().replace("/cover.png", "/bg.png");
                    IMG.clearCoverPage(path);
                }
                if (fileMeta.getPath() != null) {
                    String path = fileMeta.getPath();
                    IMG.clearCoverPage(path);
                }
            }
        }
    }
    public boolean isPause = false;

    private final static boolean FIX_NOTE_DISPLAY = true;
    private final static boolean SHOW_PIC_SUFFIX = true;

    public void clearItems() {
        if (dataList != null) {
            dataList.clear();
        }
    }
    public List<FileMeta> getItemsList() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        return dataList;
    }

    private Context context;
    //private GridViewHolder gridholder;
    private List<FileMeta> dataList;
    DisplayMetrics DM = new DisplayMetrics();

    public LibraryGridAdapter2(Context context, List<FileMeta> results) {
        this.context = context;
        this.dataList = results;

        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(DM);
    }

    @Override
    public int getCount() {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (dataList != null && position >= 0 && position < dataList.size()) {
            return dataList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //@see com.foobnix.ui2.adapter.FileMetaAdapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder gridholder = null;
        if (convertView == null) {
            convertView = View.inflate(this.context, R.layout.pagegridviewitem_library2, null);
            gridholder = new GridViewHolder();
            gridholder.tfBookName = (TextView) convertView.findViewById(R.id.bookgrid_name_library);
            gridholder.ivCoverImage = (ImageView) convertView.findViewById(R.id.browserItemIcon_library);
            gridholder.tvSuffix = (TextView) convertView.findViewById(R.id.tvSuffix);
//            gridholder.ivCoverImageBack = (ImageView) convertView.findViewById(R.id.bookgrid_pic_backgroud);
            gridholder.ivBgImage = (ImageView) convertView.findViewById(R.id.browserItemIcon_library_bg);
            convertView.setTag(gridholder);
        } else {
            gridholder = (GridViewHolder) convertView.getTag();
            gridholder.ivBgImage.setImageBitmap(null);
            gridholder.ivCoverImage.setImageBitmap(null);
        }
//        gridholder.position = position;
//        final GridViewHolder gridholder_ = gridholder;
//        globalHolder.put(position, gridholder);
        if (true) {
            ViewGroup.LayoutParams params2 = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT);
            if (NoteFragment2.USE_COLUMN_NUM) {
                //note
            }
            if (false) {
                if (DM.heightPixels > DM.widthPixels) {
                    params2.height = (int) (DM.heightPixels / 2.8);//3.5);
                } else {
                    params2.height = (int) (DM.widthPixels / 2.0 / 1.6);//2.5);
                }
            } else {
                int notegridadapter2_height = context.getResources().getDimensionPixelSize(R.dimen.notegridadapter2_height);
                if (DM.heightPixels > DM.widthPixels) {
                    params2.height = notegridadapter2_height;//(int)(274 * 1.5); //(int)(DM.heightPixels / 2.8);//3.5);
                } else {
                    params2.height = notegridadapter2_height;//(int)(274 * 1.5); //(int)(DM.widthPixels / 2.0 / 3.5);//2.5);
                }
            }
            convertView.setLayoutParams(params2);
        }

        if (dataList != null && position >= 0 && position < dataList.size()) {
            FileMeta fileMeta = dataList.get(position);
            if (fileMeta != null) {
                if (fileMeta.getPath() != null && fileMeta.getPath().contains(".pdf")) {
//                    System.out.println(".pdf");
                }
                bindFileMetaView(gridholder, position);
                //boolean needRefresh = TxtUtils.isEmpty(fileMeta.getPathTxt());
                int imageSize = 0;
                if (true) {
                    imageSize = IMG.getImageSize();
                } else {
                    imageSize = Dips.dpToPx(gridholder.ivCoverImage.getMeasuredWidth());
                }
                if (false) {
                    gridholder.ivCoverImage.setImageResource(R.drawable.glyphicons_144_database_search);
                    bindFileMetaView(gridholder, position);
                } else {
                    if (fileMeta.getPath() != null && fileMeta.getPath().endsWith("/cover.png")) {
                       String path = fileMeta.getPath().replace("/cover.png", "/bg.png");
                       IMG.getCoverPageWithEffect(gridholder.ivBgImage,
                                   path,
                                   imageSize, new IMG.ResourceReady() {
                                       @Override
                                       public void onResourceReady(Bitmap bitmap) {

                                       }
                                   });
                    }
                    if (true) {
                        //Log.e("LibraryGridAdapter2", "loading page " + position + "-" + this);
                        String path = fileMeta.getPath();
                        IMG.getCoverPageWithEffect(gridholder.ivCoverImage, path, imageSize, new IMG.ResourceReady() {
                            @Override
                            public void onResourceReady(Bitmap bitmap) {

                            }
                        });
                    }
                }
            }
        }
        return convertView;
    }

    private FileMeta bindFileMetaView(final GridViewHolder holder, final int position) {
        if (dataList == null || position >= dataList.size()) {
            return new FileMeta();
        }
        final FileMeta fileMeta = (FileMeta)dataList.get(position);
        if (fileMeta == null) {
            return new FileMeta();
        }
        if (!SHOW_PIC_SUFFIX) {
            holder.tvSuffix.setText("");
            holder.tfBookName.setText(fileMeta.getPathTxt() != null ? fileMeta.getPathTxt() : "");
        } else {
            holder.tvSuffix.setText("");
            String path = fileMeta.getPathTxt() != null ? fileMeta.getPathTxt() : "";
            if (path.toLowerCase().endsWith(".pdf")) {
                path = path.substring(0, path.length() - ".pdf".length());
                holder.tvSuffix.setText("PDF");
            } else if (path.toLowerCase().endsWith(".epub")) {
                path = path.substring(0, path.length() - ".epub".length());
                holder.tvSuffix.setText("EPUB");
            }
            holder.tfBookName.setText(path);
            if (FIX_NOTE_DISPLAY) {
                if (fileMeta.isNote) {
                    holder.tvSuffix.setText("NOTE");
                    if (fileMeta.getTitle() != null) {
                        //LocalTime now1 = LocalTime.now();
                        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
                        //System.out.println("JDK8 API: " + now1.format(formatter));
                        holder.tfBookName.setText(fileMeta.getTitle());// + "?t" + now1.format(formatter));
                    }
                }
            }
        }
//      holder.ivCoverImage.setImageBitmap(page.getThumbnail());
//      holder.ivCoverImageBack.setImageBitmap(page.getBgThumbnail());
        if (true) { //if (AppState.get().isCropBookCovers) {
            holder.ivCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.ivCoverImage.setScaleType(ImageView.ScaleType.FIT_CENTER); //
        }
        return fileMeta;
    }

    private final static class GridViewHolder {
        private TextView tfBookName;
        private ImageView ivCoverImage;
        private ImageView ivCoverImageBack;
        private TextView tvSuffix;
        private ImageView ivBgImage;
        private boolean coverLoaded = false;
        private boolean bgLoaded = false;
//        public int position = -1;
    }
}
