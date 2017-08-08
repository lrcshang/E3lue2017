package com.e3lue.us.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.CheckIn;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.BaseAdapterHelper;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.DeviceUtil;
import com.e3lue.us.utils.SharedPreferences;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import okhttp3.Call;


/**
 * Created by Leo on 2017/4/19.
 */

public class CheckInListActivity extends SwipeBackActivity {
    private CheckInListActivity context;
    public static final String ACTION = "com.e3lue.us.activity";
    private int pno = 1;
    QuickAdapter<CheckIn> adapter;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.checkin_list_header)
    PtrClassicFrameLayout mPtrFrame;

    @BindView(R.id.checkin_list_listview)
    LoadMoreListView listView;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                if (pno == 1) {
                    adapter.clear();
                }
                adapter.addAll(list);
                pno++;
                listView.updateLoadMoreViewText(list);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_list_activity);
        ButterKnife.bind(this);
        context = this;

        textHeadTitle.setText("我的签到");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
        initView();
        loadData();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(myReceiver, filter);
    }
    public BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            pno=1;
            loadData();
        }

    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    public void initView() {

        adapter = new QuickAdapter<CheckIn>(context, R.layout.checkin_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, CheckIn entity) {
                String date = "<font color='gray'>" + entity.getCreateTime() + "</font>&#160;<font color='blue'>(" + entity.getUserName() + ")</font>";
                helper.setText(R.id.checkin_item_date, date)
                        .setText(R.id.checkin_item_addr, entity.getAddress())
                        .setText(R.id.checkin_item_note, entity.getNote()).setVisible(R.id.error_icon, entity.getIsshow());
            }
        };
        listView.setDrawingCacheEnabled(true);
        listView.setAdapter(adapter);
        final StoreHouseHeader header = new StoreHouseHeader(this);
        header.setPadding(0, DeviceUtil.dp2px(this, 15), 0, 0);
        header.initWithString("E-3LUE");
        header.setTextColor(getResources().getColor(R.color.gray));
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        // 下拉刷新
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                initData();
                loadData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        // 加载更多
        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData();
            }
        });

        // 点击事件
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= adapter.getCount()) return;
                CheckIn entity = adapter.getItem(i);
                UIHelper.showCheckInDetail(context, entity);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    Picasso.with(context).pauseTag(context);
                } else {
                    Picasso.with(context).resumeTag(context);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    public void initData() {
        pno = 1;
    }

    List<String> IDlist = new ArrayList<>();
    List<CheckIn> list;

    public void loadData() {
        OkGo.<String>post(HttpUrl.Url.CHECKINLIST)
                .params("page", "" + pno)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mPtrFrame.refreshComplete();
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        list = JSONArray.parseArray(result.getData().toString(), CheckIn.class);
                        if (list == null || list.size() == 0) {
                            showToast("已经是最后一页了");
                        }
                        if (list.size() > 0 && list != null && SharedPreferences.getInstance().getDataList("ERRORID").size() > 1) {
                            IDlist = SharedPreferences.getInstance().getDataList("ERRORID");
                            for (int i = 0; i < list.size(); i++) {
                                for (int j = 0; j < IDlist.size(); j++) {
                                    if (list.get(i).getID() == (Integer.valueOf(IDlist.get(j)))) {
                                        list.get(i).setIsshow(true);
                                    }
                                }
                            }
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = 0x01;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        mPtrFrame.refreshComplete();
                    }
                });

    }
}
