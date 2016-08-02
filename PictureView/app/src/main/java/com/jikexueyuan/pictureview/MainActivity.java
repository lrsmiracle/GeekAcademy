package com.jikexueyuan.pictureview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private String mPath;
    private Bitmap mBitmap;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init data
        init();
        //save picture
        savePicture();
        //set Listener
        setListener();
    }

    /**
     * save picture
     */
    private void savePicture() {
        saveMyBitmap("girl.jpg", mPath, mBitmap);
        mUri = Uri.parse("file://" + mPath + "/girl.jpg");
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mUri));
    }

    /**
     * set Listener
     */
    private void setListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(mUri, "image/*");
                startActivity(intent);
            }
        });
    }

    /**
     * init data
     */
    private void init() {
        mButton = (Button) findViewById(R.id.button);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    }

    /**
     * save bitmap
     *
     * @param bitName filename include file suffix
     * @param mBitmap need to save
     */
    public void saveMyBitmap(String bitName, String path, Bitmap mBitmap) {
        File file = new File(path + "/" + bitName);
        FileOutputStream fOut = null;
        try {
            if (file.exists()) {
                return;
            } else {
                file.createNewFile();
            }
        } catch (IOException e) {
            Log.v("saveImage", "在保存图片时出错：" + e.toString());
        }
        try {
            fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
