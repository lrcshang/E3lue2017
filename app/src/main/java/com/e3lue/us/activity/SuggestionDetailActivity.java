package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.adapter.ImageShowAdapter;
import com.e3lue.us.adapter.WorkFlowTaskAdapter;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.model.Suggestions;
import com.e3lue.us.model.WorkFlowTask;
import com.e3lue.us.ui.loadmore.LoadMoreListView;
import com.e3lue.us.ui.quickadapter.QuickAdapter;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Leo on 2017/4/19.
 */

public class SuggestionDetailActivity extends SwipeBackActivity implements ImageShowAdapter.OnRecyclerViewItemClickListener {

    private ArrayList<String> imagelist;
    QuickAdapter<WorkFlowTask> taskAdapter;
    Gson gson;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.suggestion_detail_recyclerView)
    RecyclerView imagelistview;

    @BindView(R.id.suggestion_detail_tasks)
    LoadMoreListView taskslist;


    @BindView(R.id.suggestion_detail_info)
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion_detail_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("建议意见详情");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Suggestions entity = (Suggestions) getIntent().getSerializableExtra("entity");
        init(entity);
        inittask();
        getTasks(entity.getID());
    }

    public void init(Suggestions entity) {
        imagelist = new ArrayList<String>();
        String[] imgs = entity.getPicture().split(",");
        for (int i = 0; i < imgs.length; i++) {
            imagelist.add(HttpUrl.Url.BASIC + "/userfiles/Suggestions/" + entity.getDateStr() + "/source/" + imgs[i]);
        }
        ImageShowAdapter adapter = new ImageShowAdapter(imagelist, this);
        adapter.setOnItemClickListener(this);
        imagelistview.setLayoutManager(new GridLayoutManager(this, 4));
        imagelistview.setHasFixedSize(true);
        imagelistview.setAdapter(adapter);

        content.setText(entity.getInfo());
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putStringArrayListExtra("list", imagelist);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void inittask() {
        //tasks
        taskAdapter = WorkFlowTaskAdapter.getAdapter(this);
        taskslist.setDrawingCacheEnabled(true);
        taskslist.setAdapter(taskAdapter);
    }

    public void getTasks(int ID) {
        OkGo.<String>post(HttpUrl.Url.WORKTASKLIST)
                .params("formtype", "suggestion")
                .params("instanceid", ID)
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
