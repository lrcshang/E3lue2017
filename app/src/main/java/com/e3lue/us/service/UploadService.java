package com.e3lue.us.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.e3lue.us.activity.CheckInDetailActivity;
import com.e3lue.us.fragment.MainPagerFragment;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.utils.SharedPreferences;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Enzate on 2017/7/17.
 */

public class UploadService extends Service {
    List<String> list;
    Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO 自动生成的方法存根
        return null;
    }

    File checkin_picture;

    public UploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        list = new ArrayList<String>();
    }

    public void uploadfile(final String ID) {
        OkGo.<String>post(HttpUrl.Url.UPLOADFILE)
                .params("CheckinID", ID)
                .params("operation", "CHECKIN")
                .params("file", checkin_picture)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.i("xinxi", ID);
                        showToast("图片上传完成");
                        List<String> list1 = SharedPreferences.getInstance().getDataList("ERRORID");
                        removeDuplicate(list1);
                        list1.remove(ID);
                        Log.i("xinxi", list1.size() + "");
                        SharedPreferences.getInstance().setDataList("ERRORID", list1);
                        intent = new Intent();
                        intent.setAction(MainPagerFragment.ACTION);
                        intent.setAction(CheckInDetailActivity.ACTION);
                        sendBroadcast(intent);
                        onDestroy();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        intent = new Intent(MainPagerFragment.ACTION);
                        List<String> list1 = SharedPreferences.getInstance().getDataList("ERRORID");
                        removeDuplicate(list1);
                        list.clear();
                        list.add(ID);
                        list.addAll(list1);
                        SharedPreferences.getInstance().setDataList("ERRORID", list);
                        sendBroadcast(intent);
                        onDestroy();
                    }

                    @Override
                    public void uploadProgress(Progress progress) {

                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), progress.currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), progress.totalSize);
                        String speed = Formatter.formatFileSize(getApplicationContext(), progress.speed);
//                        progressBar.setMax(10000);
//                        progressBar.setProgress((int) (progress.fraction * 10000));
                    }

                });
    }

    public void removeDuplicate(List<String> list1) {
        HashSet<String> set = new HashSet<String>(list1);
//        String[] titles = new String[size];
        list1.clear();
        list1.addAll(set);
    }


    public void showToast(String text) {
        Toast.makeText(UploadService.this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        File picture = new File(Environment.getExternalStorageDirectory()
                + "/temp.jpg");
        if (picture != null) {
            checkin_picture = new CompressHelper.Builder(UploadService.this)
                    .setMaxWidth(1280)
                    .setMaxHeight(960)
                    .setQuality(95)
                    .build()
                    .compressToFile(picture);
        }
        uploadfile(intent.getStringExtra("data"));
        super.onStart(intent, startId);
    }
}
