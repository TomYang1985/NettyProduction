package com.example.client.sample.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.client.sample.R;


/**
 * Created by robincxiao on 2016/11/14.
 */

public class LoadingDialog extends Dialog{
    private View contentView;
    private TextView mMsgText;

    public LoadingDialog(Context context) {
        super(context, R.style.DialogNormal);

        init(context);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);

        init(context);
    }

    private void init(Context context){
        contentView = View.inflate(context, R.layout.dialog_loading, null);
        setContentView(contentView);

        mMsgText = (TextView) findViewById(R.id.text_msg);

        setCancelable(false);
    }

    public LoadingDialog setMsg(String msg){
        mMsgText.setText(msg);

        return this;
    }
}
