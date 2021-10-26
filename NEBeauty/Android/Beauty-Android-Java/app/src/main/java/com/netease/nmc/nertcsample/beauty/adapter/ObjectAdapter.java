package com.netease.nmc.nertcsample.beauty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.view.ObjectItem;

import java.util.List;


/**
* Created by netease on 17-6-7.
 */

public class ObjectAdapter extends RecyclerView.Adapter{
    List<ObjectItem> mObjectList;
    private View.OnClickListener mOnClickObjectListener;
    private int mSelectedPosition = 0;
    Context mContext;

    public ObjectAdapter(List<ObjectItem> list, Context context) {
        mObjectList = list;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item, null);
        return new ObjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ObjectViewHolder viewHolder = (ObjectViewHolder) holder;
        viewHolder.imageView.setImageResource(mObjectList.get(position).drawableID);
        //viewHolder.textView.setText(mObjectList.get(position).name);
        holder.itemView.setSelected(mSelectedPosition == position);
        if(mOnClickObjectListener != null) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickObjectListener);

            holder.itemView.setSelected(mSelectedPosition == position);
        }
    }

    public void setClickObjectListener(View.OnClickListener listener) {
        mOnClickObjectListener = listener;
    }

    @Override
    public int getItemCount() {
        return mObjectList.size();
    }

    static class ObjectViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageView;
        //TextView textView;

        public ObjectViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.icon);
            //textView = (TextView) itemView.findViewById(R.id.filter_text);
        }
    }

    public void setSelectedPosition(int position){
        mSelectedPosition = position;
    }
}
