package com.txkj.contentbrowser2.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.txkj.contentbrowser2.R;

public class MainActivityTest1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_test);
        if (getSupportActionBar() != null) {
            this.getSupportActionBar().hide();
        }
    }
}
