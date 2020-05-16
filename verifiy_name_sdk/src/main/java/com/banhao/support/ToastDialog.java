package com.banhao.support;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by zhongpingye on 2020/4/24.
 */


public class ToastDialog extends Dialog {
    Activity activity;
    private TextView mTextView;
    String _msg;
    private ViewGroup viewGroup;

    public ToastDialog(Context context, String msg) {
        super(context, R.style.custom_dialog_style);
        activity = (Activity) context;
        _msg = msg;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCanceledOnTouchOutside(false);
        viewGroup = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.toast, null);
        addContentView(viewGroup,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mTextView = (TextView)findViewById(R.id.toast_msg);
        mTextView.setText(_msg);
    }
}