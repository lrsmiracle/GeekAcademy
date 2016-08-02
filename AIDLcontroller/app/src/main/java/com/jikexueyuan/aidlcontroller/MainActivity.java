package com.jikexueyuan.aidlcontroller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    private Button mButtonUnbind, mButtonBind;
    private TextView mTVShow;
    private Intent mIntent;
    private boolean isBindSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化View
        initView();
        //设置监听器
        setListener();
        //初始化Intent
        mIntent = new Intent();
        mIntent.setComponent(new ComponentName("com.jikexueyuan.aidlconnecttoother","com.jikexueyuan.aidlconnecttoother.MyService"));
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        mButtonBind.setOnClickListener(this);
        mButtonUnbind.setOnClickListener(this);
    }

    /**
     * 初始化View
     */
    private void initView() {
        mButtonBind = (Button) findViewById(R.id.button_bindService);
        mButtonUnbind = (Button) findViewById(R.id.button_Unbind);
        mTVShow = (TextView) findViewById(R.id.tv_show);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_bindService:
                isBindSuccess = bindService(mIntent,this, Context.BIND_AUTO_CREATE);
                break;
            case R.id.button_Unbind:
                if(isBindSuccess){
                    unbindService(this);
                    isBindSuccess = false;
                }
                break;
        }

    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        binder = IControlOtherAppService.Stub.asInterface(service);
        try {
            binder.registCallBack(callBack);
        } catch (RemoteException e) {
            System.out.println("服务绑定失败！其他程序正在与服务绑定");
            handlerSendMes("其他程序正在与服务绑定");
        }

    }
    /**
     * 发送Handler信息
     */
    private void handlerSendMes(String str) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        unRigistCallBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRigistCallBack();
    }

    private void unRigistCallBack() {
        try {
            if(binder!=null){

                binder.unregistCallBack(callBack);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //定义AIDL
    private IControlOtherAppService binder = null;
    //初始化CallBack函数
    private ICallback.Stub callBack = new ICallback.Stub() {
        @Override
        public void onTimeCallback(String str) throws RemoteException {

            handlerSendMes(str);
        }
    };

    //创建handler
    private Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTVShow.setText(msg.getData().getString("data"));
        }
    };
}
