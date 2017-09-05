package xiao.framework.activity;


import android.app.Activity;

import android.graphics.Bitmap;

import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Rect;

import android.media.SoundPool;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.view.View;
import android.view.Window;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import xiao.framework.template.BaseTemplate;
import xiao.framework.tools.StatusBarHelper;
import xiao.framework.util.FastBlur;
import xiao.framework.util.LayoutUtil;


/**
 * Created by robincxiao on 2016/11/1.
 */
public abstract class BaseFragmentActivity extends RxAppCompatActivity {
    protected Activity mContext;
    /**
     * 页面的真正的内容视图(除开了标题栏视图的)
     **/
    protected View mContentView;
    //当前页面模板
    protected BaseTemplate mTemplate;
    //Fragment管理器
    protected FragmentManager mFragmentManager = null;
    //默认Fragment的容器
    protected int mFragmentContainer = -1;
    //当前显示的Fragment
    public Fragment mCurrentFragment;
    private Unbinder mUnbinder;
    protected boolean isSetStatusBar = false;
    protected int mStatusBarColorId = android.R.color.white;
    protected boolean isDarkTitleBar = true;

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mContext = this;
        mFragmentManager = getSupportFragmentManager();
        mTemplate = createTemplate();
        if (mTemplate != null) {
            if (getContentLayout() != 0) {
                mContentView = LayoutUtil.inflate(mContext, getContentLayout(), null, false);
                mTemplate.addContentView(mContentView);
            }
            setContentView(mTemplate.getPageView());

            mUnbinder = ButterKnife.bind(this);
        }
        preSetContentView(savedInstanceState);
        doOnCreate(savedInstanceState);

        //注：setDarkStatusIcon需在setContentView后执行
        if (isSetStatusBar) {
            StatusBarHelper.initStatusBar(this, mStatusBarColorId, isDarkTitleBar);
        }

    }

    @Override
    protected void onDestroy() {
        //解绑视图
        mUnbinder.unbind();

        super.onDestroy();
    }


    /**
     * 在SetContentView前执行
     *
     * @param savedInstanceState
     */
    protected void preSetContentView(Bundle savedInstanceState) {

    }

    ;

    /**
     * 初始化视图
     */
    protected abstract void doOnCreate(Bundle savedInstanceState);

    /**
     * 创建模板，不能返回空，可以返回EmptyTemplate
     *
     * @return
     */
    protected abstract BaseTemplate createTemplate();

    /**
     * 返回内容view layout
     *
     * @return
     */
    protected abstract int getContentLayout();

    /*
     * [ 不要删除该函数 ],该函数的空实现修复了FragmentActivity中的bug
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }



}
