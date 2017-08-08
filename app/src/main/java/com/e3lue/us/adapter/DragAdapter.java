package com.e3lue.us.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.e3lue.us.R;
import com.e3lue.us.db.operate.OperateDao;
import com.e3lue.us.model.BaseMenu;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.utils.SharedPreferences;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DragAdapter extends BaseAdapter {
    /**
     * 本地数据库换存类
     */
    OperateDao operateDao;
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    public List<BaseMenu> channelList;
    /** TextView 频道内容 */
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    static class ViewHolder {
        TextView name;
        ImageView image;
        ImageView imageView;
    }

    public DragAdapter(Context context, List<BaseMenu> channelList) {
        this.context = context;
        this.channelList = channelList;
        operateDao = new OperateDao(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public BaseMenu getItem(int position) {
        // TODO Auto-generated method stub
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void notifyView() {
        is=VISIBLE;
        notifyDataSetChanged();
    }

    int is = GONE;
    int not = GONE;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (holder == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.home_func_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.home_func_text);
            holder.image = (ImageView) convertView.findViewById(R.id.home_func_image);
            holder.imageView = (ImageView) convertView.findViewById(R.id.error_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaseMenu menu = getItem(position);

        holder.name.setText(menu.getMobileName());
        Glide.with(context)
                .load(HttpUrl.Url.BASIC + menu.getMobileIcon())
                .into(holder.image);
        if (isChanged && (position == holdPosition) && !isItemShow) {
            holder.name.setText("");
            holder.name.setSelected(true);
            holder.name.setEnabled(true);
            isChanged = false;
        }
        if (!isVisible && (position == -1 + channelList.size())) {
            holder.name.setText("");
            holder.name.setSelected(true);
            holder.name.setEnabled(true);
        }
        if (remove_position == position) {
            holder.name.setText("");
        } if (SharedPreferences.getInstance().getDataList("ERRORID").size() > 1) {
            is=VISIBLE;
            if (menu.getMobileName().equals("签到"))
                holder.imageView.setVisibility(is);
        } else holder.imageView.setVisibility(not);
        return convertView;
    }

    /**
     * 添加频道列表
     */
    public void addItem(BaseMenu channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        BaseMenu dragItem = getItem(dragPostion);
        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }
        isChanged = true;
        operateDao.deleteAll();
        operateDao.insertTest(channelList);
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<BaseMenu> getChannnelLst() {
        return channelList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<BaseMenu> list) {
        channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }
}