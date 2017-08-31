package xiao.framework.template.impl;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import xiao.framework.template.BaseCommonTemplate;


/**
 * Created by robincxiao on 2016/11/1.
 * 无顶部状态栏的模板
 */
public class NoStatusBarTemplate extends BaseCommonTemplate {
    protected NoStatusBarTemplate(Context context) {
        super(context);

        setStatusBarVisibility(View.GONE);
    }

    @Override
    public void addContentView(View contentView) {
        RelativeLayout.LayoutParams contentViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        mContainerLayout.addView(contentView, contentViewLayoutParams);
    }

}
