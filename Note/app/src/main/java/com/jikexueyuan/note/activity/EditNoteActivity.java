package com.jikexueyuan.note.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jikexueyuan.note.R;
import com.jikexueyuan.note.broadcast.BootReceiver;
import com.jikexueyuan.note.contentProvider.MyContentProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {

    private Button mButtonSave;
    private EditText mEditTextInputTime, mEditTextInputContext;
    private String mInputTime, mInputContext;
    private String mTime;
    private Calendar c;
    private SimpleDateFormat sdf;
    public static final String  SEND_NOTIFICATION = "EidtNoteActivity send notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init data
        init();
        //set listener
        setListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editnote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel) {
            setResult(RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * set listener
     */
    private void setListener() {

        //set time
        mEditTextInputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TimePickerDialog(EditNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        mTime = sdf.format(new Date(c.getTimeInMillis()));
                        mEditTextInputTime.setText(mTime);
                    }
                }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false).show();

                new DatePickerDialog(EditNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DATE, dayOfMonth);

                    }
                },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();


            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mInputTime = mEditTextInputTime.getText().toString()+":00";
                mInputContext = mEditTextInputContext.getText().toString();
                if (TextUtils.isEmpty(mInputTime)) {

                    Toast.makeText(EditNoteActivity.this, "请输入时间！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mInputContext)) {

                    Toast.makeText(EditNoteActivity.this, "请输入内容！", Toast.LENGTH_SHORT).show();
                } else {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("time", mInputTime);
                    contentValues.put("context", mInputContext);
                    Uri id = getContentResolver().insert(MyContentProvider.url,contentValues);
                    long dataId = ContentUris.parseId(id);

                    setResult(RESULT_OK);

                    //set Alarm
                    setAlarm((int) dataId);
                    finish();

                }

            }

            /**
             * Set Alarm
             * @param dataId  Insert process create
             */
            private void setAlarm(int dataId) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(EditNoteActivity.this, BootReceiver.class);
                Bundle bundle = new Bundle();
                bundle.putInt("_id", dataId);
                bundle.putString("time",mInputTime);
                bundle.putString("context",mInputContext);
                intent.putExtra("iBundle",bundle);
                intent.putExtra("Action",SEND_NOTIFICATION);
                Log.v("id","id: "+dataId+"");
               // intent.setAction(SEND_NOTIFICATION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(EditNoteActivity.this, dataId,
                        intent ,PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    alarmManager.set(AlarmManager.RTC_WAKEUP,sdf.parse(mInputTime).getTime(),pendingIntent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * init data
     */
    private void init() {

        mButtonSave = (Button) findViewById(R.id.mButtonSave);
        mEditTextInputContext = (EditText) findViewById(R.id.mInputContext);
        mEditTextInputTime = (EditText) findViewById(R.id.mInputTime);
        mEditTextInputTime.setKeyListener(null);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        c = Calendar.getInstance();

    }
}
