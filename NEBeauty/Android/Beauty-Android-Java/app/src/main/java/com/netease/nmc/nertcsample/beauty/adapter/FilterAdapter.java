package com.netease.nmc.nertcsample.beauty.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.view.FilterItem;
import com.netease.nmc.nertcsample.beauty.view.RoundImageView;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter {

    List<FilterItem> mFilterList;
    private View.OnClickListener mOnClickFilterListener;
    private int mSelectedPosition = 0;
    Context mContext;

    public FilterAdapter(List<FilterItem> list, Context context) {
        mFilterList = list;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, null);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FilterViewHolder viewHolder = (FilterViewHolder) holder;
        viewHolder.imageView.setImageBitmap(mFilterList.get(position).icon);
        viewHolder.textView.setText(mFilterList.get(position).name);
        viewHolder.textView.setTextColor(Color.parseColor("#000000"));
        viewHolder.alphaView.getBackground().setAlpha(0);

        viewHolder.imageViewBg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_filter_view_unselected));
        holder.itemView.setSelected(mSelectedPosition == position);

        if(mSelectedPosition == position){
            viewHolder.alphaView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_filter_alpha_selected));
            viewHolder.textView.setText(mFilterList.get(position).name);
            viewHolder.textView.setTextColor(Color.parseColor("#ffffff"));

            viewHolder.imageViewBg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_filter_view_selected));
        }

        if(mOnClickFilterListener != null) {
            holder.itemView.setTag(position);

            holder.itemView.setOnClickListener(mOnClickFilterListener);
            holder.itemView.setSelected(mSelectedPosition == position);
        }
    }

    public void setClickFilterListener(View.OnClickListener listener) {
        mOnClickFilterListener = listener;
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {

        View view;
        RoundImageView imageView;
        TextView textView;
        ImageView alphaView;
        LinearLayout imageViewBg;

        public FilterViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (RoundImageView) itemView.findViewById(R.id.iv_filter_image);
            textView = (TextView) itemView.findViewById(R.id.filter_text);
            alphaView = (ImageView) itemView.findViewById(R.id.iv_alpha_view);
            imageViewBg = (LinearLayout) itemView.findViewById(R.id.ll_filter_image);
        }
    }

    public void setSelectedPosition(int position){
        mSelectedPosition = position;
    }
}
