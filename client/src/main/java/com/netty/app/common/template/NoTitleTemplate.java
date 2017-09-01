package com.netty.app.common.template;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import xiao.framework.template.BaseCommonTemplate;

/**
 * Created by robincxiao on 2016/11/1.
 * 无顶部titile的模板
 */
public class NoTitleTemplate extends BaseCommonTemplate {
    protected NoTitleTemplate(Context context) {
        super(context);

        setTitleLayoutVisibility(View.GONE);
    }

    @Override
    public void addContentView(View contentView) {
        RelativeLayout.LayoutParams contentViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        mContainerLayout.addView(contentView, contentViewLayoutParams);
    }

}
