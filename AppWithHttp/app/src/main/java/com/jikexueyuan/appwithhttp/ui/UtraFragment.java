package com.jikexueyuan.appwithhttp.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jikexueyuan.appwithhttp.R;
import com.jikexueyuan.appwithhttp.base.MyListView;
import com.jikexueyuan.appwithhttp.contentprovider.MyContentProvider;
import com.jikexueyuan.appwithhttp.model.ListContentModel;
import com.jikexueyuan.appwithhttp.utils.LocalDisplay;
import com.jikexueyuan.appwithhttp.utils.NetConnectionUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * Show ultraLayout
 */
public class UtraFragment extends Fragment implements MyListView.MyPullUpListViewCallBack {

    private MyListView mListView;

    //url
    private String mHttpUrl = "http://1.appwithhttp.applinzi.com/getLastestEssay.php";

    //All of ListView content data
    private ArrayList<ListContentModel> mContentList;

    //Current ListView content data
    private ArrayList<ListContentModel> mContentListCurrentDatas;

    //listView adapter
    private MyListViewAdapter myListViewAdapter;

    //Current ListNumber
    private int mListNum;

    //Flag present all of data was
    private boolean mFlag, mFlagAdding,isAdding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout view_linearLayout = (LinearLayout) View.inflate(getActivity(), R.layout.ultra_layout, null);

        //Init data
        initData(view_linearLayout);

        //Use ptrFrameLayout to show pull down refreshing
        setPullDownView(view_linearLayout);

        //Create the list view adapter
        myListViewAdapter = new MyListViewAdapter();
        mListView.setAdapter(myListViewAdapter);

        return view_linearLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        myListViewAdapter.notifyDataSetChanged();
        //Reset current list number
        mListNum = 0;
        mFlag = false;

