package com.txkj.contentbrowser2.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.txkj.contentbrowser2.R;

public class AccountLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);
        gotoMainActivity();
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra(DualScreenConstant.EXTRA_LAUNCH_SCREEN,
                DualScreenConstant.EXTRA_LAUNCH_SCREEN_PANEL_BOTH);
        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }
}
