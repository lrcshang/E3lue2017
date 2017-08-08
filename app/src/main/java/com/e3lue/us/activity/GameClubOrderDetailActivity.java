package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.e3lue.us.R;
import com.e3lue.us.adapter.WorkFlowTaskAdapter;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.GameClub;
import com.e3lue.us.model.GameClubOrder;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.model.WorkFlowTask;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.SharedPreferences;
import com.e3lue.us.utils.StringUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leo on 2017/5/3.
 */

public class GameClubOrderDetailActivity extends SwipeBackActivity {
    BaiduMap mBaiduMap;

    QuickAdapter<WorkFlowTask> taskAdapter;
    private int gcID;
    Gson gson;

    GameClubOrder curEntity;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.gcorder_detail_tasks)
    LoadMoreListView taskslist;

    @BindView(R.id.gcorder_detail_map)
    MapView map;

    @BindView(R.id.gcorder_detail_clubname)
    TextView mClubname;

    @BindView(R.id.gcorder_detail_responseperson)
    TextView mResponseperson;

    @BindView(R.id.gcorder_detail_tel)
    TextView mTel;

    @BindView(R.id.gcorder_detail_addr)
    TextView mAddr;

    @BindView(R.id.gcorder_detail_step)
    TextView mStep;

    @BindView(R.id.gcorder_detail_clubamount)
    TextView mClubamount;

    @BindView(R.id.gcorder_detail_saleamount)
    TextView msaleamount;

    @BindView(R.id.gcorder_detail_marketingcost)
    TextView mMarketingcost;

    @BindView(R.id.gcorder_detail_discount)
    TextView mdiscount;

    @BindView(R.id.gcorder_detail_realamount)
    TextView mRealamount;

    @BindView(R.id.gcorder_detail_shenhe)
    LinearLayout shenhe;

    @BindView(R.id.gcorder_detail_submit)
    Button submitBtn;

    @BindView(R.id.gcorder_detail_back)
    Button backBtn;

    @OnClick(R.id.gcorder_detail_submit)
    void submit() {
        new AlertView("是否审核", null, "取消", null,
                new String[]{"确认"},
                this, AlertView.Style.Alert, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                if (position > -1) {
                    showProgress();
                    backBtn.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.GONE);
                    Submit();
                }
            }
        }).show();
    }

    @OnClick(R.id.gcorder_detail_back)
    void back() {
        new AlertView("是否退回", null, "取消", null,
                new String[]{"确认"},
                this, AlertView.Style.Alert, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                if (position > -1) {
                    showProgress();
                    backBtn.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.GONE);
                    Back();
                }
            }
        }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gcorder_detail_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("电竞馆订单详情");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        init();

    }

    public void init() {
        gson = new Gson();
        Intent intent = getIntent();
        curEntity = (GameClubOrder) intent.getSerializableExtra("entity");
        gcID = curEntity.getID();

        inittask();
        getGameClub(curEntity.getGameClubID());

        mBaiduMap = map.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));

        String step = "<font color='red'>" + curEntity.getStateName() + "</font>";
        if (curEntity.getState() > 17) {
            step = step.replace("red", "gray");
        } else {
            if (StringUtils.FindString(curEntity.getLastReceiveId(), SharedPreferences.getInstance().getInt("UserID", 0) + "") > -1) {
                submitBtn.setVisibility(View.VISIBLE);
                shenhe.setVisibility(View.VISIBLE);
                if (curEntity.getState() > 0) backBtn.setVisibility(View.VISIBLE);
            }
        }
        mStep.setText(Html.fromHtml(step));
        mClubname.setText(curEntity.getGameClubName());
        mClubamount.setText(Html.fromHtml("电竞馆总价:" + StringUtils.ColorString(curEntity.getClubAmount(), "red")));
        msaleamount.setText(Html.fromHtml("运营商总价:" + StringUtils.ColorString(curEntity.getSaleAmount(), "red")));
        mMarketingcost.setText("推广费:" + curEntity.getMarketingCost());
        mdiscount.setText("整单直减:" + curEntity.getDiscount());
        if (curEntity.getDirectOrder() == 1) {
            mRealamount.setText(Html.fromHtml("运营商总应收:" + StringUtils.ColorString(""+(Float.parseFloat(curEntity.getSaleAmount()) - (Float.parseFloat(curEntity.getDiscount()))),"red")));
        } else {
            mRealamount.setText(Html.fromHtml("电竞馆总应收:" + StringUtils.ColorString(""+(Float.parseFloat(curEntity.getClubAmount()) - (Float.parseFloat(curEntity.getDiscount()))),"red") + "直接订单"));
        }
    }

    public void Submit() {
        OkGo.<String>post(HttpUrl.Url.GbOoderSubmit)
                .params("id", "" + curEntity.getID())
                .params("comment", "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            showToast(result.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    public void Back() {
        OkGo.<String>post(HttpUrl.Url.GbOrderBack)
                .params("id", "" + curEntity.getID())
                .params("comment", "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            showToast(result.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    public void getGameClub(int ID) {
        OkGo.<String>post(HttpUrl.Url.GameClub)
                .params("ID", "" + ID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            return;
                        }
                        GameClub club = gson.fromJson(result.getData().toString(), GameClub.class);
                        if (!club.getLatitude().equals("0")) {
                            LatLng point = new LatLng(Double.valueOf(club.getLatitude()), Double.valueOf(club.getLongitude()));
                            BitmapDescriptor bitmap = null;
                            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.gps);
                            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                            mBaiduMap.addOverlay(option);
                            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
                        }
                        mResponseperson.setText(club.getResponsePerson());
                        mTel.setText(club.getResponsePersonPhone());
                        mAddr.setText(club.getAddress());
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    public void getTasks() {
        OkGo.<String>post(HttpUrl.Url.WORKTASKLIST)
                .params("formtype", "newgameclub")
                .params("instanceid", gcID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            return;
                        }
                        List<WorkFlowTask> list = JSONArray.parseArray(result.getData().toString(), WorkFlowTask.class);
                        taskAdapter.clear();
                        taskAdapter.addAll(list);
                        setTaskHeight();
                        taskslist.updateLoadMoreViewText(list);
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    //任务
    public void inittask() {
        //tasks
        taskAdapter = WorkFlowTaskAdapter.getAdapter(this);
        taskslist.setDrawingCacheEnabled(true);
        taskslist.setAdapter(taskAdapter);
        getTasks();
    }

    private void setTaskHeight() {
        int totalHeight = 0;
        for (int i = 0; i < taskAdapter.getCount(); i++) {
            View listItem = taskAdapter.getView(i, null, taskslist);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = taskslist.getLayoutParams();
        params.height = totalHeight
                + (taskslist.getDividerHeight() * (taskAdapter.getCount() - 1));
        taskslist.setLayoutParams(params);
    }
}
