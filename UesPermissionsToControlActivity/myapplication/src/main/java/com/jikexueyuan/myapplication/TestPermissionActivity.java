package com.jikexueyuan.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TestPermissionActivity extends AppCompatActivity {

    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_permission);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.jikexueyuan.uespermissionstocontrolactivity.PermissionUseActivity");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Log.v("permission","Don't have the permission!");
                    Toast.makeText(TestPermissionActivity.this,"Sorry,You didn't have the Permission!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
