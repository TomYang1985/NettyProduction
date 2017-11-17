package com.example.client.sample.applistmodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.client.sample.R;
import com.example.client.sample.applistmodule.holder.AppListHolder;
import com.netty.client.msg.EMAppList;

import java.util.zip.Inflater;

import xiao.framework.adapter.XGCRecyclerViewAdapter;
import xiao.framework.viewholder.XGCRecyclerViewHolder;

/**
 * Created by robincxiao on 2017/11/6.
 */

public class AppListAdapter extends XGCRecyclerViewAdapter<EMAppList.AppInfo, AppListHolder> {

    public AppListAdapter(Context context) {
        super(context);
    }

    @Override
    protected AppListHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_app_list_item, parent, false);
        return new AppListHolder(context, this, parent, view, viewType);
    }

    @Override
    protected void setItemData(int position, AppListHolder holder, EMAppList.AppInfo model, int viewType) {
        holder.setData(model);
    }
}
