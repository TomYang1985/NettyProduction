package com.example.client.sample.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.client.sample.R;
import com.example.client.sample.utils.DensityUtils;
import com.netty.client.utils.L;


/**
 * Created by robincxiao on 2016/11/16.
 */

public class ProgressWebView extends WebView {
    private ProgressBar progressbar;
    //加载是否完成
    private boolean isFinished = true;
    private LoadingPageListener loadingPageListener;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, DensityUtils.dp2px(context, 2.5f), 0, 0));

        Drawable drawable = context.getResources().getDrawable(R.drawable.progress_bar_states);
        progressbar.setProgressDrawable(drawable);
        addView(progressbar);
        setWebViewClient(new InnerWebViewClient());
        setWebChromeClient(new InnerWebChromeClient());
        //是否可以缩放
//        getSettings().setSupportZoom(true);
//        getSettings().setBuiltInZoomControls(true);
        getSettings().setUseWideViewPort(true);

        safeinit();
    }

    public void safeinit() {
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
        getSettings().setSavePassword(false);
        getSettings().setAllowFileAccess(false);
        if (Build.VERSION.SDK_INT >= 16) {
            getSettings().setAllowFileAccessFromFileURLs(false);
            getSettings().setAllowUniversalAccessFromFileURLs(false);
        }
    }

    public class InnerWebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        /**
         * 当WebView加载之后，返回 HTML 页面的标题 Title
         *
         * @param view
         * @param title
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
            if(!TextUtils.isEmpty(title)) {
                if (title.toLowerCase().contains("找不到网页") || title.toLowerCase().contains("error")){
                    L.print("onReceivedTitle");
                    if (loadingPageListener != null) {
                        loadingPageListener.onReceivedError();
                    }
                }
            }
        }
    }

    public class InnerWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (loadingPageListener != null) {
                loadingPageListener.onPageStarted();
            }
            isFinished = false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            L.print("onReceivedError");
            if (loadingPageListener != null) {
                loadingPageListener.onReceivedError();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            isFinished = true;
            if (loadingPageListener != null) {
                loadingPageListener.onPageFinished();
            }
            super.onPageFinished(view, url);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public boolean isLoadFinished() {
        return isFinished;
    }

    public void setLoadingPageListener(LoadingPageListener loadingPageListener) {
        this.loadingPageListener = loadingPageListener;
    }

    public interface LoadingPageListener {
        void onPageStarted();

        void onPageFinished();

        void onReceivedError();
    }
}
