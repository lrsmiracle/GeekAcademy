package com.jikexueyuan.add20cards;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Liurs on 2016/6/1.
 **/
public class Card extends FrameLayout {


    private TextView mTextView;

    public Card(Context context) {
        super(context);

        //init card number
        initCardNumber(getContext());

    }

    /**
     * init card number
     *
     * @param context
     */
    private void initCardNumber(Context context) {
        mTextView = new TextView(context);
        mTextView.setText("aaa");
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTextView.setTextColor(getResources().getColor(R.color.textcolor, null));
            mTextView.setBackgroundColor(getResources().getColor(R.color.textbgcolor, null));
        }else{
            mTextView.setTextColor(getResources().getColor(R.color.textcolor));
            mTextView.setBackgroundColor(getResources().getColor(R.color.textbgcolor));
        }
        mTextView.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 10, 10);
        addView(mTextView);
    }

    public Card(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCardNumber(context);
    }

    public Card(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCardNumber(context);
    }

    /**
     * set card text
     *
     * @param str card text string
     */
    public void setTextView(String str) {
        mTextView.setText(str);
    }

}
