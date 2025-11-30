package com.codeteenager.systemsettings;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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

    private void initData() {
        data.clear();
        data.add(new SettingItem("WIFI Settings", Settings.ACTION_WIFI_SETTINGS,
                "Network & internet", "Wi-Fi", R.drawable.ic_my_setting_001));
        data.add(new SettingItem("Bluetooth Settings", Settings.ACTION_BLUETOOTH_SETTINGS,
                "Connected Devices", "Bluetooth, pairing", R.drawable.ic_my_setting_002));

        data.add(new SettingItem("Application Settings", Settings.ACTION_APPLICATION_SETTINGS,
                "Apps", "Assistant, recent apps, default...", R.drawable.ic_my_setting_003));
        data.add(new SettingItem("Data Roaming Settings", Settings.ACTION_DATA_ROAMING_SETTINGS,
                "Notifications", "Notification history, conversations", R.drawable.ic_my_setting_004));
        //notify

        //battery
        data.add(new SettingItem("Date Settings", Settings.ACTION_DATE_SETTINGS,
                "Battery", "100%", R.drawable.ic_my_setting_005));
        data.add(new SettingItem("Internal Storage Settings", Settings.ACTION_INTERNAL_STORAGE_SETTINGS,
                "Storage", "38% - 4.98 GB free", R.drawable.ic_my_setting_006));

        //sound
        data.add(new SettingItem("Input Method Settings", Settings.ACTION_INPUT_METHOD_SETTINGS,
                "Sound & viration", "Volume, haptics, Do not Disturb", R.drawable.ic_my_setting_007));
        data.add(new SettingItem("Display Settings", Settings.ACTION_DISPLAY_SETTINGS,
                "Display", "Font size, brightness", R.drawable.ic_my_setting_008));

        data.add(new SettingItem("Accessibility Settings", Settings.ACTION_ACCESSIBILITY_SETTINGS,
                "Accessibility", "Display, interaction, audio", R.drawable.ic_my_setting_009));
        data.add(new SettingItem("Security Settings", Settings.ACTION_SECURITY_SETTINGS,
                "Security & privacy", "App security, device lock", R.drawable.ic_my_setting_010));

        //system
        data.add(new SettingItem("Memory Card Settings", Settings.ACTION_MEMORY_CARD_SETTINGS,
                "System", "Languages, gestures, time, backup", R.drawable.ic_my_setting_011));
        data.add(new SettingItem("Device Info Settings", Settings.ACTION_DEVICE_INFO_SETTINGS,
                "About Device", "Paper 2", R.drawable.ic_my_setting_012));

        //not used
        data.add(new SettingItem("Settings", Settings.ACTION_SETTINGS,
                "Settings", "Settings", R.drawable.ic_my_setting_001));
        data.add(new SettingItem("Privacy Settings", Settings.ACTION_PRIVACY_SETTINGS,
                "Privacy Settings", "Privacy Settings", R.drawable.ic_my_setting_001));
        data.add(new SettingItem("Application Development Settings", Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS,
                "Application Development Settings", "Application Development Settings", R.drawable.ic_my_setting_001));
        data.add(new SettingItem("Location Source Settings", Settings.ACTION_LOCATION_SOURCE_SETTINGS,
                "Location Source Settings", "Location Source Settings", R.drawable.ic_my_setting_001));
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerview);
        settingsAdapter = new LibraryGridAdapter4(getActivity(), data);
        // 设置adapter
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
}
