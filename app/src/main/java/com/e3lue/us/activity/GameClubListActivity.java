package com.e3lue.us.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.GameClubOrder;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.BaseAdapterHelper;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.DeviceUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import me.kareluo.ui.PopupView;
import okhttp3.Call;

/**
 * Created by Leo on 2017/5/2.
 */

public class GameClubListActivity extends SwipeBackActivity {

    private GameClubListActivity context;

    private int pno = 1;
    QuickAdapter<GameClubOrder> adapter;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnMore)
    ImageView mornBtn;

    @BindView(R.id.gc_list_header)
    PtrClassicFrameLayout mPtrFrame;

    @BindView(R.id.gc_list_listview)
    LoadMoreListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc_list_activity);
        ButterKnife.bind(this);
        context = this;

        textHeadTitle.setText("电竞馆订单表");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mornBtn.setVisibility(View.VISIBLE);
        mornBtn.setOnClickListener(menuListener);

        initData();
        initView();
        loadData();
    }

    public void initView() {

        adapter = new QuickAdapter<GameClubOrder>(context, R.layout.gc_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, GameClubOrder entity) {
                String clubname = entity.getGameClubName() + "&#160;<font color='gray'>(" + entity.getDocumentMaker() + ")</font>";
                String note = entity.getState() < 17 ? "<font color='red'>" + entity.getStateName() + "</font>" : entity.getStateName();
                helper.setText(R.id.gc_list_item_company, entity.getCustomerName())
                        .setText(R.id.gc_list_item_clubname, clubname)
                        .setText(R.id.gc_list_item_date,entity.getCreateTime())
                        .setText(R.id.gc_list_item_step, note);
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
                GameClubOrder entity = adapter.getItem(i);
                UIHelper.showGcOrderDetail(context, entity);
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
        OkGo.<String>post(HttpUrl.Url.GbOrderList)
                .params("page", "" + pno)
                .params("keyword", "")
                .params("step", "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mPtrFrame.refreshComplete();
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<GameClubOrder> list = JSONArray.parseArray(result.getData().toString(), GameClubOrder.class);
                        if (list == null || list.size() == 0) {
                            showToast("已经是最后一页了");
                            list = new ArrayList<GameClubOrder>();
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

    View.OnClickListener menuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenuView menuView = new PopupMenuView(GameClubListActivity.this);
            menuView.setMenuItems(Arrays.asList(
                    new OptionMenu("新增")));
            menuView.setOnMenuClickListener(menuClickListener);
            menuView.setSites(PopupView.SITE_BOTTOM);
            menuView.show(mornBtn);
        }
    };

    OptionMenuView.OnOptionMenuClickListener menuClickListener = new OptionMenuView.OnOptionMenuClickListener() {
        @Override
        public boolean onOptionMenuClick(int position, OptionMenu menu) {
            UIHelper.showGcNew(GameClubListActivity.this);
            return false;
        }
    };
}
