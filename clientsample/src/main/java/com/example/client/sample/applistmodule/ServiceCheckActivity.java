package com.example.client.sample.applistmodule;

import android.os.Bundle;
import android.widget.TextView;

import com.example.client.sample.R;
import com.example.client.sample.template.WhiteTitleTemplate;
import com.example.client.sample.widget.ProgressWebView;

import butterknife.BindView;
import xiao.framework.activity.BaseFragmentActivity;
import xiao.framework.template.BaseTemplate;

/**
 * Created by robincxiao on 2017/11/22.
 */

public class ServiceCheckActivity extends BaseFragmentActivity {
    private static final int DEFAULT_PORT = 8443;
    @BindView(R.id.progresswebview)
    ProgressWebView mProgressWebView;
    @BindView(R.id.text_url)
    TextView mUrlText;
    private WhiteTitleTemplate mTemplate = null;
    private int port = DEFAULT_PORT;
    private String ip;

    @Override
    protected void doOnCreate(Bundle savedInstanceState) {
        ip = getIntent().getStringExtra("ip");

        mProgressWebView.setLoadingPageListener(new ProgressWebView.LoadingPageListener() {
            @Override
            public void onPageStarted() {

            }

            @Override
            public void onPageFinished() {

            }

            @Override
            public void onReceivedError() {
                if (port <= DEFAULT_PORT + 2) {
                    mProgressWebView.loadUrl(getUrl(ip, ++port));
                }
            }
        });

        mProgressWebView.loadUrl(getUrl(ip, port));
    }

    private String getUrl(String ip, int port) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(ip).append(":").append(port);
        String url = builder.toString();
        mUrlText.setText(url);
        return url;
    }

    @Override
    protected BaseTemplate createTemplate() {
        mTemplate = new WhiteTitleTemplate(this);

        mTemplate.setTitleText("服务存活检测");
        mTemplate.setImageResource(R.mipmap.ic_back);

        return mTemplate;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_service_check;
    }
}
