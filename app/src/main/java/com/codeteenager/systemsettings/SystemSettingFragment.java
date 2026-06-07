package com.codeteenager.systemsettings;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tvg.AutoWrapViewGroup;
import com.txkj.contentbrowser.LibraryGridAdapter3;
import com.txkj.contentbrowser.LibraryGridAdapter4;
import com.txkj.contentbrowser2.R;

import java.util.ArrayList;
import java.util.List;

public class SystemSettingFragment extends Fragment {
    private GridView mRecyclerView;
    private LibraryGridAdapter4 settingsAdapter;
    public static class SettingItem {
        public String dataTitle;
        public String intentData;
        public String title1, title2;
        public int drawable;

        public SettingItem(String dataTitle, String intentData, String title1, String title2, int drawable) {
            this.dataTitle = dataTitle;
            this.intentData = intentData;
            this.title1 = title1;
            this.title2 = title2;
            this.drawable = drawable;
        }
    }
    private List<SettingItem> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_3, container, false);
        initData();
        initView(view);
        return view;
    }

    //https://blog.csdn.net/yuhui77268769/article/details/128144540
    private void initData() {
        data.clear();
        data.add(new SettingItem("WIFI Settings", Settings.ACTION_WIFI_SETTINGS,
                "Network & internet", "Wi-Fi", R.drawable.ic_my_setting_001));
        data.add(new SettingItem("Bluetooth Settings", Settings.ACTION_BLUETOOTH_SETTINGS,
                "Connected Devices", "Bluetooth, pairing", R.drawable.ic_my_setting_002));

        data.add(new SettingItem("Application Settings", Settings.ACTION_APPLICATION_SETTINGS,
                "Apps", "Assistant, recent apps, default apps", R.drawable.ic_my_setting_003));
        data.add(new SettingItem("Data Roaming Settings", Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS,
                "Notifications", "Notification history, conversations", R.drawable.ic_my_setting_004));

        data.add(new SettingItem("Date Settings", Settings.ACTION_BATTERY_SAVER_SETTINGS,
                "Battery", /*"100%"*/getPowerLevel(getActivity()) + "%", R.drawable.ic_my_setting_005));
        String extStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        //String extStorage = Environment.getRootDirectory().getAbsolutePath();//"/";
        data.add(new SettingItem("Internal Storage Settings", Settings.ACTION_INTERNAL_STORAGE_SETTINGS,
                "Storage", /*"38% - 4.98 GB free"*/getRootSubtitle(extStorage), R.drawable.ic_my_setting_006));

        data.add(new SettingItem("Input Method Settings", Settings.ACTION_SOUND_SETTINGS,
                "Sound & viration", "Volume, haptics, Do not Disturb", R.drawable.ic_my_setting_007));
        data.add(new SettingItem("Display Settings", Settings.ACTION_DISPLAY_SETTINGS,
                "Display", "Font size, brightness", R.drawable.ic_my_setting_008));

        data.add(new SettingItem("Accessibility Settings", Settings.ACTION_ACCESSIBILITY_SETTINGS,
                "Accessibility", "Display, interaction, audio", R.drawable.ic_my_setting_009));
        data.add(new SettingItem("Security Settings", Settings.ACTION_SECURITY_SETTINGS,
                "Security & privacy", "App security, device lock", R.drawable.ic_my_setting_010));

        //https://blog.csdn.net/godcok/article/details/108636231
        data.add(new SettingItem("Device Info Settings", Settings.ACTION_DEVICE_INFO_SETTINGS,
                "About Device", /*"Paper 2"*/"" + Build.MODEL, R.drawable.ic_my_setting_012));

        data.add(new SettingItem("Memory Card Settings", Settings.ACTION_SETTINGS,
                "All Settings", "", R.drawable.ic_outline_settings_24));

        if (false) {
            //not used
            data.add(new SettingItem("Settings", Settings.ACTION_SETTINGS,
                    "Settings", "Settings", R.drawable.ic_my_setting_001));
            data.add(new SettingItem("Privacy Settings", Settings.ACTION_PRIVACY_SETTINGS,
                    "Privacy Settings", "Privacy Settings", R.drawable.ic_my_setting_001));
            data.add(new SettingItem("Application Development Settings", Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS,
                    "Application Development Settings", "Application Development Settings", R.drawable.ic_my_setting_001));
            data.add(new SettingItem("Location Source Settings", Settings.ACTION_LOCATION_SOURCE_SETTINGS,
                    "Location Source Settings", "Location Source Settings", R.drawable.ic_my_setting_001));

            data.add(new SettingItem("Date Settings", Settings.ACTION_DATE_SETTINGS,
                    "Battery", "100%", R.drawable.ic_my_setting_005));
            data.add(new SettingItem("Input Method Settings", Settings.ACTION_INPUT_METHOD_SETTINGS,
                    "Sound & viration", "Volume, haptics, Do not Disturb", R.drawable.ic_my_setting_007));
            data.add(new SettingItem("Memory Card Settings", Settings.ACTION_MEMORY_CARD_SETTINGS,
                    "System", "Languages, gestures, time, backup", R.drawable.ic_my_setting_011));
            data.add(new SettingItem("Data Roaming Settings", Settings.ACTION_DATA_ROAMING_SETTINGS,
                    "Notifications", "Notification history, conversations", R.drawable.ic_my_setting_004));
        }
    }

    private void initView(View view) {
        AutoWrapViewGroup autoWrapViewGroup = (AutoWrapViewGroup) view.findViewById(R.id.autoWrapViewGroup);
        autoWrapViewGroup.output("Settings", ""); //"Settings: "

        mRecyclerView = (GridView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics DM = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(DM);
        if (DM.heightPixels > DM.widthPixels) {
            mRecyclerView.setNumColumns(2);
        } else {
            mRecyclerView.setNumColumns(3);
        }
        settingsAdapter = new LibraryGridAdapter4(getActivity(), data);
        mRecyclerView.setAdapter(settingsAdapter);
        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                openSystemSetting(data.get(position).intentData);
            }
        });
    }

    private void openSystemSetting(String intent) {
        Intent mIntent = new Intent(intent);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
    }

    public static int getPowerLevel(Activity a) {
        try {
            final Intent batteryIntent = a.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            final int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            final int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            return level * 100 / scale;
        } catch (final Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //https://stackoverflow.com/questions/79497730/how-to-get-the-total-internal-storage-size-including-system-usage-in-android-ko
    //https://stackoverflow.com/questions/8133417/android-get-free-size-of-internal-external-memory
    private String getRootSubtitle(String path) {
        StatFs stat = new StatFs(path);
        long total = 0;
        long free = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            total = stat.getBlockCountLong() * stat.getBlockSizeLong();
            free = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        } else {
            total = (long)stat.getBlockCount() * (long)stat.getBlockSize();
            free = (long)stat.getAvailableBlocks() * (long)stat.getBlockSize();
        }
        //return "Free " + formatFileSize(free) + " of " + formatFileSize(total);
        //"38% - 4.98 GB free"
        if (total == 0) {
            return "0% - " + formatFileSize(free) + " free";
        } else {
            return String.format("%.2f", ((double) (total - free) / (double) total * 100)) + "% - " + formatFileSize(free) + " free";
        }
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
}
