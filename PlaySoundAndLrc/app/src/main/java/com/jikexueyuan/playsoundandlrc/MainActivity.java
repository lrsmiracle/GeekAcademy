package com.jikexueyuan.playsoundandlrc;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonRun;
    private TextView mTextViewShowLRC;
    private MediaPlayer mMediaPlayer;
    /**
     * InputStreams used to get lyrics
     */
    private InputStreamReader isr;
    private InputStream is;
    private BufferedReader br;
    /**
     * The time during play
     */
    private int mMusicTime;

    /**
     * Lyrics List
     */
    private ArrayList<SongModel> mLyricsList;
    /**
     * Lyrics List index
     */
    private int index = 1;

    private boolean mflag , mflag2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //InitData
        initData();
        //Extract Lrc document
        extractLRC();
    }

    /**
     * Extract Lrc document
     */
    private void extractLRC() {

        is = getResources().openRawResource(R.raw.daoxiang_lrc);
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        mLyricsList = new ArrayList<SongModel>();
        mflag = true;

        //Get Lyrics
        getLyrics();
    }

    /**
     * Get Lyrics
     */
    private void getLyrics() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    while (mflag) {
                        String str = br.readLine();
                        if (!TextUtils.isEmpty(str)) {
                            String[] strsplit = str.split("]");
                            String temp = strsplit[0].substring(1);
                            if (temp.contains(".")) {
                                mLyricsList.add(new SongModel(temp, strsplit[1]));
                            }
                        } else {
                            mflag = false;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    br.close();
                    isr.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Init data
     */
    private void initData() {
        mButtonRun = (Button) findViewById(R.id.StartPlay);

        mTextViewShowLRC = (TextView) findViewById(R.id.ShowLRCText);

        mButtonRun.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.StartPlay:
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.daoxiang);
                    mMediaPlayer.start();
                    mflag2 = true;
                    mMusicTime = mLyricsList.get(index).getPlaytime();

                    //Create a new thread to compare playtime and lyrics
                    startThread();

                }
                break;
        }
    }

    /**
     * Create a new thread to compare playtime and lyrics
     */
    private void startThread() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (mflag2) {
                    try {
                        if (mMediaPlayer != null) {

                            System.out.println("run");
                            if (mMusicTime > mMediaPlayer.getCurrentPosition() - 10 &&
                                    mMusicTime < mMediaPlayer.getCurrentPosition() + 10) {

                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("Lyrics", mLyricsList.get(index).getLyrics());
                                message.setData(bundle);
                                handler.sendMessage(message);
                                if (index < mLyricsList.size() - 1) {

                                    index++;
                                }
                                mMusicTime = mLyricsList.get(index).getPlaytime();
                            }
                        }
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mMediaPlayer!=null){

            mMediaPlayer.stop();
        }
        mflag2 = false;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg != null) {

                String str = msg.getData().getString("Lyrics");
                mTextViewShowLRC.setText(str);
            }
        }
    };
}
