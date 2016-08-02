package com.jikexueyuan.appwithhttp.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jikexueyuan.appwithhttp.R;

/**
 * The fragment is used to show ListView content
 */
public class ListViewContextFragment extends Fragment {

    private Button mButton;
    public ListViewContextFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) View.inflate(getActivity(),R.layout.fragment_list_view_context,null);
        TextView textViewTitle = (TextView) linearLayout.findViewById(R.id.textViewTitle);
        TextView textViewTime = (TextView) linearLayout.findViewById(R.id.textViewTime);
        TextView textViewContent = (TextView) linearLayout.findViewById(R.id.textViewContent);
        mButton = (Button) linearLayout.findViewById(R.id.button_back);

        //Obtain datas
        String title = getArguments().getString("Title");
        String time = getArguments().getString("Time");
        String content = getArguments().getString("Content");

        //Set textView data
        textViewTitle.setText(title);
        textViewTime.setText(time);
        textViewContent.setText(content);

        //Set listener
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return linearLayout;
    }


}
