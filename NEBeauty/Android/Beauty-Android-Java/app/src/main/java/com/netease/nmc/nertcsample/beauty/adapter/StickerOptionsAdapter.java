package com.netease.nmc.nertcsample.beauty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.view.StickerOptionsItem;

import java.util.ArrayList;

public class StickerOptionsAdapter extends RecyclerView.Adapter{
    ArrayList<StickerOptionsItem> mStickerOptions;
    private View.OnClickListener mOnClickStickerListener;
    private int mSelectedPosition = 0;
    Context mContext;

    public StickerOptionsAdapter(ArrayList<StickerOptionsItem> list, Context context) {
        mStickerOptions = list;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_options_item, null);
        if (mOnClickStickerListener != null) {
            view.setOnClickListener(mOnClickStickerListener);
        }
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FilterViewHolder viewHolder = (FilterViewHolder) holder;
        viewHolder.mIcon.setImageBitmap(mStickerOptions.get(position).unselectedtIcon);
        viewHolder.mFlag.setVisibility(View.INVISIBLE);
        holder.itemView.setTag(position);
        holder.itemView.setSelected(mSelectedPosition == position);
        if(mSelectedPosition == position){
            viewHolder.mIcon.setImageBitmap(mStickerOptions.get(position).selectedtIcon);
            viewHolder.mFlag.setVisibility(View.VISIBLE);

            return;
        }
    }

    public void setClickStickerListener(View.OnClickListener listener) {
        mOnClickStickerListener = listener;
    }

    @Override
    public int getItemCount() {
        return mStickerOptions.size();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView mIcon;
        ImageView mFlag;

        public FilterViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mIcon = (ImageView) itemView.findViewById(R.id.iv_sticker_options_icon);
            mFlag = (ImageView) itemView.findViewById(R.id.iv_select_flag);
        }
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;

    }
}
