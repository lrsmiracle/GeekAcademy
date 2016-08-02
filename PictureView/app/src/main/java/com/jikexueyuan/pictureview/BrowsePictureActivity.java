package com.jikexueyuan.pictureview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class BrowsePictureActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpicture);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
        mImageView.setImageURI(uri);

    }
}
