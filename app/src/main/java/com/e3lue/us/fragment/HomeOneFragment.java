package com.e3lue.us.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.activity.MainActivity;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.CheckIn;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.BaseAdapterHelper;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
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

//import com.e3lue.us.model.SearchShop;

/**
 * Created by Leo on 15/9/4.
 */
public class HomeOneFragment extends Fragment {
    private MainActivity context;

    private int pno = 1;
    private boolean isLoadAll;

    @BindView(R.id.rotate_header_list_view_frame)
    PtrClassicFrameLayout mPtrFrame;

    @BindView(R.id.homeone_listView)
    LoadMoreListView listView;
    QuickAdapter<CheckIn> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_one, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
        initData();
        initView();
        loadData();
    }

    void initView() {
        adapter = new QuickAdapter<CheckIn>(context, R.layout.checkin_fragment_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, CheckIn checkin) {
                helper.setText(R.id.checkin_item_date, checkin.getCreateTime())
                        .setText(R.id.checkin_item_user, checkin.getUserName())
                        .setText(R.id.checkin_item_addr, checkin.getAddress());
            }
        };
        listView.setDrawingCacheEnabled(true);
        listView.setAdapter(adapter);

        // header custom begin
        final StoreHouseHeader header = new StoreHouseHeader(context);
        header.setPadding(0, DeviceUtil.dp2px(context, 15), 0, 0);
        header.initWithString("E-3LUE");
        header.setTextColor(getResources().getColor(R.color.gray));
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        // header custom end

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
                // UIHelper.showHouseDetailActivity(context);
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

    public void initData()
    {
        pno=1;
    }

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
                        List<CheckIn> list = JSONArray.parseArray(result.getData().toString(), CheckIn.class);
                        if (list == null || list.size() == 0) {
                            Toast.makeText(context, "已经是最后一页了.", Toast.LENGTH_LONG).show();
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

    @Override
    public void onResume() {
        super.onResume();
        Picasso.with(context).resumeTag(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        Picasso.with(context).pauseTag(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with(context).cancelTag(context);
    }

}
