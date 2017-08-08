package com.e3lue.us.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e3lue.us.R;
import com.e3lue.us.model.BaseMenu;
import com.e3lue.us.model.HttpUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 2017/4/26.
 */

public class HomeFuncAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<BaseMenu> data;
    public HomeFuncAdapter(Context context, List<BaseMenu> data) {
        this.context = context;
        this.data = data;
        this.inflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data == null ? 0 : data.size();
    }

    @Override
    public BaseMenu getItem(int position) {
        // TODO Auto-generated method stub
        if (data != null && data.size() != 0) {
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (holder == null) {
            convertView = inflater.inflate(R.layout.home_func_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.home_func_text);
            holder.image = (ImageView) convertView.findViewById(R.id.home_func_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaseMenu menu = data.get(position);
        holder.name.setText(menu.getMobileName());
        Glide.with(context)
                .load(HttpUrl.Url.BASIC + menu.getMobileIcon())
                .into(holder.image);
        holder.name.setText(menu.getMobileName());
        Glide.with(context)
                .load(HttpUrl.Url.BASIC + menu.getMobileIcon())
                .into(holder.image);
        return convertView;
    }
    static class ViewHolder {
        TextView name;
        ImageView image;
    }
}
