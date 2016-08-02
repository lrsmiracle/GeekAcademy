package com.jikexueyuan.aidlconnecttoother;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.jikexueyuan.aidlcontroller.ICallback;
import com.jikexueyuan.aidlcontroller.IControlOtherAppService;

public class MyService extends Service {

    private boolean flag = true;
    private RemoteCallbackList<ICallback> remoteCallBackList = new RemoteCallbackList<ICallback>();
    private IControlOtherAppService.Stub controlOtherAppService ;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
            controlOtherAppService = new IControlOtherAppService.Stub() {
                @Override
                public void registCallBack(ICallback callBack) throws RemoteException {
                    remoteCallBackList.register(callBack);
                }

                @Override
                public void unregistCallBack(ICallback callBack) throws RemoteException {

                    remoteCallBackList.unregister(callBack);
                }

            };
        return controlOtherAppService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("服务被创建！");
        new Thread(){
            @Override
            public void run() {

                super.run();

                for (int i = 0; flag==true; i++){

                    String data = "服务正在运行,数据为："+i;
                    int count = remoteCallBackList.beginBroadcast();

                    while (count-->0){
                        try {
                            remoteCallBackList.getBroadcastItem(count).onTimeCallback(data);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    remoteCallBackList.finishBroadcast();


                    System.out.println(data);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {

        System.out.println("服务被销毁！");
        flag = false;
        super.onDestroy();
    }

}
