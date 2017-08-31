package xiao.framework.template.impl;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import xiao.framework.template.BaseTemplate;


/**
 * Created by robincxiao on 2016/11/1.
 * 空模板，没有状态栏和title
 */
public class EmptyTemplate extends BaseTemplate {
    //整个页面的视图View
    protected LinearLayout mPageView;

    public EmptyTemplate(Context context) {
        super(context);
        mContext = context;
        mPageView = new LinearLayout(mContext);
    }

    @Override
    public void addContentView(View contentView) {
        RelativeLayout.LayoutParams contentViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        mPageView.addView(contentView, contentViewLayoutParams);
    }

    @Override
    public View getPageView() {
        return mPageView;
    }

}
