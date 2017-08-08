package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.e3lue.us.R;
import com.e3lue.us.adapter.ImageShowAdapter;
import com.e3lue.us.adapter.WorkFlowTaskAdapter;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.Expenses;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.model.WorkFlowTask;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.BitmapUtil;
import com.e3lue.us.utils.SharedPreferences;
import com.e3lue.us.utils.StringUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Leo on 2017/4/11.
 */

public class ExpensesDetailActivity extends SwipeBackActivity implements ImageShowAdapter.OnRecyclerViewItemClickListener {

    private ArrayList<String> imagelist;
    QuickAdapter<WorkFlowTask> taskAdapter;
    private int expensesID;
    private Expenses curEntity;
    Gson gson;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.expenses_detail_recyclerView)
    RecyclerView imagelistview;

    @BindView(R.id.expenses_detail_tasks)
    LoadMoreListView taskslist;

    @BindView(R.id.expenses_detail_amount)
    TextView amount;

    @BindView(R.id.expenses_detail_amount_edit)
    EditText amount_edit;

    @BindView(R.id.expenses_detail_note)
    TextView note;

    @BindView(R.id.expenses_detail_note_edit)
    EditText note_edit;

    @BindView(R.id.expenses_detail_comment)
    EditText comment;

    @BindView(R.id.expenses_detail_save)
    Button saveBtn;

    @BindView(R.id.expenses_detail_submit)
    Button submitBtn;

    @BindView(R.id.expenses_detail_back)
    Button backBtn;

    @OnClick(R.id.expenses_detail_save)
    void save() {
        new AlertView("是否修改", null, "取消", null,
                new String[]{"确认"},
                this, AlertView.Style.Alert, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                if (position > -1) {
                    showProgress();
                    saveBtn.setEnabled(false);
                    submitBtn.setEnabled(false);
                    Modify();
                }
            }
        }).show();
    }

    @OnClick(R.id.expenses_detail_submit)
    void submit() {
        if (curEntity.getState() == 0 && curEntity.getDiaryID() == 0 && curEntity.getType1().equals("差旅")) {
            showToast("差旅费用需关联日记，写日记时自动审核");
            return;
        }
        new AlertView("是否审核", null, "取消", null,
                new String[]{"确认"},
                this, AlertView.Style.Alert, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                if (position > -1) {
                    showProgress();
                    saveBtn.setVisibility(View.GONE);
                    backBtn.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.GONE);
                    Submit();
                }
            }
        }).show();
    }

    @OnClick(R.id.expenses_detail_back)
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
        setContentView(R.layout.expenses_detail_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("报销单详情");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initView();
        inittask();
    }

    public void initView() {
        gson = new Gson();
        Intent intent = getIntent();
        expensesID = intent.getIntExtra("ID", 0);
        if (expensesID == 0) {
            Expenses entity = (Expenses) intent.getSerializableExtra("entity");
            if (entity == null) {
                showToast("参数错误");
                finish();
            }
            expensesID = entity.getID();
        }
        get();
    }

    public void setData(Expenses entity) {
        //处理模式
        if (StringUtils.FindString(entity.getLastReceiveId(), SharedPreferences.getInstance().getInt("UserID", 0) + "") > -1) {
            submitBtn.setVisibility(View.VISIBLE);
            if (entity.getState() > 0) {
                backBtn.setVisibility(View.VISIBLE);
                comment.setVisibility(View.VISIBLE);
                amount.setVisibility(View.VISIBLE);
                note.setVisibility(View.VISIBLE);
                amount.setText("" + entity.getAmount());
                note.setText(entity.getNote());
            }

            if (entity.getState() == 0) {
                saveBtn.setVisibility(View.VISIBLE);
                amount_edit.setVisibility(View.VISIBLE);
                note_edit.setVisibility(View.VISIBLE);
                amount_edit.setText("" + entity.getAmount());
                note_edit.setText(entity.getNote());
            }
        } else { //察看模式
            amount.setVisibility(View.VISIBLE);
            note.setVisibility(View.VISIBLE);
            amount.setText("" + entity.getAmount());
            note.setText(entity.getNote());
        }

        imagelist = new ArrayList<String>();
        String[] imgs = entity.getPicture().split(",");
        for (int i = 0; i < imgs.length; i++) {
            imagelist.add(BitmapUtil.ImgPath("Expenses", entity.getDateStr(), imgs[i]));
            //imagelist.add(HttpUrl.Url.BASIC + "/userfiles/Expenses/" + entity.getDateStr() + "/source/" + imgs[i]);
        }
        ImageShowAdapter adapter = new ImageShowAdapter(imagelist, this);
        adapter.setOnItemClickListener(this);
        imagelistview.setLayoutManager(new GridLayoutManager(this, 4));
        imagelistview.setHasFixedSize(true);
        imagelistview.setAdapter(adapter);
    }

    public void Modify() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Amount", amount_edit.getText().toString());
        params.put("Note", note_edit.getText().toString());
        params.put("ID", "" + expensesID);

        OkGo.<String>post(HttpUrl.Url.ExpensesModify)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        JsonResult r = HttpClient.GetResult(response.body());
                        if (r.getRet() == 0) {
                            saveBtn.setEnabled(true);
                            submitBtn.setEnabled(true);
                        }
                        showToast(r.getMsg());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        progressDimss();
                        saveBtn.setEnabled(true);
                        submitBtn.setEnabled(true);
                        showToast("网络好像开小差了,请重试");
                    }
                });
    }

    public void Submit() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Comment", comment.getText().toString());
        params.put("ID", "" + expensesID);

        OkGo.<String>post(HttpUrl.Url.ExpensesSubmit)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        JsonResult r = HttpClient.GetResult(response.body());
                        if (r.getRet() == 0) {
                            getTasks();
                        }
                        showToast(r.getMsg());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        progressDimss();
                        backBtn.setEnabled(true);
                        submitBtn.setEnabled(true);
                        showToast("网络好像开小差了,请重试");
                    }
                });
    }

    public void Back() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Comment", comment.getText().toString());
        params.put("ID", "" + expensesID);

        OkGo.<String>post(HttpUrl.Url.ExpensesBack)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        JsonResult r = HttpClient.GetResult(response.body());
                        if (r.getRet() == 0) {
                            getTasks();
                        }
                        showToast(r.getMsg());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        progressDimss();
                        saveBtn.setEnabled(true);
                        submitBtn.setEnabled(true);
                        showToast("网络好像开小差了,请重试");
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putStringArrayListExtra("list", imagelist);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void get() {
        OkGo.<String>post(HttpUrl.Url.ExpensesGet)
                .params("ID", expensesID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            return;
                        }
                        curEntity = gson.fromJson(result.getData().toString(), Expenses.class);
                        setData(curEntity);
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

    public void getTasks() {
        OkGo.<String>post(HttpUrl.Url.WORKTASKLIST)
                .params("formtype", "expenses")
                .params("instanceid", expensesID)
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
