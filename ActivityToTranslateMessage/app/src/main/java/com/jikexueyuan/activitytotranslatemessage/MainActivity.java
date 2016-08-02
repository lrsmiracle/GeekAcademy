package com.jikexueyuan.activitytotranslatemessage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    /**
     * 返回结果码
     */
    public final static int RETURNCODE_CONFIRM = 1,RETURNCODE_CANCEL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化View
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.showView);

        //实例化Intent
        final Intent intent = new Intent(this,MessageInputActivity.class);

        //设置监听器
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){

            switch (data.getIntExtra("returnCode",0)){

                case RETURNCODE_CANCEL:
                    Toast.makeText(this,"您取消了操作！",Toast.LENGTH_SHORT).show();
                    break;
                case RETURNCODE_CONFIRM:
                    String str = data.getStringExtra("input");
                    textView.setText("您今天的愿望是：\n"+ str);
                    break;
            }
        }


    }
}
