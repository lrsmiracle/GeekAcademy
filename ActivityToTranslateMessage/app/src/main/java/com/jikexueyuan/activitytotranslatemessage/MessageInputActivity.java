package com.jikexueyuan.activitytotranslatemessage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

public class MessageInputActivity extends AppCompatActivity {

    private EditText editText;
    private Button button_confirm,button_cancel;
    /**
     * Activity返回结果码
     */
    private int MessageInputActivityReCode = 1;
    /**
     * 返回结果码
     */
    public final static int RETURNCODE_CONFIRM = 1,RETURNCODE_CANCEL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_input);

        //实例化View
        button_cancel = (Button) findViewById(R.id.cancel_action);
        button_confirm = (Button) findViewById(R.id.confirm_action);
        editText = (EditText) findViewById(R.id.editText);
        //设置监听器
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MessageInputActivity.this,MainActivity.class);
                intent.putExtra("returnCode",RETURNCODE_CANCEL);
                setResult(MessageInputActivityReCode,intent);
                finish();
            }
        });
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MessageInputActivity.this,MainActivity.class);
                String str = editText.getText().toString().trim();
                if(StringUtils.isEmpty(str)){
                    Toast.makeText(MessageInputActivity.this,"请输入您的愿望！",Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("input",str);
                intent.putExtra("returnCode",RETURNCODE_CONFIRM);
                setResult(MessageInputActivityReCode,intent);
                finish();
            }
        });
    }
}
