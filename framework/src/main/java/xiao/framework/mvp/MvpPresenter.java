package xiao.framework.mvp;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import xiao.framework.imageloader.ImageLoadTool;
import xiao.framework.util.NetUtils;
import xiao.framework.widget.dialog.LoadingDialog;

/**
 * Created by robincxiao on 2016/10/31.
 */

public abstract class MvpPresenter implements BasePresenter {
    protected Context mContext;
    private LoadingDialog mLoadingDialog;


    public MvpPresenter(Context context) {
        this.mContext = context;
    }

    public void onCreate() {
    }

    public void onStart() {
    }

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onResume() method.
     */
    public void onResume() {
    }

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onPause() method.
     */
    public void onPause() {
    }

    public void onStop() {
    }

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onDestroy() method.
     */
    public void onDestroy() {
    }

    /**
     * 判断网络是否连接
     *
     * @return
     */
    public boolean isConnected() {
        boolean isConnected = NetUtils.isConnected(mContext);
        if (!isConnected) {
            //T.showLongCustomHint(mContext, "网络未连接");
        }

        return isConnected;
    }

    /**
     * 判断网络是否连接(无提示)
     *
     * @return
     */
    public boolean isConnectedWithoutHint() {
        return NetUtils.isConnected(mContext);
    }


    /*
     * loading管理
     */
    public void showLoadingDialog(Context context, String msg) {
        mLoadingDialog = new LoadingDialog(context);
        mLoadingDialog.setMsg(msg).show();
    }

    public void dissmissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    /******************************常用图片加载管理*********************************/
    /**
     * 显示图片（使用默认的mOptionsSmallImage）
     *
     * @param imageView
     * @param url
     */
    public void displayImage(ImageView imageView, String url) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        ImageLoadTool.getInstance().displayImage(imageView, url, null, null);
    }

    /**
     * 显示大图片（区别在于使用mOptionsBigImage）
     *
     * @param imageView
     * @param url
     */
    public void displayBigImage(ImageView imageView, String url) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        ImageLoadTool.getInstance().displayImage(imageView, url, ImageLoadTool.mOptionsBigImage, null);
    }

    /**
     * 显示图片(支持网络和本地)
     *
     * @param imageView
     * @param url
     */
    public void displayImage(ImageView imageView, String url, SimpleImageLoadingListener listener) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        ImageLoadTool.getInstance().displayImage(imageView, url, listener);
    }

    /**
     * 显示图片(支持网络和本地)
     *
     * @param imageView
     * @param url
     */
    public void displayImage(ImageView imageView, String url, DisplayImageOptions displayImageOptions, SimpleImageLoadingListener listener) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        ImageLoadTool.getInstance().displayImage(imageView, url, displayImageOptions, listener);
    }

    public void displayEmptyImage(ImageView imageView) {
        ImageLoadTool.getInstance().displayEmptyImage(imageView, ImageLoadTool.mOptionsSmallImage);
    }

    /**
     * 显示图片(支持网络和本地, 区别在于使用mOptionsBigImage)
     *
     * @param imageView
     * @param url
     */
    public void displayBigImage(ImageView imageView, String url, SimpleImageLoadingListener listener) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        ImageLoadTool.getInstance().displayImage(imageView, url, ImageLoadTool.mOptionsBigImage, listener);
    }

    /**
     * 从Drawable中加载图片
     *
     * @param imageView
     * @param url
     * @param displayImageOptions
     */
    public void displayImageFromDrawable(ImageView imageView, String url, DisplayImageOptions displayImageOptions) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        ImageLoadTool.getInstance().displayImageFromDrawable(imageView, url, displayImageOptions);
    }

    /**
     * 从Drawable中加载图片(区别在于使用mOptionsBigImage)
     *
     * @param imageView
     * @param url
     */
    public void displayBigImageFromDrawable(ImageView imageView, String url) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        ImageLoadTool.getInstance().displayImageFromDrawable(imageView, url, ImageLoadTool.mOptionsBigImage);
    }

    public void displayBigEmptyImage(ImageView imageView) {
        ImageLoadTool.getInstance().displayEmptyImage(imageView, ImageLoadTool.mOptionsBigImage);
    }
}
