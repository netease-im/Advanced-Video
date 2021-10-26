package com.netease.nmc.nertcsample.beauty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.recyclerview.widget.RecyclerView;

import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.view.StickerItem;

import java.util.ArrayList;


public class StickerAdapter extends RecyclerView.Adapter {

    ArrayList<StickerItem> mStickerList;
    private View.OnClickListener mOnClickStickerListener;
    private int mSelectedPosition = 0;
    Context mContext;

    public StickerAdapter(ArrayList<StickerItem> list, Context context) {
        mStickerList = list;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item, null);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FilterViewHolder viewHolder = (FilterViewHolder) holder;
        viewHolder.imageView.setImageBitmap(mStickerList.get(position).icon);
        holder.itemView.setSelected(mSelectedPosition == position);
        if (mOnClickStickerListener != null) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickStickerListener);

            holder.itemView.setSelected(mSelectedPosition == position);
        }
    }

    public void setClickStickerListener(View.OnClickListener listener) {
        mOnClickStickerListener = listener;
    }

    @Override
    public int getItemCount() {
        return mStickerList.size();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageView;

        public FilterViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.icon);
        }
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }
}
