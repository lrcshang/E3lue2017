package com.e3lue.us.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.adapter.DownloadAdapter;
import com.e3lue.us.adapter.DownloadAdapter_1;
import com.e3lue.us.callback.MyBackChickListener;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.FileShare;
import com.e3lue.us.model.FileShares;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.CheckFile;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import org.wlf.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Leo on 2017/6/23.
 */

public class FileShareActivity extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.file_share_list)
    RecyclerView list;
    @BindView(R.id.text)
    TextView directory;
    @BindView(R.id.alldownoad)
    TextView alldownoad;
    private DownloadAdapter_1 adapter;
    List<FileShare> filelists;
    List<FileShares> fileRes;
    CheckFile checkFile;//检查本地是否存在类
    MyBackChickListener listener;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0x01) {
                String txt = (String) msg.obj;
                directory.setText(directory.getText() + txt + ">");
            }else if (msg.what==0x02){
                String str=directory.getText().toString();
                str=str.substring(0,str.length() - 1);
                str=str.replace(str.substring(str.lastIndexOf(">")+1),"");
                directory.setText(str);
            }
        }
    };

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

        alldownoad.setText("下载");
        directory.setText("目录>");
        filelists = new ArrayList<>();
        fileRes = new ArrayList<>();
        adapter = new DownloadAdapter_1(FileShareActivity.this);
        alldownoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.vh.downAllFile();
            }
        });
        getData(HttpUrl.Url.FileShareList);
        getData(HttpUrl.Url.FileShareLists);
        checkFile = new CheckFile(FileShareActivity.this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        list.setAdapter(adapter);
        FileDownloader.registerDownloadStatusListener(adapter);
    }

    public void getData(final String URL) {
        OkGo.<String>post(URL)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        JsonResult r = HttpClient.GetResult(response.body());
                        if (r.getRet() == 0 && r.getData() != null) {
//                            List<FileShare> list

                            if (URL.equals(HttpUrl.Url.FileShareList)) {
                                filelists = JSONArray.parseArray(r.getData().toString(), FileShare.class);
//                                adapter.setFilelists(filelists);
                            } else {
                                Log.i("xinxi", r.getData().toString());
                                fileRes = JSONArray.parseArray(r.getData().toString(), FileShares.class);
                                adapter.setFileRess(fileRes, 0);
                            }
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
        FileDownloader.unregisterDownloadStatusListener(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            listener.onBackChick();
        }
        return true;
    }

    public void setBackChickListener(MyBackChickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
