package com.e3lue.us.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.wlf.filedownloader.FileDownloader;

/**
 * Created by x-lrc on 2017/9/8.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext=context;
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
//                        showToast("当前WiFi连接");
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan
                        FileDownloader.pauseAll();
                        showToast("当前移动网络连接，已暂停下载任务");
                    }
                } else {
//                    Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                }

            } else {   // not connected to the internet
//                Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");


            }


        }
    }
    public void showToast(String info) {
        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
    }
}
