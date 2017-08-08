package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
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
import com.loonggg.bottomsheetpopupdialoglib.ShareBottomPopupDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.squareup.picasso.Picasso;

import java.util.Date;
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
 * Created by Leo on 2017/5/25.
 */

public class DiaryManaActivity extends SwipeBackActivity {

    private DiaryManaActivity context;

    private int pno = 1;
    QuickAdapter<DiaryDay> adapter;
    private String sdate = "";
    private String edate = "";
    private String keyword = "";
    private String organizeName="";
    private int organize = 0;
    private TextView morganize;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnMore)
    ImageView mornBtn;

    @BindView(R.id.diary_mana_root)
    LinearLayout root;

    @BindView(R.id.diary_mana_header)
    PtrClassicFrameLayout mPtrFrame;

    @BindView(R.id.diary_mana_listview)
    LoadMoreListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_mana_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("下属日记");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        context = this;

        mornBtn.setVisibility(View.VISIBLE);
        mornBtn.setOnClickListener(menuListener);

        initView();
        initData();
        loadData();
    }

    public void initView() {

        adapter = new QuickAdapter<DiaryDay>(context, R.layout.diary_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, DiaryDay entity) {
                String date = DateUtil.strToDateShort(entity.getCreateDate()) + " <font color='blue'>(" + entity.getUserName() + ")</font>";
                helper.setText(R.id.diary_item_date, date)
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
                UIHelper.showDiaryDay(context, 1, entity.getCreateDate(),entity.getUserID());
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
        OkGo.<String>post(HttpUrl.Url.DiaryManaList)
                .params("page", "" + pno)
                .params("sdate", sdate)
                .params("edate", edate)
                .params("keyword", keyword)
                .params("organize",organize)
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

    View.OnClickListener menuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View dialogView = getLayoutInflater().inflate(R.layout.diary_mana_search, null);
            final ShareBottomPopupDialog shareBottomPopupDialog = new ShareBottomPopupDialog(DiaryManaActivity.this, dialogView);
            shareBottomPopupDialog.showPopup(root);

            final TextView sdatev = (TextView) dialogView.findViewById(R.id.diary_mana_search_sdate);
            sdatev.setText(sdate);
            sdatev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatePickDialog dialog = new DatePickDialog(DiaryManaActivity.this);
                    dialog.setTitle("选择日期");
                    dialog.setType(DateType.TYPE_YMD);
                    dialog.setMessageFormat("yyyy-MM-dd");
                    dialog.setOnSureLisener(new OnSureLisener() {
                        @Override
                        public void onSure(Date date) {
                            sdatev.setText(DateUtil.DateToShort(date));
                        }
                    });
                    dialog.show();
                }
            });

            final TextView edatev = (TextView) dialogView.findViewById(R.id.diary_mana_search_edate);
            edatev.setText(edate);
            edatev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatePickDialog dialog = new DatePickDialog(DiaryManaActivity.this);
                    dialog.setTitle("选择日期");
                    dialog.setType(DateType.TYPE_YMD);
                    dialog.setMessageFormat("yyyy-MM-dd");
                    dialog.setOnSureLisener(new OnSureLisener() {
                        @Override
                        public void onSure(Date date) {
                            edatev.setText(DateUtil.DateToShort(date));
                        }
                    });
                    dialog.show();
                }
            });

            final EditText keywordv = (EditText) dialogView.findViewById(R.id.diary_mana_search_keyword);

            morganize = (TextView) dialogView.findViewById(R.id.diary_mana_search_organize);
            morganize.setText(organizeName);
            morganize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(DiaryManaActivity.this, SelectOrganizeActivity.class);
                    startActivityForResult(intent, 100);
                }
            });

            //查询
            Button close = (Button) dialogView.findViewById(R.id.diary_mana_search_submit);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareBottomPopupDialog.dismiss();
                    sdate = sdatev.getText().toString();
                    edate = edatev.getText().toString();
                    keyword = keywordv.getText().toString();
                    initData();
                    loadData();
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if(data==null)return;
            int id = data.getIntExtra("id", 0);
            String name = data.getStringExtra("name");
            if (id > 0) {
                organize = id;
                morganize.setText(name);
                organizeName = name;
            }
        }
    }
}
