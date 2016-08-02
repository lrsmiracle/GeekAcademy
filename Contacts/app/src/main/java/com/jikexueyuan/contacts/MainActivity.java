package com.jikexueyuan.contacts;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jikexueyuan.contacts.contactBean.Contacter;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button maddContact;
    private ListView mListView ;
    private List<Contacter> mcontacterList;
    //姓名、电话号码临时信息
    private String mName,mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();//初始化ListView数据
        initListView();//初始化ListView
        initAddContacterButton();//设置添加联系人Button


    }

    private void initAddContacterButton() {
        //设置添加联系人Button
        maddContact = (Button) findViewById(R.id.button_addContact);
        maddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();//创建Dialog
            }
        });
    }

    /**
     * 初始化ListView数据
     */
    private void initData() {
        mcontacterList = new ArrayList<Contacter>();
        getContacts();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {

        mListView = (ListView) findViewById(R.id.listView);
        //实例化ContactAdapter
        ContactAdapter contactAdaper = new ContactAdapter();
        mListView.setAdapter(contactAdaper);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view_dialog = View.inflate(MainActivity.this,R.layout.listonclick_layout,null);
                builder.setView(view_dialog);
                builder.setTitle(R.string.pleaseChouse);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                //监听打电话发短信事件
                TextView tv_Call = (TextView) view_dialog.findViewById(R.id.textView_Call);
                TextView tv_SendMes = (TextView) view_dialog.findViewById(R.id.textView_SendMes);
                //获取事件电话号
                final String number = mcontacterList.get(position).getNumber();
                //设置alertdialog点击事件监听器
                setOnclick(tv_Call, tv_SendMes, number);
                builder.create().show();
            }

            //设置alertdialog点击事件监听器
            private void setOnclick(TextView tv_Call, TextView tv_SendMes, final String number) {
                tv_Call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ number));
                        startActivity(intent);
                    }
                });
                tv_SendMes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+ number));
                        startActivity(intent);
                    }
                });
            }
        });
    }

    class ContactAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mcontacterList.size();
        }

        @Override
        public Contacter getItem(int position) {
            return mcontacterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View list_Layout = View.inflate(MainActivity.this,R.layout.listview_layout,null);
            TextView tv_name = (TextView) list_Layout.findViewById(R.id.textView_name);
            TextView tv_number = (TextView) list_Layout.findViewById(R.id.textView_number);
            tv_name.setText(mcontacterList.get(position).getName());
            tv_number.setText(mcontacterList.get(position).getNumber());

            return list_Layout;
        }
    }

    /**
     * 创建Dialog
     */
    private void createDialog() {

        final View view = getLayoutInflater().inflate(R.layout.dialog_layout,null);

        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
        adb.setTitle(R.string.addContact);
        adb.setPositiveButton(R.string.confirm,null);
        adb.setNegativeButton(R.string.cancel,null);
        adb.setView(view);

        final AlertDialog alertdialog = adb.create();
        alertdialog.show();

        //确认按钮监听
        alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog里布局view
                EditText mEditText_name,mEditText_phoneNumber ;
                //dialog里布局view初始化
                mEditText_name = (EditText) view.findViewById(R.id.EditText_name);
                mEditText_phoneNumber = (EditText) view.findViewById(R.id.EditText_phoneNumber);
                String str1 = mEditText_name.getText().toString().trim();
                String str2 = mEditText_phoneNumber.getText().toString().trim();
                if(StringUtils.isEmpty(str1)){
                    Toast.makeText(MainActivity.this,R.string.InputContacter,Toast.LENGTH_SHORT).show();
                }else if(StringUtils.isEmpty(str2)){
                    Toast.makeText(MainActivity.this,R.string.InputPhoneNumber,Toast.LENGTH_SHORT).show();
                }else{

                    mName = str1;
                    mPhoneNumber = str2;
                    addContacts();
                    Toast.makeText(MainActivity.this,R.string.success,Toast.LENGTH_SHORT).show();
                    initData();
                    initListView();
                    alertdialog.dismiss();
                }
            }
        });
        //取消按钮监听
        alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,R.string.cancelAction,Toast.LENGTH_SHORT).show();
                alertdialog.dismiss();
            }
        });

    }
    /**
     * 查询联系人
     */
    public void getContacts(){
        Uri uri = Uri.parse("content://com.android.contacts/contacts"); // 访问所有联系人
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        while(cursor.moveToNext()){
            int contactsId = cursor.getInt(0);
            StringBuilder sb = new StringBuilder("contactsId=");
            sb.append(contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data"); //某个联系人下面的所有数据
            Cursor dataCursor = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            while(dataCursor.moveToNext()){
                String data = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if("vnd.android.cursor.item/name".equals(type)){    // 如果他的mimetype类型是name
                    sb.append(", name=" + data);
                    mName = data;
                }else if("vnd.android.cursor.item/phone_v2".equals(type)){ // 如果他的mimetype类型是phone
                    sb.append(", phone=" + data);
                    mPhoneNumber = data;
                }
                //获取ListView数据
                if(StringUtils.isNotEmpty(mName)&&StringUtils.isNotEmpty(mPhoneNumber)){

                    mcontacterList.add(new Contacter(mName,mPhoneNumber));
                    mPhoneNumber=null;
                    mName=null;
                }
            }
            Log.i("contacts", sb.toString());
        }
    }
    /**
     * 添加联系人
     * */
    public void addContacts(){
        /* 往 raw_contacts 中添加数据，并获取添加的id号*/
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        Uri rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long contactId = ContentUris.parseId(rawContactUri);

        // 向data表插入数据
        if (mName != "") {
            values.clear();
            values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, contactId);
            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, mName);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }
        // 向data表插入电话号码
        if (mPhoneNumber != "") {
            values.clear();
            values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, contactId);
            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, mPhoneNumber);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,values);
        }
    }

}
