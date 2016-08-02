package com.jikexueyuan.note.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.jikexueyuan.note.activity.EditNoteActivity;
import com.jikexueyuan.note.bean.NoteBean;
import com.jikexueyuan.note.broadcast.BootReceiver;
import com.jikexueyuan.note.contentProvider.MyContentProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyService extends Service {
    private ArrayList<NoteBean> mArrayList;
    private SimpleDateFormat mFormatter;
    private SimpleDateFormat sdf;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return new Binder();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        initData();

    }

    /**
     * init data
     */
    private void initData() {


        //init NoteBean List
        mArrayList = new ArrayList<NoteBean>();
        //get current Time
        mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //init simpleDateFormat
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //get all of data where time > current time
        readData();

        Log.v("service", mArrayList.toString());
    }

    /**
     * Set Alarm
     * @param dataId  Insert process create
     */
    private void setAlarm(int dataId,String time,String context ) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MyService.this, BootReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt("_id", dataId);
        bundle.putString("time",time);
        bundle.putString("context",context);
        intent.putExtra("iBundle",bundle);
        intent.putExtra("Action", EditNoteActivity.SEND_NOTIFICATION);
     //   intent.setAction(EditNoteActivity.SEND_NOTIFICATION);   比较奇怪的事是设置了Action之后就会发生取消去不了的请况，神了！
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyService.this, dataId,
                intent ,PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            alarmManager.set(AlarmManager.RTC_WAKEUP,sdf.parse(time).getTime(),pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    /**
     * read data and set Alarm where send time > current time
     */
    private void readData() {

        Cursor cursor = getContentResolver().query(MyContentProvider.url, new String[]{"time"},
                null,null, "time ASC");
        while (cursor.moveToNext()) {
            try {
                if (mFormatter.parse(cursor.getString(cursor.getColumnIndex("time"))).getTime() > System.currentTimeMillis()) {

                    setAlarm(cursor.getInt(cursor.getColumnIndex("_id")),cursor.getString(cursor.getColumnIndex("time")),
                            cursor.getString(cursor.getColumnIndex("context")) );
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
