package com.example.client.sample.applistmodule.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.client.sample.R;
import com.example.client.sample.applistmodule.adapter.AppListAdapter;
import com.netty.client.msg.EMAppList;
import com.netty.client.utils.L;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.BindView;
import xiao.framework.imageloader.ImageLoadTool;
import xiao.framework.viewholder.XGCRecyclerViewHolderExt;

/**
 * Created by robincxiao on 2017/11/6.
 */

public class AppListHolder extends XGCRecyclerViewHolderExt<EMAppList.AppInfo, AppListAdapter>{
    @BindView(R.id.img_icon)
    ImageView imageView;
    @BindView(R.id.text_name)
    TextView nameText;

    /**
     * 子类必须要实现
     *
     * @param context
     * @param adapter
     * @param parent
     * @param itemView
     * @param viewType
     */
    public AppListHolder(Context context, AppListAdapter adapter, ViewGroup parent, View itemView, int viewType) {
        super(context, adapter, parent, itemView, viewType);
    }

    @Override
    public void setData(EMAppList.AppInfo data) {
        final ImageView tempImage = imageView;
        String[] array = data.packageName.split("\\.");
        nameText.setText(array[array.length - 1]);
        ImageLoadTool.getInstance().displayImage(imageView, data.iconUrl, null);
    }

    @Override
    protected void initWidgets() {

    }
}
