package com.e3lue.us.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.e3lue.us.R;
import com.e3lue.us.adapter.DragAdapter;
import com.e3lue.us.adapter.HomeFuncAdapter;
import com.e3lue.us.db.DatabaseHelper;
import com.e3lue.us.db.operate.OperateDao;
import com.e3lue.us.model.BaseMenu;
import com.e3lue.us.model.BaseMessage;
import com.e3lue.us.model.BaseUser;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.loopviewpager.AutoLoopViewPager;
import com.e3lue.us.ui.view.LineScrollGridView;
import com.e3lue.us.ui.view.LineScrollGridView_1;
import com.e3lue.us.ui.viewpagerindicator.CirclePageIndicator;
import com.e3lue.us.utils.SharedPreferences;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainPagerFragment extends Fragment {

    Gson gson;
    List<BaseMessage> messageList;
    HomeFuncAdapter homeFuncAdapter2;
    DragAdapter homeFuncAdapter;

    @BindView(R.id.home_top_picture)
    AutoLoopViewPager picturePager;

    @BindView(R.id.home_top_indicator)
    CirclePageIndicator indicator;

    @BindView(R.id.home_func)
    LineScrollGridView func;

    @BindView(R.id.home_func_mana)
    LineScrollGridView_1 func_mana;

    @BindView(R.id.home_func_mana_layout)
    LinearLayout mana_layout;

    private GalleryPagerAdapter galleryAdapter;
    public static final String ACTION = "com.e3lue.us.activity";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        operateDao = new OperateDao(getActivity());
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        getActivity().registerReceiver(myReceiver, filter);
        return view;
    }
    public BroadcastReceiver myReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            homeFuncAdapter.notifyView();
        }

    };
    private DatabaseHelper databaseHelper = null;

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager
                    .getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gson = new Gson();
        messageList = new ArrayList<BaseMessage>();
        BaseMessage msg1 = new BaseMessage();
        msg1.setMessagePicture("20170428-2.jpg");
        msg1.setRelationURL("http://mp.weixin.qq.com/s/87RnqaYCfC8o15IhFik3XQ");
        msg1.setMessageTitle("英雄,出征!");
        BaseMessage msg2 = new BaseMessage();
        msg2.setMessagePicture("20170428-3.jpg");
        msg2.setRelationURL("http://mp.weixin.qq.com/s/636Z3CRlGMyjv6dTJyRjTA");
        msg2.setMessageTitle("三强出炉,王者之争即将开始.");
        BaseMessage msg3 = new BaseMessage();
        msg3.setMessagePicture("20170428-1.jpg");
        msg3.setRelationURL("http://mp.weixin.qq.com/s/bAyvaIIdNkDVTFidX8Ov_w");
        msg3.setMessageTitle("一体机京东众筹开启");

        messageList.add(msg1);
        messageList.add(msg2);
        messageList.add(msg3);

        initView();
    }

    public void initView() {
        galleryAdapter = new GalleryPagerAdapter();
        picturePager.startAutoScroll();
        picturePager.setAdapter(galleryAdapter);
        indicator.setViewPager(picturePager);
        indicator.setPadding(5, 5, 10, 5);

        GetMenu();
        GetManaMenu();
    }

    //应用功能表数据
    public void GetMenu() {
        OkGo.<String>post(HttpUrl.Url.Menu)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            List<BaseMenu> menus = JSONArray.parseArray(result.getData().toString(), BaseMenu.class);
                            if (menus == null) return;
                            setMenu(menus);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    //管理功能表数据
    public void GetManaMenu() {
        BaseUser user = gson.fromJson(SharedPreferences.getInstance().getString("BaseUser", ""), BaseUser.class);
        if (user.getIsLeader() == 0) return;
        OkGo.<String>post(HttpUrl.Url.ManaMenu)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            List<BaseMenu> menus = JSONArray.parseArray(result.getData().toString(), BaseMenu.class);
                            if (menus == null) return;
                            setAdminMenu(menus);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    OperateDao operateDao;

    //功能表
    public void setMenu(List<BaseMenu> menus) {
//        Collections.sort(menus);
        if (operateDao.display().size() <= 0) {
            operateDao.insertTest(menus);
        } else if (menus.size() != operateDao.display().size()) {
            operateDao.deleteAll();
            operateDao.insertTest(menus);
        } else {
            menus.clear();
            menus = operateDao.display();
        }

        homeFuncAdapter = new DragAdapter(getActivity(), menus);
        func.setAdapter(homeFuncAdapter);
        func.setOnItemClickListener(listener1);
    }

    //管理功能
    public void setAdminMenu(List<BaseMenu> menus) {
        mana_layout.setVisibility(View.VISIBLE);
        Collections.sort(menus);
        homeFuncAdapter2 = new HomeFuncAdapter(getActivity(), menus);
        func_mana.setAdapter(homeFuncAdapter2);
        func_mana.setOnItemClickListener(listener2);
    }

    //应用点击
    AdapterView.OnItemClickListener listener1 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BaseMenu menu = (BaseMenu) homeFuncAdapter.getItem(position);
            switch (menu.getEnglishName().toLowerCase()) {
                case "signin":
                    UIHelper.showCheckInList(getActivity());
                    break;
                case "expenses":
                    UIHelper.showExpensesMain(getActivity());
                    break;
                case "diary":
                    UIHelper.showDiaryList(getActivity());
                    break;
                case "help":
                    UIHelper.showSuggestionList(getActivity());
                    break;
                case "gamecluborder":
                    UIHelper.showGcList(getActivity());
                    break;
                case "fileshare":
                    UIHelper.showFileDown(getActivity());
                    break;
            }
        }
    };

    //管理功能点击
    AdapterView.OnItemClickListener listener2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BaseMenu menu = (BaseMenu) homeFuncAdapter2.getItem(position);
            switch (menu.getEnglishName().toLowerCase()) {
                case "diary":
                    UIHelper.showDiaryMana(getActivity());
                    break;
                case "gameclub":
                    UIHelper.showGameClubMap(getActivity());
                    break;
                case "gamecluborder":
                    UIHelper.showGcMana(getActivity());
                    break;
            }
        }
    };

    //轮播图适配器
    public class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView item = new ImageView(getActivity());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
            item.setLayoutParams(params);
            item.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getActivity())
                    .load(HttpUrl.Url.BASIC + "/userfiles/images/" + messageList.get(position).getMessagePicture())
                    .into(item);
            container.addView(item);

            final int pos = position;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showWebViewer(getActivity(), messageList.get(pos).getMessageTitle(), messageList.get(pos).getRelationURL());
                }
            });

            return item;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
