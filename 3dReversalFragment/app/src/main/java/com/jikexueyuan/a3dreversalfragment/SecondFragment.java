package com.jikexueyuan.a3dreversalfragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Liurs on 2016/6/14.
 **/
public class SecondFragment extends Fragment {

    private LinearLayout root;
    private Button mButton;

    public SecondFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (LinearLayout) inflater.inflate(R.layout.fragment_second, container, false);
        //Set listener;
        setListener();
        return root;
    }

    /**
     * set listener
     */
    private void setListener() {
        mButton = (Button) root.findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.fragment_second_3d_reversal_enter,R.animator.fragment_second_3d_reversal_exit)
                        .replace(R.id.container, new MainFragment()).commit();
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
