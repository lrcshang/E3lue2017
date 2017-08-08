package com.e3lue.us.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.e3lue.us.R;

import java.util.ArrayList;


/**
 * Created by Leo on 2016/8/12.
 */
public class ImageShowAdapter extends RecyclerView.Adapter<ImageShowAdapter.ViewHolder> {

    public ArrayList<String> urls = null;
    Context context;
    private OnRecyclerViewItemClickListener listener;

    public ImageShowAdapter(ArrayList<String> datas, Context context) {
        this.urls = datas;
        this.context = context;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_image, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.bind(position);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        private int clickPosition;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.showimage_imgview);
        }

        public void bind(int position) {
            //设置条目的点击事件
            itemView.setOnClickListener(this);
            clickPosition = position;
            Glide.with(context)
                    .load(urls.get(position))
                    .into(mImageView);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) listener.onItemClick(v, clickPosition);
        }
    }
}
