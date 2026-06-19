package com.txkj.contentbrowser;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeteenager.systemsettings.SystemSettingFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
            gridholder.switchOpen = (SwitchMaterial) convertView.findViewById(R.id.switchOpen);
            gridholder.switchOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                                 @Override
                                                                 public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                                     gridholder.switchOpen.postDelayed(new Runnable() {
                                                                         @Override
                                                                         public void run() {
                                                                             setAcraEnable(context, b);
                                                                         }
                                                                     }, 500);
                                                                 }
                                                             });
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
        if (text2 != null && text2.length() > 0) {
            holder.tfBookName2.setText(text2);//path);
            holder.tfBookName2.setVisibility(View.VISIBLE);
        } else {
            holder.tfBookName2.setText("");//path);
            holder.tfBookName2.setVisibility(View.GONE);
        }
        if (fileMeta.drawable != 0) {
            holder.ivCoverImage.setImageResource(fileMeta.drawable);
        }
//      holder.ivCoverImageBack.setImageBitmap(page.getBgThumbnail());
        if (true) { //if (AppState.get().isCropBookCovers) {
            holder.ivCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.ivCoverImage.setScaleType(ImageView.ScaleType.FIT_CENTER); //
        }
        if (fileMeta.intentData != null && fileMeta.intentData.equals(SystemSettingFragment.TYPE_BUG)) {
            holder.switchOpen.setVisibility(View.VISIBLE);
            holder.switchOpen.setChecked(getAcraEnable(context));
        } else {
            holder.switchOpen.setVisibility(View.GONE);
        }
        return fileMeta;
    }

    private final static class GridViewHolder {
        private TextView tfBookName, tfBookName2;
        private ImageView ivCoverImage;
        private ImageView ivCoverImageBack;
        private TextView tvSuffix;
        private SwitchMaterial switchOpen;
    }

    public final static String ACRA_PREF_NAME = "ACRAPrefs";
    public final static String ACRA_PREF_ITEM_NAME = "ACRAEnable";
    public static void setAcraEnable(Context context_, boolean acraEnable){
        if (context_ == null) {
            return;
        }
        SharedPreferences.Editor prefs = context_.getSharedPreferences(ACRA_PREF_NAME, Activity.MODE_PRIVATE).edit();
        prefs.putBoolean(ACRA_PREF_ITEM_NAME, acraEnable);
        prefs.apply();
    }
    public static boolean getAcraEnable(Context context_){
        if (context_ == null) {
            return false;
        }
        SharedPreferences prefs = context_.getSharedPreferences(ACRA_PREF_NAME, Activity.MODE_PRIVATE);
        return prefs.getBoolean(ACRA_PREF_ITEM_NAME, false);
    }
}
