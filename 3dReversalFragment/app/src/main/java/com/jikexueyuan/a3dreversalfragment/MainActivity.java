package com.jikexueyuan.a3dreversalfragment;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mFragmentManager = getFragmentManager();
            mFragmentManager.beginTransaction().replace(R.id.container, new MainFragment()).commit();
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
