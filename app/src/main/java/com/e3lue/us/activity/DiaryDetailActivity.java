package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.adapter.ImageShowAdapter;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.Diary;
import com.e3lue.us.model.DiaryMessage;
import com.e3lue.us.model.Expenses;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.BaseAdapterHelper;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.BitmapUtil;
import com.e3lue.us.utils.DateUtil;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import me.kareluo.ui.PopupView;
import okhttp3.Call;



/**
 * Created by Leo on 2017/4/11.
 */

public class DiaryDetailActivity extends SwipeBackActivity implements ImageShowAdapter.OnRecyclerViewItemClickListener {

    ArrayList<String> imagelist;
    QuickAdapter<Expenses> expensesAdapter;
    QuickAdapter<DiaryMessage> messageAdapter;

    private int diaryID;
    Gson gson;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnMore)
    ImageView mornBtn;

    @BindView(R.id.diary_detail_recyclerView)
    RecyclerView imagelistview;

    @BindView(R.id.diary_detail_expenses)
    TextView expenses;

    @BindView(R.id.diary_detail_date)
    TextView date;

    @BindView(R.id.diary_detail_company)
    TextView company;

    @BindView(R.id.diary_detail_person)
    TextView person;

    @BindView(R.id.diary_detail_info)
    TextView info;

    @BindView(R.id.diary_detail_expenseslist)
    LoadMoreListView expenseslist;

    @BindView(R.id.diary_detail_messagelist)
    LoadMoreListView messagelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_detail_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("日记");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mornBtn.setVisibility(View.VISIBLE);
        mornBtn.setOnClickListener(menuListener);

        init();
    }

    public void init() {
        gson = new Gson();
        Intent intent = getIntent();
        diaryID = intent.getIntExtra("ID", 0);
        if (diaryID == 0) {
            Diary entity = (Diary) intent.getSerializableExtra("entity");
            if (entity == null) {
                showToast("参数错误");
                finish();
            }
            diaryID = entity.getID();
        }

        //费用
        expensesAdapter = new QuickAdapter<Expenses>(this, R.layout.expenses_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, Expenses entity) {
                helper.setText(R.id.expenses_list_item_type, entity.getType1())
                        .setText(R.id.expenses_list_item_date, entity.getCreateTime())
                        .setText(R.id.expenses_list_item_amount, entity.getAmount() + "")
                        .setText(R.id.expenses_list_item_statename, entity.getNote());
            }
        };
        expenseslist.setAdapter(expensesAdapter);

        //留言
        messageAdapter = new QuickAdapter<DiaryMessage>(this, R.layout.diary_detail_message_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, DiaryMessage entity) {
                helper.setText(R.id.message_item_date, entity.getCreateTime())
                        .setText(R.id.message_item_username, entity.getUserName())
                        .setText(R.id.message_item_msg, entity.getMessage() + "");
            }
        };
        messagelist.setAdapter(messageAdapter);

        get();
        getExpenses();
        getMessageList();
    }

    //获得日记
    public void get() {
        OkGo.<String>post(HttpUrl.Url.DiaryGet)
                .params("ID", diaryID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        JsonResult r = HttpClient.GetResult(response.body());
                        if (r.getRet() == 0) {
                            Diary entity = gson.fromJson(r.getData().toString(), Diary.class);
                            initData(entity);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        progressDimss();
                    }
                });
    }

    //显示日记
    public void initData(Diary entity) {
        String title = entity.getUserName();
        title += entity.getCompanyName().length() > 0 ? " 拜访 " + entity.getCompanyName() : " 拜访 " + entity.getContactPersonName();
        String cname = entity.getCompanyName().trim().length() == 0 ? "(新客户)" : entity.getCompanyName();
        info.setText(entity.getDiaryContent());
        textHeadTitle.setText(title);
        date.setText(DateUtil.strToDateShort(entity.getCreateDate()));
        company.setText(cname);
        person.setText(entity.getContactPersonName());

        if (entity.getPicture().trim().length() == 0) {
            imagelistview.setVisibility(View.GONE);
            return;
        }

        imagelist = new ArrayList<String>();
        String[] imgs = entity.getPicture().split(",");
        for (int i = 0; i < imgs.length; i++) {
            imagelist.add(BitmapUtil.ImgPath("Diary", entity.getDateStr(), imgs[i]));
        }
        ImageShowAdapter adapter = new ImageShowAdapter(imagelist, this);
        adapter.setOnItemClickListener(this);
        imagelistview.setLayoutManager(new GridLayoutManager(this, 4));
        imagelistview.setHasFixedSize(true);
        imagelistview.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putStringArrayListExtra("list", imagelist);
        intent.putExtra("position", position);
        startActivity(intent);
    }


    public void getMessageList() {
        OkGo.<String>post(HttpUrl.Url.DiaryMessageList)
                .params("DiaryID", diaryID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            return;
                        }
                        List<DiaryMessage> list = JSONArray.parseArray(result.getData().toString(), DiaryMessage.class);
                        float amount = 0;
                        if (list == null) list = new ArrayList<DiaryMessage>();

                        messageAdapter.clear();
                        messageAdapter.addAll(list);
                        messagelist.updateLoadMoreViewText(list);
                        setMessageHeight();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        //progressDimss();
                        //showToast("网络好像开小差了,请重试");
                    }
                });
    }

    public void getExpenses() {
        OkGo.<String>post(HttpUrl.Url.ExpensesListByDiary)
                .params("id", diaryID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            return;
                        }
                        List<Expenses> list = JSONArray.parseArray(result.getData().toString(), Expenses.class);
                        float amount = 0;
                        if (list == null) list = new ArrayList<Expenses>();

                        for (Expenses item : list) {
                            amount += item.getAmount();
                        }
                        expenses.setText(Html.fromHtml("<font color='red'>$" + amount + "</font>"));

                        expensesAdapter.clear();
                        expensesAdapter.addAll(list);
                        expenseslist.updateLoadMoreViewText(list);
                        setExpensesHeight();
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    private void setMessageHeight() {
        int totalHeight = 0;
        for (int i = 0; i < messageAdapter.getCount(); i++) {
            View listItem = messageAdapter.getView(i, null, messagelist);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = messagelist.getLayoutParams();
        params.height = totalHeight
                + (messagelist.getDividerHeight() * (messageAdapter.getCount() - 1));
        messagelist.setLayoutParams(params);
    }

    private void setExpensesHeight() {
        int totalHeight = 0;
        for (int i = 0; i < expensesAdapter.getCount(); i++) {
            View listItem = expensesAdapter.getView(i, null, expenseslist);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = expenseslist.getLayoutParams();
        params.height = totalHeight
                + (expenseslist.getDividerHeight() * (expensesAdapter.getCount() - 1));
        expenseslist.setLayoutParams(params);
    }

    View.OnClickListener menuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenuView menuView = new PopupMenuView(DiaryDetailActivity.this);
            menuView.setMenuItems(Arrays.asList(
                    new OptionMenu("留言")));
            menuView.setOnMenuClickListener(menuClickListener);
            menuView.setSites(PopupView.SITE_BOTTOM);
            menuView.show(mornBtn);
        }
    };

    OptionMenuView.OnOptionMenuClickListener menuClickListener = new OptionMenuView.OnOptionMenuClickListener() {
        @Override
        public boolean onOptionMenuClick(int position, OptionMenu menu) {
            Intent intent = new Intent();
            intent.setClass(DiaryDetailActivity.this, DiaryMessageAddActivity.class);
            intent.putExtra("ID", diaryID);
            startActivityForResult(intent, 100);
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getMessageList();
    }

}
