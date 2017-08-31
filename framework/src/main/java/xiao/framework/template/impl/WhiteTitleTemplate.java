package xiao.framework.template.impl;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import xiao.framework.template.BaseCommonTemplate;

/**
 * Created by robincxiao on 2016/11/1.
 * 顶部title为天空蓝模板
 */
public class WhiteTitleTemplate extends BaseCommonTemplate {
    public WhiteTitleTemplate(Context context) {
        super(context);

//        mStatusBar.setBackgroundResource(R.color.colorPrimaryDark);
//        mTitleLayout.setBackgroundResource(R.color.colorPrimary);
    }

    @Override
    public void addContentView(View contentView) {
        RelativeLayout.LayoutParams contentViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        mContainerLayout.addView(contentView, contentViewLayoutParams);
    }
}
