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
    protected boolean isDarkTitleBar = false;

    private SoundPool soundPool;//声明一个SoundPool
    public int clickSoundID;//创建某个声音对应的音频ID
    public int moveSoundID;

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


//        initSound();

    }

    @Override
    protected void onDestroy() {
        //解绑视图
        mUnbinder.unbind();

        super.onDestroy();
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

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

    public Bitmap getBackground() {
        long startMs = System.currentTimeMillis();
        float radius = 8;//2
        float scaleFactor = 50;//8

        try {
            View view = getWindow().getDecorView();
//            view.setDrawingCacheEnabled(true);
//            view.buildDrawingCache(true);
            /**
             * 获取当前窗口快照，相当于截屏
             */
//            Bitmap bmp1 = view.getDrawingCache();
//            int height = getOtherHeight();//没有吧

            Bitmap bmp2 = null;
//        if (bmp1 == null) {
//            Log.e("knw", "bmp1 null!!!!!");
//            //获取当前屏幕的大小
            int width = getWindow().getDecorView().getRootView().getWidth();
            int height2 = getWindow().getDecorView().getRootView().getHeight();
////生成相同大小的图片
            bmp2 = Bitmap.createBitmap(width, height2, Bitmap.Config.ARGB_8888);
////            bmp2 = Bitmap.createBitmap(view.getWidth()/5,view.getHeight()/5, Bitmap.Config.ARGB_8888);
//            bmp2.eraseColor(Color.parseColor("#170145"));
//        } else {
//            bmp2 = Bitmap.createBitmap(bmp1, 0, height,bmp1.getWidth(), bmp1.getHeight() - height);
//        }

//            bmp2 = Bitmap.createBitmap(bmp1, 0, height,bmp1.getWidth(), bmp1.getHeight() - height);

            Canvas canvas0 = new Canvas();
            canvas0.setBitmap(bmp2);
            view.draw(canvas0);


            Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(overlay);
            canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
            canvas.scale(1 / scaleFactor, 1 / scaleFactor);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(bmp2, 0, 0, paint);
            overlay = FastBlur.doBlur(overlay, (int) radius, true);
//        L.d("Blur 耗时：" + (System.currentTimeMillis() - startMs));
            return overlay;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取系统状态栏和软件标题栏，部分软件没有标题栏，看自己软件的配置；
     *
     * @return
     */
    private int getOtherHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return statusBarHeight + titleBarHeight;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
////        public static final int KEYCODE_DPAD_CENTER = 23;
////        public static final int KEYCODE_DPAD_DOWN = 20;
////        public static final int KEYCODE_DPAD_DOWN_LEFT = 269;
////        public static final int KEYCODE_DPAD_DOWN_RIGHT = 271;
////        public static final int KEYCODE_DPAD_LEFT = 21;
////        public static final int KEYCODE_DPAD_RIGHT = 22;
////        public static final int KEYCODE_DPAD_UP = 19;
////        public static final int KEYCODE_DPAD_UP_LEFT = 268;
////        public static final int KEYCODE_DPAD_UP_RIGHT = 270;
//
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
//                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT
//                ||keyCode == KeyEvent.KEYCODE_DPAD_DOWN_RIGHT
//                ||keyCode == KeyEvent.KEYCODE_DPAD_LEFT
//                ||keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
//                ||keyCode == KeyEvent.KEYCODE_DPAD_UP
//                ||keyCode == KeyEvent.KEYCODE_DPAD_UP_LEFT
//                ||keyCode == KeyEvent.KEYCODE_DPAD_UP_RIGHT) {
//
//            playSound(moveSoundID);
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * 5.0以后
     */
//    @SuppressLint("NewApi")
//    private void initSound() {
//        soundPool = new SoundPool.Builder().build();
//        clickSoundID = soundPool.load(this, R.raw.click, 1);
//        moveSoundID = soundPool.load(this, R.raw.move, 1);
//    }

//    public  void playSound(int soundID) {
//        soundPool.play(
//                soundID,
//                0.1f,      //左耳道音量【0~1】
//                0.5f,      //右耳道音量【0~1】
//                0,         //播放优先级【0表示最低优先级】
//                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
//                1          //播放速度【1是正常，范围从0~2】
//        );
//    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        return super.dispatchKeyEvent(event);
//    }
}
