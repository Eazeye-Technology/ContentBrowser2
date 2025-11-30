package com.txkj.contentbrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeteenager.systemsettings.SystemSettingFragment;
import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.LOG;
import com.foobnix.android.utils.TxtUtils;
import com.foobnix.dao2.FileMeta;
import com.foobnix.pdf.info.IMG;
import com.foobnix.ui2.AppDB;
import com.txkj.contentbrowser2.R;

import java.util.ArrayList;
import java.util.List;

public class LibraryGridAdapter4 extends BaseAdapter {
    private final static boolean SHOW_PIC_SUFFIX = true;

    public void clearItems() {
        if (dataList != null) {
            dataList.clear();
        }
    }
    public List<SystemSettingFragment.SettingItem> getItemsList() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        return dataList;
    }

    private Context context;
    private GridViewHolder gridholder;
    private List<SystemSettingFragment.SettingItem> dataList;

    public LibraryGridAdapter4(Context context, List<SystemSettingFragment.SettingItem> results) {
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
            convertView = View.inflate(this.context, R.layout.pagegridviewitem_library4, null);
            gridholder = new GridViewHolder();
            gridholder.tfBookName = (TextView) convertView.findViewById(R.id.bookgrid_name_library);
            gridholder.tfBookName2 = (TextView) convertView.findViewById(R.id.bookgrid_name_library2);
            gridholder.ivCoverImage = (ImageView) convertView.findViewById(R.id.browserItemIcon_library);
            gridholder.tvSuffix = (TextView) convertView.findViewById(R.id.tvSuffix);
//            gridholder.ivCoverImageBack = (ImageView) convertView.findViewById(R.id.bookgrid_pic_backgroud);
            convertView.setTag(gridholder);
        } else {
            gridholder = (GridViewHolder) convertView.getTag();
        }

        if (dataList != null) {
            SystemSettingFragment.SettingItem fileMeta = dataList.get(position);
            if (fileMeta != null) {
                bindFileMetaView(gridholder, position);
            }
        }
        return convertView;
    }

    private SystemSettingFragment.SettingItem bindFileMetaView(final GridViewHolder holder, final int position) {
        if (dataList == null || position >= dataList.size()) {
            return new SystemSettingFragment.SettingItem("", "", "", "", 0);
        }
        final SystemSettingFragment.SettingItem fileMeta = (SystemSettingFragment.SettingItem)dataList.get(position);
        if (fileMeta == null) {
            return new SystemSettingFragment.SettingItem("", "", "", "", 0);
        }

        holder.tvSuffix.setText("");
        String path = fileMeta.dataTitle != null ? fileMeta.dataTitle : "";
        String text1 = fileMeta.title1 != null ? fileMeta.title1 : "";
        String text2 = fileMeta.title2 != null ? fileMeta.title2 : "";

        holder.tfBookName.setText(text1);//path);
        holder.tfBookName2.setText(text2);//path);
        if (fileMeta.drawable != 0) {
            holder.ivCoverImage.setImageResource(fileMeta.drawable);
        }
//      holder.ivCoverImageBack.setImageBitmap(page.getBgThumbnail());
        if (true) { //if (AppState.get().isCropBookCovers) {
            holder.ivCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.ivCoverImage.setScaleType(ImageView.ScaleType.FIT_CENTER); //
        }
        return fileMeta;
    }

    private final static class GridViewHolder {
        private TextView tfBookName, tfBookName2;
        private ImageView ivCoverImage;
        private ImageView ivCoverImageBack;
        private TextView tvSuffix;
    }
}
