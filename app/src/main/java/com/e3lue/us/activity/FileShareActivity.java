package com.e3lue.us.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.adapter.DownloadAdapter;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.FileShare;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.CheckFile;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Leo on 2017/6/23.
 */

public class FileShareActivity extends SwipeBackActivity  {

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.file_share_list)
    RecyclerView list;

    private DownloadAdapter adapter;
    List<FileShare> filelists;
    CheckFile checkFile;//检查本地是否存在类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_share_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("文件共享资源");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        filelists = new ArrayList<>();
        adapter = new DownloadAdapter(FileShareActivity.this, filelists);
        getData();
        checkFile = new CheckFile(FileShareActivity.this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        list.setAdapter(adapter);
    }

    public void getData() {
        OkGo.<String>post(HttpUrl.Url.FileShareList)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        JsonResult r = HttpClient.GetResult(response.body());
                        if (r.getRet() == 0 && r.getData() != null) {
//                            List<FileShare> list
                            filelists = JSONArray.parseArray(r.getData().toString(), FileShare.class);
                            adapter.setFilelists(filelists);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        progressDimss();
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.unRegister();
    }
    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
