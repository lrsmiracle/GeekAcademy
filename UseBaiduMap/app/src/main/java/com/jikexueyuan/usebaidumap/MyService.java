package com.jikexueyuan.usebaidumap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class MyService extends Service {

    ArrayList<LatLng> mLocationContainer;
    LatLng userLocation;
    boolean threadFlag;
    IoSession ioSession;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationContainer = new ArrayList<LatLng>();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        threadFlag = false;
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        connectSocket();
        return new MyBinder();

    }

    public ArrayList<LatLng> getLocationList() {
        return mLocationContainer;
    }


    public class MyBinder extends Binder {

        public MyService getService() {
            return MyService.this;
        }

        public void sendUserLocation(LatLng Location) {
            userLocation = Location;
        }
    }

    private String[] changeLocationToString(LatLng Location) {
        String[] coordinate = new String[2];
        coordinate[0] = String.valueOf(Location.latitude);
        coordinate[1] = String.valueOf(Location.longitude);
        return coordinate;
    }

    /**
     * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
     * Socket
     */

    private void connectSocket() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                threadFlag = true;
                try {

                    NioSocketConnector connector = new NioSocketConnector();
                    MySocketHandler handler = new MySocketHandler();
                    connector.setHandler(handler);
                    ConnectFuture future = connector.connect(new InetSocketAddress("10.1.232.10",8000));

                    DefaultIoFilterChainBuilder chain = connector.getFilterChain();
                    chain.addLast("codec", new ProtocolCodecFilter(
                            new TextLineCodecFactory()));

                    future.awaitUninterruptibly();

                    if(future.isConnected()){

                        Log.v("connect","sever connect success , port address is 9000.");
                    }else{
                        Log.v("connect","sever connect failed.");
                        return;
                    }

                    while (threadFlag) {
                        if (userLocation != null) {

                            //send user location to server
                            String[] str = changeLocationToString(userLocation);
                            future.getSession().write(str[0] + "&" + str[1]);
                        }

                        Thread.sleep(1000);
                        mLocationContainer.clear();
                    }

                    connector.dispose();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class MySocketHandler extends IoHandlerAdapter {

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            ioSession = session;
            Log.v("connectors","create session success");
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            mLocationContainer .clear();

            String str = (String)message;

            //Change data to coordinate
            mLocationContainer.add(changeStringToLocation(str));

        }
    }

    private LatLng changeStringToLocation(String coordinate) {
        String[] str = coordinate.split("&");

        //便于测试使用的是固定的地理位置 40.107926,经度:116.23421
     //   return new LatLng(Double.valueOf(str[0])+0.1,Double.valueOf(str[0])+0.1);
        return new LatLng(40.107926,116.23421);
    }
}
