package com.jikexueyuan.aidlconnecttoother;

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

import com.jikexueyuan.aidlcontroller.ICallback;
import com.jikexueyuan.aidlcontroller.IControlOtherAppService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private Button mButtonBind, mButtonUnbind;
    private TextView mTvShow;
    private boolean mIsBindservice;
    private IControlOtherAppService.Stub mBinder;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();//初始化数据
        setListener();//设置监听器
    }

    /**
     * 初始化数据
     */
    private void initData() {

        mButtonBind = (Button) findViewById(R.id.button_bindService);
        mButtonUnbind = (Button) findViewById(R.id.button_Unbind);
        mTvShow = (TextView) findViewById(R.id.tv_show);

        mIntent = new Intent(MainActivity.this, MyService.class);
        int i = 1;
        mIntent.putExtra("MainActivity", i);

    }

    /**
     * 设置监听器
     */
    private void setListener() {
        mButtonBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsBindservice = bindService(mIntent, MainActivity.this, Context.BIND_AUTO_CREATE);
            }
        });
        mButtonUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBindservice) {
                    unbindService(MainActivity.this);

                    mIsBindservice = false;
                }
            }
        });

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        //获取MyServiceBinder
        try {
            mBinder = (IControlOtherAppService.Stub) service;
            mBinder.registCallBack(new ICallback.Stub(){

                @Override
                public void onTimeCallback(String str) throws RemoteException {

                    handlerSendMes(str);
                }
            });

        System.out.println("服务绑定成功！");
    } catch (Exception e) {
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
            if(mBinder!=null){

                mBinder.unregistCallBack(callBack);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //创建handler
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String str = msg.getData().getString("data");
            mTvShow.setText(str);
            super.handleMessage(msg);
        }
    };

    //初始化CallBack函数
    private ICallback.Stub callBack = new ICallback.Stub() {
        @Override
        public void onTimeCallback(String str) throws RemoteException {

            handlerSendMes(str);
        }
    };


}
