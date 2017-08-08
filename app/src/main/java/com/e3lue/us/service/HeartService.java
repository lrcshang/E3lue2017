package com.e3lue.us.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.e3lue.us.model.HttpUrl;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;


/**
 * Created by Leo on 2017/5/7.
 */

public class HeartService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public void onCreate() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO 自动生成的方法存根


        OkGo.<String>get(HttpUrl.Url.Heart)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.i("D", response.body());
                    }
                });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO 自动生成的方法存根

    }
}
