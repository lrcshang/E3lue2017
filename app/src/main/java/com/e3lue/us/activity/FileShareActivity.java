package com.e3lue.us.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.e3lue.us.R;
import com.e3lue.us.adapter.DownloadAdapter;
import com.e3lue.us.adapter.DownloadAdapter_1;
import com.e3lue.us.callback.MyBackChickListener;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.http.Pingutil;
import com.e3lue.us.model.FileShare;
import com.e3lue.us.model.FileShares;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.service.DownloadService;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.CheckFile;
import com.e3lue.us.utils.SharedPreferences;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.Calendar;
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
    List<FileShares> fileRes;
    MyBackChickListener listener;
    Boolean isback = false;
    String msg;
    public static final String ACTION = "com.e3lue.us.activity.FileShareActivity";
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0x01) {
                String txt = (String) msg.obj;
                directory.setText(directory.getText() + txt + ">");
            } else if (msg.what == 0x02) {
                String str = directory.getText().toString();
                str = str.substring(0, str.length() - 1);
                str = str.replace(str.substring(str.lastIndexOf(">") + 1), "");
                directory.setText(str);
            } else if (msg.what == 0x03) {
                alldownoad.setText("下载中");
                alldownoad.setEnabled(false);
            } else if (msg.what == 0x04) {
                alldownoad.setText("已全部下载");
                alldownoad.setEnabled(false);
            } else if (msg.what == 0x05) {
                if (alldownoad.getText().equals("下载中"))
                    return;
                alldownoad.setText("全部下载");
                alldownoad.setEnabled(true);
            } else if (msg.what == 0x06) {
                Boolean txt = (Boolean) msg.obj;
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                if (txt) {
                    alldownoad.setText("下载中");
                    alldownoad.setEnabled(false);
                    adapter.vh.downAllFile();
                } else {
                    if (hour > 20 || hour < 6) {
                        alldownoad.setText("下载中");
                        alldownoad.setEnabled(false);
                        adapter.vh.downAllFile();
                    } else {
                        Toast.makeText(FileShareActivity.this, "请晚上9点后下载文件", Toast.LENGTH_SHORT).show();
                    }
                }
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
        alldownoad.setText("等待加载完成");
        alldownoad.setEnabled(false);
        directory.setText("目录>");
        fileRes = new ArrayList<>();
        adapter = new DownloadAdapter_1(FileShareActivity.this);
        alldownoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    isback = false;
                    return;
                }
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                Log.i("xinxi", hour + ":  " + minute);
                Pingutil.ping("192.168.8.8", 2, FileShareActivity.this);
            }
        });
//        getData(HttpUrl.Url.FileShareList);
        getData(HttpUrl.Url.FileShareLists);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        list.setAdapter(adapter);
//        FileDownloader.registerDownloadStatusListener(adapter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(adapter.myReceiver, filter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            Toast.makeText(this, "当前无网络连接，请检查网络", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            Toast.makeText(this, "当前网络为数据网络，未下载任务", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    public void getData(final String URL) {
        OkGo.<String>post(URL)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        if (HttpUrl.Url.FileShareAllow.equals(URL)) {
                            msg = response.body().toString();
                            if (msg.contains("true")) {
                                Log.i("xinxi", response.body().toString());
                            }
                        } else {
                            JsonResult r = HttpClient.GetResult(response.body());
                            if (r.getRet() == 0 && r.getData() != null) {
                                Log.i("xinxi", r.getData().toString());
                                isback = true;
                                fileRes = JSONArray.parseArray(r.getData().toString(), FileShares.class);
                                adapter.setFileRess(fileRes, 0);
                            }
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        isback = false;
                        progressDimss();
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.unRegister();
//        Intent intent = new Intent(this, DownloadService.class);
//        stopService(intent);
        unregisterReceiver(adapter.myReceiver);
//        FileDownloader.unregisterDownloadStatusListener(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isback) {
                if (fileRes.size() <= 0) {
                    finish();
                }
                listener.onBackChick();
            } else {
                finish();
            }
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
