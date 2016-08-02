package com.jikexueyuan.appwithhttp.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jikexueyuan.appwithhttp.R;

public class UltraLayoutActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ultralayout_activity_layout);

        if (savedInstanceState == null) {

            mFragmentManager = getFragmentManager();
            mFragmentManager.beginTransaction()
                    .replace(R.id.container,new UtraFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            mFragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