        //Get data from server if device is connecting with internet
        if (NetConnectionUtils.isNetworkConnected(getActivity())) {

            getDataFromServer();
        } else {

            //Get and init lisView from database
            initNativeListViewData();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isAdding==false){

                    Bundle bundle = new Bundle();
                    bundle.putString("Title", mContentListCurrentDatas.get(position).getTitle());
                    bundle.putString("Time", mContentListCurrentDatas.get(position).getTime());
                    bundle.putString("Content", mContentListCurrentDatas.get(position).getContent());

                    ListViewContextFragment listViewContextFragment = new ListViewContextFragment();
                    listViewContextFragment.setArguments(bundle);

                    getFragmentManager().beginTransaction()
                            .addToBackStack("utraFragment")
                            .setCustomAnimations(R.animator.fragment_3d_reversal_enter, R.animator.fragment_3d_reversal_exit, R.animator.fragment_second_3d_reversal_enter, R.animator.fragment_second_3d_reversal_exit)
                            .replace(R.id.container, listViewContextFragment)
                            .commit();
                }
            }
        });

    }

    /**
     * Use ptrFrameLayout to show pull down refreshing
     *
     * @param view_linearLayout
     */
    private void setPullDownView(LinearLayout view_linearLayout) {
        final PtrFrameLayout ptrFrameLayout = (PtrFrameLayout) view_linearLayout.findViewById(R.id.store_house_ptr_frame);
        View view = (View) ptrFrameLayout.getParent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(getResources().getColor(R.color.PtrFrameLayout_color, null));
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.PtrFrameLayout_color));
        }
        StoreHouseHeader header = new StoreHouseHeader(getActivity());
        header.setPadding(0, LocalDisplay.dp2px(getActivity(), 20), 0, LocalDisplay.dp2px(getActivity(), 20));
        header.initWithString("UPDATING");

        ptrFrameLayout.setDurationToCloseHeader(1500);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        ptrFrameLayout.setPullToRefresh(false);
        ptrFrameLayout.setKeepHeaderWhenRefresh(true);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrameLayout.refreshComplete();
                    }
                }, 1500);

                if (NetConnectionUtils.isNetworkConnected(getActivity())) {

                    //Get data from server
                    getDataFromServer();
                } else {
                    Toast.makeText(getActivity(), "网络访问失败，请检查网络设置！", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    /**
     * Get data from server
     */
    private void getDataFromServer() {

        mContentListCurrentDatas.clear();
        myListViewAdapter.notifyDataSetChanged();
        //Start thread to get data
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {

                try {
                    getData(params);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                myListViewAdapter.notifyDataSetChanged();
                saveDataToDataBase(mContentList);
            }
        }.execute(mHttpUrl);
    }

    /**
     * Get data
     *
     * @param params
     * @throws IOException
     */
    private void getData(String[] params) throws IOException {
        URL url = new URL(params[0]);
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr);

        //get io data
        String data = "";
        String temp;
        while ((temp = br.readLine()) != null && temp.length() != 0) {
            data += temp;
        }
        Log.v("test", data);
        try {
            //Translate http data to jsonArray
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {

                mContentList.add(new ListContentModel(jsonArray.getJSONObject(i).getInt("ID"),
                        jsonArray.getJSONObject(i).getString("post_content"),
                        jsonArray.getJSONObject(i).getString("post_date"),
                        jsonArray.getJSONObject(i).getString("post_title")
                ));
            }
            addCurrentListCollection();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //close streams
        br.close();
        isr.close();
        is.close();
    }

    /**
     * save data to dataBase from web server
     */
    private void saveDataToDataBase(final ArrayList<ListContentModel> mContentList) {

        new AsyncTask<ArrayList<ListContentModel>, Void, Void>() {

            @Override
            protected Void doInBackground(ArrayList<ListContentModel>... params) {

                ArrayList<ListContentModel> AL = new ArrayList<ListContentModel>(params[0]);
                Cursor c = getActivity().getContentResolver().query(MyContentProvider.url, new String[]{"_id"}, null, null, null);
                while (c.moveToNext()) {

                    int j = c.getInt(c.getColumnIndex("_id"));

                    for (int i = 0; i < AL.size(); i++) {
                        if (j == AL.get(i).getId()) {
                            AL.remove(i);
                        }
                    }
                }

                for (int i = 0; i < AL.size(); i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_id", AL.get(i).getId());
                    contentValues.put("title", AL.get(i).getTitle());
                    contentValues.put("time", AL.get(i).getTime());
                    contentValues.put("content", AL.get(i).getContent());

                    getActivity().getContentResolver().insert(MyContentProvider.url, contentValues);
                }

                return null;
            }
        }.execute(mContentList);
    }

    /**
     * Init data
     *
     * @param view_linearLayout
     */
    private void initData(LinearLayout view_linearLayout) {
        mContentList = new ArrayList<ListContentModel>();
        mContentListCurrentDatas = new ArrayList<ListContentModel>();

        mListView = (MyListView) view_linearLayout.findViewById(R.id.ListView);

        mListView.setMyPullUpListViewCallBack(this);

    }

    /**
     * Get and init lisView
     */
    private void initNativeListViewData() {

        mFlag = false;
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                Cursor c = getActivity().getContentResolver().query(MyContentProvider.url, new String[]{"_id", "time", "title", "content"}, null, null, null);
                while (c.moveToNext()) {

                    ListContentModel model = new ListContentModel();
                    model.setTime(c.getString(c.getColumnIndex("time")));
                    model.setContent(c.getString(c.getColumnIndex("content")));
                    model.setId(c.getInt(c.getColumnIndex("_id")));
                    model.setTitle(c.getString(c.getColumnIndex("title")));
                    mContentList.add(model);

                }
                if (c.getCount() != 0) {

                    //Add data to list view collection from main sever data
                    addCurrentListCollection();
                }
                c.close();
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mListView.setTextView("Loading...");
            }
        }.execute("");
    }

    /**
     * Add 15 data to list view collection from main collection
     */
    private void addCurrentListCollection() {

        for (int i = 0; i < 15; i++) {

            mContentListCurrentDatas.add(mContentList.get(i));
        }
        mListNum = 15;
    }

    @Override
    public void scrollBottomState() {

        if (mFlagAdding == false) {
            //Updating data pause method
            mFlagAdding = true;
            isAdding = true;
            new AsyncTask<String, Void, Void>() {

                private ArrayList<ListContentModel> tempList = new ArrayList<ListContentModel>();
                @Override
                protected Void doInBackground(String... params) {

                    int i = mListNum;

                    //The main list data container is not done
                    if (mFlag == false) {

                        for (; mListNum < i + 5; mListNum++) {
                            if (mListNum < mContentList.size()) {
                                tempList.add(mContentList.get(mListNum));
                            } else {
                                mFlag = true;
                                break;
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mFlagAdding = false;
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    isAdding = false;
                    mContentListCurrentDatas.addAll(tempList);
                    myListViewAdapter.notifyDataSetChanged();
                    if (mListNum >= mContentList.size()) {

                        mListView.setTextView("No more ...");
                    }
                }
            }.execute("");
        }


    }

    /**
     * ListViewAdapter
     */
    class MyListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mContentListCurrentDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mContentListCurrentDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.listview_layout, null);
                holder.textView_title = (TextView) convertView.findViewById(R.id.title);
                holder.textView_time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView_title.setText(mContentListCurrentDatas.get(position).getTitle());
            holder.textView_time.setText(mContentListCurrentDatas.get(position).getTime());

            return convertView;
        }

        public class ViewHolder {
            TextView textView_title;
            TextView textView_time;
        }
    }
}
