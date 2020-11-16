package com.netease.nmc.nertcsample.voicechanger.voice.ui;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nmc.nertcsample.voicechanger.voice.VoiceItemData;

import java.util.List;

public abstract class VoiceBaseAdapter<T extends VoiceItemData> extends RecyclerView.Adapter<VoiceBaseAdapter.Holder> {
    protected List<T> dataSource;
    protected T focusedData;
    protected Context context;
    protected OnItemClickListener<T> onItemClickListener;

    public VoiceBaseAdapter(Context context, List<T> dataSource) {
        this(context, dataSource, null);
    }

    public VoiceBaseAdapter(Context context, List<T> dataSource, T focusedData) {
        this.context = context;
        this.dataSource = dataSource;
        this.focusedData = focusedData;
    }

    /**
     * 更改 focused 项时调用
     *
     * @param newFocus 新的 focus 对象
     */
    public void changeFocus(T newFocus) {
        if (newFocus == null) {
            return;
        }
        this.focusedData = newFocus;
        notifyDataSetChanged();
    }

    /**
     * 设置 item 子项点击监听
     *
     * @param onItemClickListener 监听listener
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        T item = getItem(position);
        if (item == null) {
            return;
        }
        clickableView(holder).setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
            changeFocus(item);
        });

        if (focusedData != null && focusedData.value == item.value) {
            onFocus(holder, position);
        } else {
            onUnFocus(holder, position);
        }
    }

    /**
     * 确定相应点击范围，若存在不同的点击对象建议直接覆写 {@link #onBindViewHolder(Holder, int)}方法实现
     *
     * @param holder ViewHolder
     * @return 支持点击的view 会为其设置 onClickListener.
     */
    protected View clickableView(Holder holder) {
        return holder.itemView;
    }

    /**
     * 当前项处于 focused 状态调用
     */
    protected void onFocus(Holder holder, int position) {
        onFocus(holder, getItem(position));
    }

    protected void onFocus(Holder holder, T itemData) {
    }

    /**
     * 当前项处于 非 focused 状态调用
     */
    protected void onUnFocus(Holder holder, int position) {
        onUnFocus(holder, getItem(position));
    }

    protected void onUnFocus(Holder holder, T itemData) {
    }

    @Override
    public int getItemCount() {
        return dataSource != null ? dataSource.size() : 0;
    }

    public T getItem(int position) {
        int count = getItemCount();
        if (count < 0 || position >= count) {
            return null;
        }
        return dataSource.get(position);
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private SparseArray<View> viewCache;

        public Holder(@NonNull View itemView) {
            super(itemView);
            viewCache = new SparseArray<>();
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getViewById(@IdRes int viewId) {
            View resultView = viewCache.get(viewId);
            if (resultView == null) {
                resultView = itemView.findViewById(viewId);
                viewCache.put(viewId, resultView);
            }
            return (T) resultView;
        }
    }

    /**
     * 子项点击设置回调
     *
     * @param <T> 具体点击项数据
     */
    public interface OnItemClickListener<T extends VoiceItemData> {
        void onItemClick(T itemData);
    }
}
