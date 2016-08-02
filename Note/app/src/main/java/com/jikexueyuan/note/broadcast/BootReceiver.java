package com.jikexueyuan.note.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jikexueyuan.note.activity.EditNoteActivity;
import com.jikexueyuan.note.activity.MainActivity;
import com.jikexueyuan.note.service.MyService;

/**
 * Created by Liurs on 2016/5/25.
 **/
public class BootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null && intent.getStringExtra("Action")!=null) {

            if(intent.getStringExtra("Action").equals(EditNoteActivity.SEND_NOTIFICATION)){

            }
            Bundle bundle = intent.getBundleExtra("iBundle");
            int id = bundle.getInt("_id");
            String _context = bundle.getString("context");
            sendNotification(id, _context, context);
        }else if(intent.getAction() == null && intent.getStringExtra("Action")==null){
            return;
        }else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Intent bootService = new Intent(context, MyService.class);
            context.startService(bootService);
            Log.v("boot", "开机自动服务自动启动.....");
        }
       /* else if(intent.getAction().equals(EditNoteActivity.SEND_NOTIFICATION)){

            Bundle bundle = intent.getBundleExtra("iBundle");
            int id = bundle.getInt("_id");
            String _context = bundle.getString("context");
            sendNotification(id,_context,context);
        }*/

    }

    /**
     * send Notification
     *
     * @param Message Note context : will do
     */
    private void sendNotification(int id, String Message, Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_dialog_email);
        builder.setContentTitle("今天您还有需要做的事情呦！");
        builder.setContentText(Message);
        builder.setWhen(0);
        builder.setAutoCancel(true);
        Intent mIntent = new Intent(context, MainActivity.class);
        PendingIntent pend = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pend);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }
}
