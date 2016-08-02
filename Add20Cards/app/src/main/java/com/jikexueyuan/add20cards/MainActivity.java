package com.jikexueyuan.add20cards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridLayout;

public class MainActivity extends AppCompatActivity {

    private GridLayout root;
    private int mWidth, mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new GridLayout(this) {
            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                // Add 20 Cards objects
                addCards(w, h);
            }

            /**
             * Add 20 Cards Objects
             * @param w screen width
             * @param h screen height
             */
            private void addCards(int w, int h) {
                mWidth = Math.min(w, h);
                mWidth /= 4;
                mHeight = h - (10 + mWidth) * 5;
                root.setPadding(0,mHeight / 2+10,0,0);
                root.setColumnCount(4);
                for (int i = 1; i <= 20; i++) {
                    Card card = new Card(MainActivity.this);
                    card.setTextView(i + "");
                    card.setPadding(5, 5, 5, 5);
                    root.addView(card, mWidth, mWidth);
                }
            }
        };
        setContentView(root);
    }

}
