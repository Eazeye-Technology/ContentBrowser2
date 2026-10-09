package com.txkj.contentbrowser2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.dseink.DualScreenConstant;
import com.txkj.contentbrowser2.R;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);
        Log.e("HomeActivity", "HomeActivity==============================================");
        gotoMainActivity();
    }

    private void gotoMainActivity(){
        Intent intent = null;
        if (false) {
            intent = new Intent(this, MainActivity7.class);
        } else if (true) {
            intent = new Intent(this, MainActivity6.class);
        } else {
            intent = new Intent(this, MainActivity2.class);
        }
        DualScreenConstant.launchFull(intent, false, true);
        if (DualScreenConstant.USE_HOME) {
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        }
        startActivity(intent);
        finish();
    }
}
