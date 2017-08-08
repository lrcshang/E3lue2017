package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.BaseUser;
import com.e3lue.us.model.Diary;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.BaseAdapterHelper;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.DateUtil;
import com.e3lue.us.utils.SharedPreferences;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Leo on 2017/5/15.
 */

public class DiaryDayActivity extends SwipeBackActivity {

    private DiaryDayActivity context;
    private boolean isFirst = true;

    QuickAdapter<Diary> adapter;
    String whichday;
    int user = 0;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.diary_day_listview)
    LoadMoreListView listView;

    @BindView(R.id.diary_day_add)
    Button addBtn;

    @OnClick(R.id.diary_day_add)
    public void save() {
        addBtn.setEnabled(false);
        Intent intent = new Intent();
        intent.setClass(this, DiaryNewActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_day_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("日记内容");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int isnew = getIntent().getIntExtra("isnew", -1);
        if (isnew == 0) {
            addBtn.setVisibility(View.VISIBLE);
            whichday = DateUtil.getNowDateShort();
            textHeadTitle.setText("撰写" + whichday + "日记");
        } else if (isnew == 1) {
            String date = getIntent().getStringExtra("date");
            user = getIntent().getIntExtra("user", 0);
            Gson gson = new Gson();
            BaseUser curuser = gson.fromJson(SharedPreferences.getInstance().getString("BaseUser", ""), BaseUser.class);
            whichday = DateUtil.strToDateShort(date);
            textHeadTitle.setText(whichday + "的日记");
            if (curuser.getUserId() == user && DateUtil.getNowDateShort().equals(whichday)) {
                addBtn.setVisibility(View.VISIBLE);
            }
            isFirst = false;
            initView();
            loadData();
        }
    }

    public void initView() {
        context = DiaryDayActivity.this;
        adapter = new QuickAdapter<Diary>(context, R.layout.diary_day_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, Diary entity) {
                String content = "";
                String cname = "新客户";
                if (entity.getDiaryContent().length() > 30) {
                    content = entity.getDiaryContent().substring(0, 30) + "...(更多内容)";
                } else {
                    content = entity.getDiaryContent();
                }
                if (entity.getCompanyName().length() > 0) cname = entity.getCompanyName();
                helper.setText(R.id.diary_item_date, "公司：" + cname)
                        .setText(R.id.diary_item_count, entity.getUserName() + " 拜访 " + entity.getContactPersonName())
                        .setText(R.id.diary_item_spend, "费用：<font color='red'>" + entity.getSpend() + "</font>")
                        .setText(R.id.diary_item_note, "简摘:<font color='gray'>" + content + "</font>");
            }
        };

        listView.setDrawingCacheEnabled(true);
        listView.setAdapter(adapter);

        // 点击事件
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= adapter.getCount()) return;
                Diary entity = adapter.getItem(i);
                UIHelper.showDiaryDetail(context, entity);
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

    public void loadData() {
        OkGo.<String>post(HttpUrl.Url.DiaryOneDay)
                .params("date", whichday)
                .params("user", user)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<Diary> list = JSONArray.parseArray(result.getData().toString(), Diary.class);
                        if (list == null || list.size() == 0) {
                            //showToast("已经是最后一页了");
                        }
                        adapter.clear();
                        adapter.addAll(list);
                        listView.updateLoadMoreViewText(list);
                        setHeight();
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    private void setHeight() {
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isFirst) {
            isFirst = false;
            initView();
        }
        addBtn.setEnabled(true);
        loadData();
    }

}
