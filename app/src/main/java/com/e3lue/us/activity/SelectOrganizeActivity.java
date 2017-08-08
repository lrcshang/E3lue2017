package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.adapter.TreeAdapter2;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.BaseOrganize;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.kanade.treeadapter.Node;
import com.kanade.treeadapter.TreeItemClickListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Leo on 2017/5/31.
 */

public class SelectOrganizeActivity extends SwipeBackActivity {

    TreeAdapter2<BaseOrganize> adapter;
    List<BaseOrganize> list;
    private int id = 0;
    private String name = "";

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.organize_select_name)
    TextView title;

    @BindView(R.id.select_organize_listview)
    RecyclerView listview;

    @OnClick(R.id.organzie_select_btn)
    void click() {
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        setResult(100, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organize_select_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("选择机构");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initview();
        getdata();
    }

    public void initview() {
        list = new ArrayList<>();
        adapter = new TreeAdapter2<>(this);
        adapter.setNodes(list);
        adapter.setListener(new TreeItemClickListener() {
            @Override
            public void OnClick(Node node) {
                title.setText("已选:" + node.getName());
                name = node.getName();
                id = node.getId();
            }
        });

        listview.setLayoutManager(new LinearLayoutManager(this));
        listview.setAdapter(adapter);
    }

    private void getdata() {
        OkGo.<String>post(HttpUrl.Url.OrganizeCharge)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            return;
                        }
                        list = JSONArray.parseArray(result.getData().toString(), BaseOrganize.class);
                        if (list != null || list.size() > 0) {
                            adapter.setNodes(list);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        setResult(100, intent);
        finish();
    }
}
