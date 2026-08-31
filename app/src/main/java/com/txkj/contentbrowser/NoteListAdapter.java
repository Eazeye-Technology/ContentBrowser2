package com.txkj.contentbrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.BaseExtractor;
import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.LOG;
import com.foobnix.android.utils.TxtUtils;
import com.foobnix.dao2.FileMeta;
import com.foobnix.pdf.info.IMG;
import com.foobnix.ui2.AppDB;
import com.txkj.contentbrowser2.R;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends BaseAdapter {
    private final static int IMAGE_ID = R.id.browserItemIcon_library;
    public final static int IMAGE_WIDTH_DP = 30;

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

    public NoteListAdapter(Context context, List<FileMeta> results) {
        this.context = context;
        this.dataList = results;
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
            convertView = View.inflate(this.context, R.layout.pagegridviewitem_note_list, null);
            gridholder = new GridViewHolder();
            gridholder.tfBookName = (TextView) convertView.findViewById(R.id.bookgrid_name_library);
            gridholder.ivCoverImage = (ImageView) convertView.findViewById(R.id.browserItemIcon_library);
            gridholder.tfBookTime = (TextView) convertView.findViewById(R.id.bookgrid_time_library);
//            gridholder.ivCoverImageBack = (ImageView) convertView.findViewById(R.id.bookgrid_pic_backgroud);
            gridholder.ivBgImage = (ImageView) convertView.findViewById(R.id.browserItemIcon_library_bg);
            convertView.setTag(gridholder);
        } else {
            gridholder = (GridViewHolder) convertView.getTag();
        }

        if (dataList != null) {
            FileMeta fileMeta = dataList.get(position);
            if (fileMeta != null) {
                bindFileMetaView(gridholder, position);
                boolean needRefresh = TxtUtils.isEmpty(fileMeta.getPathTxt());
                int imageSize = 0;
                if (false) {
                    imageSize = IMG.getImageSize();
                } else {
                    String prefix = BaseExtractor.BASE64_PREFIX;
                    imageSize = Dips.dpToPx(IMAGE_WIDTH_DP/*gridholder.ivCoverImage.getMeasuredWidth()*/);
                }
                if (false) {
                    gridholder.ivCoverImage.setImageResource(R.drawable.glyphicons_144_database_search);
                    bindFileMetaView(gridholder, position);
                } else {
                    if (fileMeta.getPath() != null && fileMeta.getPath().endsWith("/cover.png")) {
                        IMG.getCoverPageWithEffect(gridholder.ivBgImage,
                                fileMeta.getPath().replace("/cover.png", "/bg.png"),
                                imageSize, new IMG.ResourceReady() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap) {
                                        try {
                                            bindFileMetaView(gridholder, position);
                                        } catch (Exception e) {
                                            LOG.e(e);
                                        }
                                    }
                                });
                    }
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
        String path = fileMeta.getPathTxt() != null ? fileMeta.getPathTxt() : "";
        if (path.endsWith(".xopp")) {
            path = path.substring(0, path.length() - ".xopp".length());
        }
        if (fileMeta.getTitle() != null) {
            holder.tfBookName.setText(fileMeta.getTitle());
        } else {
            holder.tfBookName.setText(path);
        }
        holder.tfBookTime.setText(fileMeta.getDateTxt() != null ? fileMeta.getDateTxt() : "");
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
        private TextView tfBookTime;
        private ImageView ivCoverImage;
        private ImageView ivCoverImageBack;
        private ImageView ivBgImage;
    }
}
