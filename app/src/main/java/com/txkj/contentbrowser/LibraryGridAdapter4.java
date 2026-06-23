package com.txkj.contentbrowser;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeteenager.systemsettings.SystemSettingFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.txkj.contentbrowser2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
                                                                             setAcraEnableFile(APPNAME_NEW, b);
                                                                             setAcraEnableFile(APPNAME_NEW2, b);
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




























    public final static String APPNAME_NEW = "txkjreader";
    public final static String APPNAME_NEW2 = "txkjnote2";
    private final static String PREFNAME = "acrapref.json";
    public boolean getAcraEnableFile(String appName) {
        String prefName = PREFNAME;
        String recentFiles = "";
        try {
            String rootPath = null;
            rootPath = new File(Environment.getExternalStorageDirectory(), appName).toString();
            boolean kkk = new File(rootPath).mkdirs();
            if (new File(rootPath, "" + prefName).exists()) {
                InputStream fis = new FileInputStream(new File(rootPath, "" + prefName));
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
        //Log.e(TAG, "recentFiles: " + recentFiles);
        JSONObject item = new JSONObject();
        try {
            item = new JSONObject(recentFiles);
            return item.optBoolean("acraEnable", false);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return false;
    }

    public void setAcraEnableFile(String appName, boolean acraEnable) {
        String prefName = PREFNAME;
        String recentFiles = "";
        try {
            String rootPath = null;
            rootPath = new File(Environment.getExternalStorageDirectory(), appName).toString();
            boolean kkk = new File(rootPath).mkdirs();
            if (new File(rootPath, "" + prefName).exists()) {
                InputStream fis = new FileInputStream(new File(rootPath, "" + prefName));
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
        //Log.e(TAG, "recentFiles: " + recentFiles);
        JSONObject item = new JSONObject();
        try {
            item = new JSONObject(recentFiles);
            item.put("acraEnable", acraEnable);
        } catch (Throwable eee) {
            eee.printStackTrace();
            if (item != null) {
                try {
                    item.put("acraEnable", acraEnable);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            String rootPath = null;
            rootPath = new File(Environment.getExternalStorageDirectory(), appName).toString();
            FileOutputStream fout = new FileOutputStream(new File(rootPath, "" + prefName));
            OutputStreamWriter osw = new OutputStreamWriter(fout, "UTF-8");
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(item.toString());
            writer.flush();
            writer.close();
            osw.close();
            fout.close();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
}
