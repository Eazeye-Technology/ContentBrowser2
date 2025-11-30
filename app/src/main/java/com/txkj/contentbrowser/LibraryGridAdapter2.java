package com.txkj.contentbrowser;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;
import java.util.List;

public class LibraryGridAdapter2 extends BaseAdapter {
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
    private GridViewHolder gridholder;
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
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //@see com.foobnix.ui2.adapter.FileMetaAdapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(this.context, R.layout.pagegridviewitem_library2, null);
            gridholder = new GridViewHolder();
            gridholder.tfBookName = (TextView) convertView.findViewById(R.id.bookgrid_name_library);
            gridholder.ivCoverImage = (ImageView) convertView.findViewById(R.id.browserItemIcon_library);
            gridholder.tvSuffix = (TextView) convertView.findViewById(R.id.tvSuffix);
//            gridholder.ivCoverImageBack = (ImageView) convertView.findViewById(R.id.bookgrid_pic_backgroud);
            convertView.setTag(gridholder);
        } else {
            gridholder = (GridViewHolder) convertView.getTag();
        }
        if (true) {
            ViewGroup.LayoutParams params2 = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT);
            if (DM.heightPixels > DM.widthPixels) {
                params2.height = (int)(DM.heightPixels / 2.8); //3.5
            } else {
                params2.height = (int)(DM.widthPixels / 2.0); //2.5
            }
            convertView.setLayoutParams(params2);
        }

        if (dataList != null) {
            FileMeta fileMeta = dataList.get(position);
            if (fileMeta != null) {
                bindFileMetaView(gridholder, position);
                boolean needRefresh = TxtUtils.isEmpty(fileMeta.getPathTxt());
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
                    IMG.getCoverPageWithEffect(gridholder.ivCoverImage, fileMeta.getPath(), imageSize, new IMG.ResourceReady() {
                        @Override
                        public void onResourceReady(Bitmap bitmap) {
                            try {
                                if (dataList != null && position < dataList.size() && needRefresh) {
                                    FileMeta it = AppDB.get().load(fileMeta.getPath());
                                    if (it != null) {
                                        dataList.set(position, it);
                                        bindFileMetaView(gridholder, position);
                                    }
                                }
                            } catch (Exception e) {
                                LOG.e(e);
                            }
                        }
                    });
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
    }
}
