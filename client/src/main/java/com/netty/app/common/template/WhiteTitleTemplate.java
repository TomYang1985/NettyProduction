package com.netty.app.common.template;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.netty.client.R;

import xiao.framework.template.BaseCommonTemplate;


/**
 * Created by robincxiao on 2016/11/1.
 */
public class WhiteTitleTemplate extends BaseCommonTemplate {
    public WhiteTitleTemplate(Context context) {
        super(context);

        mStatusBar.setBackgroundResource(R.color.colorPrimaryDark);
        mTitleLayout.setBackgroundResource(R.color.colorPrimary);
    }

    @Override
    public void addContentView(View contentView) {
        RelativeLayout.LayoutParams contentViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        mContainerLayout.addView(contentView, contentViewLayoutParams);
    }
}
