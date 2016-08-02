package com.jikexueyuan.usebaidumap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public MapView mMapView = null;
    public BaiduMap baiduMap = null;
    //start service
    private Button mStartServiceButton;
    //User head container
    private LinearLayout mHeadContainer;
    private MyService myService;
    public LocationClient mLocationClient = null;
    private ServiceConnection conn;
    private boolean serviceFlag = false;
    //Location latitude and longitude
    private LatLng ll;
    //change ui thread handler
    private Handler mHandler;
    // 是否首次定位
    boolean isFirstLoc = true;
    //Head view container
    private ArrayList<View> mHeaderList;
    //Marker container
    private ArrayList<OverlayOptions> mMarkerContainer;

    //flag if share thelocation
    private boolean isShareLocation = true;
    //Latitude container
    private ArrayList<LatLng> mLatitudeContainer;

    public BDLocationListener myListener = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * Service binder
     */
    private MyService.MyBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //初始化数据
        initData();
        // 开始定位
        mLocationClient.start();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //set head marker1
        generateMarker();
    }

    /**
     * Get the bitmap used by Marker to show the location
     *
     * @param addViewContent
     * @return
     */
    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }


    /**
     * Set head Marker
     */
    public void generateMarker() {


        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                boolean flag = true;
                while (flag) {
                    baiduMap.clear();

                    for (OverlayOptions marker : mMarkerContainer) {
                        baiduMap.addOverlay(marker);
                    }
                    mMarkerContainer.clear();
                    try {
                        Thread.sleep(1000);
                        if (ll != null) {

                            mMarkerContainer.add(creatMarker(ll,R.mipmap.head1,false));
                            if(myService!=null){

                                for(LatLng loction:myService.getLocationList()){
                                    mMarkerContainer.add(creatMarker(loction,R.mipmap.head2,true));
                                }
                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putInt("size",mMarkerContainer.size());
                                msg.setData(bundle);
                                //Send message to handler
                                mHandler.sendMessage(msg);
                                mBinder.sendUserLocation(ll);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        }.execute();
    }

    /**
     * Create marker
     */
    private OverlayOptions creatMarker(LatLng ll,int mipmapid,boolean needdot) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.marker_layout, null);
        if(needdot){

            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.adddot);
            ImageView dot = new ImageView(MainActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(25, 25);
            dot.setLayoutParams(layoutParams);
            dot.setBackgroundResource(R.mipmap.dot2);
            linearLayout.addView(dot);
        }
        ImageView head = (ImageView) view.findViewById(R.id.header);
        head.setBackgroundResource(mipmapid);

        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(getViewBitmap(view));

        OverlayOptions oo = new MarkerOptions().position(ll).icon(markerIcon).zIndex(9).draggable(true);
        return oo;
    }

    /**
     * 初始化数据
     */
    private void initData() {

        //create connection
        createConnection();

        mStartServiceButton = (Button) findViewById(R.id.shareLocation);
        mHeadContainer = (LinearLayout) findViewById(R.id.userContainer);

        mHandler = new MyHandler();
        setListener();
        //Create container
        mMarkerContainer = new ArrayList<OverlayOptions>();
        mLatitudeContainer = new ArrayList<LatLng>();
        mHeaderList = new ArrayList<View>();

        myListener = new MyLocationListener();
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        //开启定位图层
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类
        mLocationClient.registerLocationListener(myListener); // 注册监听函数

        //设置定位参数包括：定位模式
        initLocation();
    }

    /**
     * Set listener
     */
    private void setListener() {
        mStartServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serviceFlag==false){
                    //bind service
                    bindService(new Intent(MainActivity.this, MyService.class), conn, Context.BIND_AUTO_CREATE);
                    serviceFlag = true;
                }else{
                    unbindService(conn);
                    myService = null;
                    serviceFlag=false;
                    isShareLocation = true;
                    for(int i=0;i<mHeaderList.size();i++){
                        mHeadContainer.removeView(mHeaderList.get(i));
                    }
                }
            }
        });
    }

    /**
     * Create connection
     */
    private void createConnection() {
        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                myService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //返回一个MyService对象
                mBinder = (MyService.MyBinder) service;
                myService = (mBinder).getService();

            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.jikexueyuan.usebaidumap/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.jikexueyuan.usebaidumap/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * 设置监听器
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);    //设置定位数据


            if (isFirstLoc) {
                isFirstLoc = false;

                ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);    //设置地图中心点以及缩放级别
                baiduMap.animateMapStatus(u);
                mMarkerContainer.add(creatMarker(ll,R.mipmap.head1,false));
            } else {

                ll = new LatLng(location.getLatitude(), location.getLongitude());

            }
        }

    }

    @Override
    protected void onDestroy() {
        //退出时销毁定位
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
        //
        if(myService!=null){

            unbindService(conn);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    //设置定位参数包括：定位模式（高精度定位模式，低功耗定位模式和仅用设备定位模式），返回坐标类型，是否打开GPS，是否返回地址信息、位置语义化信息、POI信息等等。
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

//            for(int i=0;i<msg.getData().getInt("size");i++){
                if(isShareLocation){

                    ImageView iv= new ImageView(MainActivity.this);
                    iv.setMaxWidth(100);
                    iv.setMaxHeight(100);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
                    layoutParams.setMargins(20,8,0,0);
                    iv.setLayoutParams(layoutParams);
                    iv.setBackgroundResource(R.mipmap.head2);
                    mHeadContainer.addView(iv);
                    mHeaderList.add(iv);
                    isShareLocation = false;
                }
 //           }

        }
    }
}
