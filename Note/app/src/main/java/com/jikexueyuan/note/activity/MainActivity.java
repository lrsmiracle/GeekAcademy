package com.jikexueyuan.note.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jikexueyuan.note.R;
import com.jikexueyuan.note.bean.NoteBean;
import com.jikexueyuan.note.broadcast.BootReceiver;
import com.jikexueyuan.note.contentProvider.MyContentProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private Binder myServiceBinder;
    private ArrayList<NoteBean> mArrayList;
    private ListView mListView;
    private FloatingActionButton mFloatButton;
    public static final int ADDNOTE = 1;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init data
        initData();

        //setListener
        setListener();

    }

    /**
     * set listener
     */
    private void setListener() {

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this).setTitle("请确认").
                        setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String id = mArrayList.get(position).get_id() + "";
                                int _id = mArrayList.get(position).get_id();
                                getContentResolver().delete(MyContentProvider.url, "_id = ?", new String[]{id});
                                initListView();
                                Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                Log.v("id","id: "+_id+"");
                                cancelAlarm(_id);
                            }

                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).setMessage("真的要删掉我么？").create().show();
                return true;
            }
        });
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, EditNoteActivity.class), ADDNOTE);
            }
        });
    }

    private void cancelAlarm(int _id) {
        Log.v("id","id: "+_id+"");
       // Intent intent = new Intent(MainActivity.this, BootReceiver.class);
        Intent intent = new Intent(getApplicationContext(), BootReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,_id,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    /**
     * init data
     */
    private void initData() {


        //init FloatButton
        mFloatButton = (FloatingActionButton) findViewById(R.id.float_add);
        mArrayList = new ArrayList<NoteBean>();

        //init listView
        initListView();

        //init SimpleDateFormat
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


 //       bindService(new Intent(MainActivity.this, MyService.class),this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
 //       unbindService(this);
    }

    /**
     * init ListView
     */
    private void initListView() {

        mArrayList.clear();
        Cursor c = getContentResolver().query(MyContentProvider.url,
                new String[]{"_id", "time", "context"}, null, null, "time ASC");
        String str = null;
        while (c.moveToNext()) {
            NoteBean note = new NoteBean();
            str = c.getString(c.getColumnIndex("time"));
            note.setTime(str);
            str = c.getString(c.getColumnIndex("context"));
            note.setContext(str);
            note.set_id(c.getInt(c.getColumnIndex("_id")));
            mArrayList.add(note);
        }

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new ListAdapter());
    }

    /**
     * ListAdapter
     */
    class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public NoteBean getItem(int position) {
            return mArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View ListLayout = View.inflate(MainActivity.this, R.layout.listview_layout, null);
            TextView textViewTime = (TextView) ListLayout.findViewById(R.id.textView_time);
            TextView textViewContext = (TextView) ListLayout.findViewById(R.id.textView_context);
            textViewTime.setText(mArrayList.get(position).getTime());
            textViewContext.setText(mArrayList.get(position).getContext());

            return ListLayout;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADDNOTE) {

            if (resultCode == RESULT_OK) {

                initListView();
                Toast.makeText(MainActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();

            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_createNote) {
            startActivityForResult(new Intent(MainActivity.this, EditNoteActivity.class), ADDNOTE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        myServiceBinder = (Binder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {


    }
}
