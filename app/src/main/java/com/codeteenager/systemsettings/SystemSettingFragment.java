package com.codeteenager.systemsettings;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.txkj.contentbrowser2.R;

import java.util.ArrayList;
import java.util.List;

public class SystemSettingFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private SettingsAdapter settingsAdapter;
    private List<String> data = new ArrayList<>();
    private List<String> intentData = new ArrayList<>();

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
        data.add("Settings");//"设置");
        intentData.add(Settings.ACTION_SETTINGS);
        data.add("Privacy Settings");//备份和重置");
        intentData.add(Settings.ACTION_PRIVACY_SETTINGS);
        data.add("Accessibility Settings");//辅助功能");
        intentData.add(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        data.add("Device Info Settings");//""关于手机");
        intentData.add(Settings.ACTION_DEVICE_INFO_SETTINGS);
        data.add("WIFI Settings");//"无线设置");
        intentData.add(Settings.ACTION_WIFI_SETTINGS);
        data.add("Application Settings");//""应用管理");
        intentData.add(Settings.ACTION_APPLICATION_SETTINGS);
        data.add("Application Development Settings");//"开发人员选项");
        intentData.add(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        data.add("Input Method Settings");//"语言和输入设备");
        intentData.add(Settings.ACTION_INPUT_METHOD_SETTINGS);
        data.add("Security Settings");//"安全设置");
        intentData.add(Settings.ACTION_SECURITY_SETTINGS);
        data.add("Display Settings");//""手机显示");
        intentData.add(Settings.ACTION_DISPLAY_SETTINGS);
        data.add("Internal Storage Settings");//""内部存储");
        intentData.add(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
        data.add("Memory Card Settings");//"外部存储");
        intentData.add(Settings.ACTION_MEMORY_CARD_SETTINGS);
        data.add("Date Settings");//""日期和时间");
        intentData.add(Settings.ACTION_DATE_SETTINGS);
        data.add("Data Roaming Settings");//""移动网络设置");
        intentData.add(Settings.ACTION_DATA_ROAMING_SETTINGS);
        data.add("Bluetooth Settings");//""蓝牙设置");
        intentData.add(Settings.ACTION_BLUETOOTH_SETTINGS);
        data.add("Location Source Settings");//""位置服务");
        intentData.add(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerview);
        settingsAdapter = new SettingsAdapter(getActivity(), data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(settingsAdapter);
        // 设置Item添加和移除的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置Item之间间隔样式(分割线)
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL));
        settingsAdapter.setItemClickListener(new SettingsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openSystemSetting(intentData.get(position));
            }
        });
    }

    private void openSystemSetting(String intent) {
        Intent mIntent = new Intent(intent);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
    }
}
