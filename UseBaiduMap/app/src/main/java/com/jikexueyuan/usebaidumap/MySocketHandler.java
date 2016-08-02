package com.jikexueyuan.usebaidumap;

import android.content.Context;
import android.content.Intent;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Liurs on 2016/7/31.
 **/
public class MySocketHandler extends IoHandlerAdapter {

    private Context context;

//    private static ClientHandler samplMinaClientHandler = null;
    /*public static ClientHandler getInstances(Context context) {
        if (null == samplMinaClientHandler) {

            context = context;
            samplMinaClientHandler = new ClientHandler(context);
        }
        return samplMinaClientHandler;
    }     */



    public MySocketHandler(Context context) {
        this.context = context;
//      samplMinaClientHandler = new ClientHandler(context);
    }



    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("sessionOpened");
    }

    public void sessionClosed(IoSession session) {
        System.out.println("sessionClosed");
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        Intent intent = new Intent();
        intent.setAction("com.mina.broadcast");
        intent.putExtra("message", (String)message);
        context.sendBroadcast(intent);
    }

    public void messageSent(IoSession arg0, Object arg1) throws Exception {
        System.out.println("messageSent");
    }


    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {

        super.exceptionCaught(session, cause);

    }

}
