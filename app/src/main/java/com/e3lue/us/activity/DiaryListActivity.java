package com.e3lue.us.activity;

import android.os.Bundle;
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
import com.e3lue.us.model.DiaryDay;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.BaseAdapterHelper;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.DateUtil;
import com.e3lue.us.utils.DeviceUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.squareup.picasso.Picasso;
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
 * Created by Leo on 2017/4/11.
 */

public class DiaryListActivity extends SwipeBackActivity {

    private DiaryListActivity context;

    private int pno = 1;
    QuickAdapter<DiaryDay> adapter;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.diary_list_header)
    PtrClassicFrameLayout mPtrFrame;

    @BindView(R.id.diary_list_listview)
    LoadMoreListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diray_list_activity);
        ButterKnife.bind(this);
        context = this;

        textHeadTitle.setText("我的日记");
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
    }

    public void initView() {

        adapter = new QuickAdapter<DiaryDay>(context, R.layout.diary_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, DiaryDay entity) {
                helper.setText(R.id.diary_item_date, "" + DateUtil.strToDateShort(entity.getCreateDate()))
                        .setText(R.id.diary_item_count, "拜访公司：" + entity.getCompanyCount() + "家")
                        .setText(R.id.diary_item_spend, "行程费用：" + entity.getSpend());
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
                DiaryDay entity = adapter.getItem(i);
                UIHelper.showDiaryDay(context, 1, entity.getCreateDate(), 0);
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

    public void loadData() {
        OkGo.<String>post(HttpUrl.Url.DiaryList)
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
                        List<DiaryDay> list = JSONArray.parseArray(result.getData().toString(), DiaryDay.class);
                        if (list == null || list.size() == 0) {
                            showToast("已经是最后一页了");
                        }
                        if (pno == 1) {
                            adapter.clear();
                        }
                        adapter.addAll(list);
                        pno++;
                        listView.updateLoadMoreViewText(list);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        mPtrFrame.refreshComplete();
                    }
                });
    }

}
