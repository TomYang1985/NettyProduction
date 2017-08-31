package xiao.framework.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import xiao.framework.adapter.XGCRecyclerViewAdapter;

/**
 * Created by xiaoguochang on 2015/12/6.
 */
public abstract class XGCRecyclerViewHolderExt<M, Adpt extends XGCRecyclerViewAdapter> extends XGCRecyclerViewHolder<Adpt> {

    /**
     * 子类必须要实现
     *
     * @param context
     * @param adapter
     * @param parent
     * @param itemView
     * @param viewType
     */
    public XGCRecyclerViewHolderExt(Context context, Adpt adapter, ViewGroup parent, View itemView, int viewType) {
        super(context, adapter, parent, itemView, viewType);
    }

    public abstract void setData(M data);


}
