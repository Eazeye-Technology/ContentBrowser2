package com.txkj.contentbrowser2.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.dseink.DualScreenConstant;
import com.txkj.contentbrowser2.R;

public class AccountLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);
        gotoMainActivity();
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(this, MainActivity2.class); //MainActivity6
        DualScreenConstant.launchFull(intent);
        if (DualScreenConstant.USE_HOME) {
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        }
        startActivity(intent);
        finish();
    }
}
