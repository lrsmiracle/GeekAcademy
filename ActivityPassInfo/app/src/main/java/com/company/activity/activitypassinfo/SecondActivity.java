package com.company.activity.activitypassinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView mTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mTextView = (TextView) findViewById(R.id.TextView_Second);
        Intent intent = getIntent();
        mTextView.setText(intent.getStringExtra("Message"));

    }
}
