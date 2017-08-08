package com.e3lue.us.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.ui.UIHelper;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MessageFragment extends Fragment {

    private Activity context;

    @BindView(R.id.message_fragment_option1)
    TextView option1;

    @OnClick(R.id.message_fragment_option1)
    void click() {
        UIHelper.showSysMsg(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }
}