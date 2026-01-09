package com.txkj.contentbrowser2.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.txkj.contentbrowser.HomeFragment3;
import com.txkj.contentbrowser2.R;

import io.material.catalog.navigationrail.NavigationRailSubMenuDemoFragment;

public class MainActivityTest2 extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_test2);
        if (getSupportActionBar() != null) {
            this.getSupportActionBar().hide();
        }

        NavigationRailSubMenuDemoFragment mHome3Fragment = new NavigationRailSubMenuDemoFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, mHome3Fragment);
        fragmentTransaction.commit();
    }
}
