package com.e3lue.us.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.e3lue.us.activity.FileShareActivity;
import com.e3lue.us.utils.CheckFile;
import com.e3lue.us.utils.SharedPreferences;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;
import org.wlf.filedownloader.listener.OnRetryableFileDownloadStatusListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by x-lrc on 2017/8/31.
 */

public class DownloadService extends Service implements OnRetryableFileDownloadStatusListener {
    private List<DownloadFileInfo> mDownloadFileInfos = Collections.synchronizedList(new ArrayList<DownloadFileInfo>());
    List<String> urls;
    String Dtype = "";
    String filename = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 将当前service注册为FileDownloader下载状态监听器
        FileDownloader.registerDownloadStatusListener(DownloadService.this);
        new Thread() {
            @Override
            public void run() {
                initDownloadFileInfos();
            }
        }.start();
        // 如果希望service启动就开始下载所有未完成的任务，则开启以下实现
//        FileDownloader.continueAll(true);
    }

    private void initDownloadFileInfos() {
        this.mDownloadFileInfos = FileDownloader.getDownloadFiles();
        Log.i("xinxi", "  " + "服务");
    }

    Boolean isd = false;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        urls = new ArrayList<>();
        if (intent.getStringArrayListExtra("urls") == null)
            return;
        isd = intent.getBooleanExtra("bool", false);
        urls = intent.getStringArrayListExtra("urls");
    }
    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
        // 正在重试下载（如果你配置了重试次数，当一旦下载失败时会尝试重试下载），retryTimes是当前第几次重试
        if (downloadFileInfo == null) {
            return;
        }
        Log.i("xinxi", downloadFileInfo.getFileName() + retryTimes);
    }

    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
        // 等待下载（等待其它任务执行完成，或者FileDownloader在忙别的操作）

        if (downloadFileInfo == null) {
            return;
        }

        // add
        if (addNewDownloadFileInfo(downloadFileInfo)) {
            // add succeed
//            Message msg=new Message();
//            msg.what=0x03;
//            msg.obj=downloadFileInfo;
//            FileShareActivity.handler.sendMessage(msg);
        } else {
        }
    }


    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
        // 准备中（即，正在连接资源）
        if (downloadFileInfo != null) {
//            Log.i("xinxi", downloadFileInfo.getFileName());
        }
    }

    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
        // 已准备好（即，已经连接到了资源）
        if (downloadFileInfo != null) {
//            Log.i("xinxi", downloadFileInfo.getFileName());
        }
    }

    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long
            remainingTime) {
        // 正在下载，downloadSpeed为当前下载速度，单位KB/s，remainingTime为预估的剩余时间，单位秒

        if (downloadFileInfo == null) {
            return;
        }

        String url = downloadFileInfo.getUrl();

        // download progress
        int totalSize = (int) downloadFileInfo.getFileSizeLong();
        int downloaded = (int) downloadFileInfo.getDownloadedSizeLong();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("totalSize", totalSize);
        bundle.putInt("downloaded", downloaded);
        bundle.putBoolean("isd", false);
        bundle.putString("FileName", downloadFileInfo.getFileName());
        intent.setAction(FileShareActivity.ACTION);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        // download size
        double downloadSize = downloadFileInfo.getDownloadedSizeLong() / 1024f / 1024f;
        double fileSize = downloadFileInfo.getFileSizeLong() / 1024f / 1024f;
//            tvDownloadSize.setText(((float) (Math.round(downloadSize * 100)) / 100) + "M/");
//            tvTotalSize.setText(((float) (Math.round(fileSize * 100)) / 100) + "M");
        // download percent
        double percent = downloadSize / fileSize * 100;
//            tvPercent.setText(((float) (Math.round(percent * 100)) / 100) + "%");

        // download speed and remain times
//            String speed = ((float) (Math.round(downloadSpeed * 100)) / 100) + "KB/s" + "  " + TimeUtil
//                    .seconds2HH_mm_ss(remainingTime);
    }

    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
        // 下载已被暂停

        if (downloadFileInfo == null) {
            return;
        }

//        for (int i = 0; i < filelists.size(); i++) {
//            if (downloadFileInfo.getFileName().equals(filelists.get(i).getFileName())) {
//                down.set(i, "继续");
//                notifyDataSetChanged();
//            }
//        }
    }

    int count = 0;

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
        // 下载完成（整个文件已经全部下载完成）
        if (downloadFileInfo == null) {
            return;
        }

        count = SharedPreferences.getInstance().getInt("files", 0) + 1;
        SharedPreferences.getInstance().putInt("files", count);
        if (count==urls.size())
            Toast.makeText(DownloadService.this, downloadFileInfo.getFileName() + "全部下载完成", Toast.LENGTH_SHORT);
        if (count % 10 == 0) {
            Toast.makeText(DownloadService.this, downloadFileInfo.getFileName() + "下载完成", Toast.LENGTH_SHORT);
            Log.i("xinxi", "完成" + downloadFileInfo.getFileName() + count);
        } else if (SharedPreferences.getInstance().getInt("files", 0) == urls.size()) {
        } else {
            return;
        }
        if (isd) {
            downAllFile();
        }
        int totalSize = (int) downloadFileInfo.getFileSizeLong();
        int downloaded = (int) downloadFileInfo.getDownloadedSizeLong();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("totalSize", totalSize);
        bundle.putInt("downloaded", downloaded);
        bundle.putBoolean("isd", true);
        bundle.putString("FileName", "完成");
        intent.setAction(FileShareActivity.ACTION);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        // 下载失败了，详细查看失败原因failReason，有些失败原因你可能必须关心

        String failType = failReason.getType();
        String failUrl = failReason.getUrl();// 或：failUrl = url，url和failReason.getUrl()会是一样的

        if (FileDownloadStatusFailReason.TYPE_URL_ILLEGAL.equals(failType)) {
            // 下载failUrl时出现url错误
            Log.i("xinxi", "下载failUrl时出现连接超时");
        } else if (FileDownloadStatusFailReason.TYPE_STORAGE_SPACE_IS_FULL.equals(failType)) {
            // 下载failUrl时出现本地存储空间不足
        } else if (FileDownloadStatusFailReason.TYPE_NETWORK_DENIED.equals(failType)) {
            // 下载failUrl时出现无法访问网络
        } else if (FileDownloadStatusFailReason.TYPE_NETWORK_TIMEOUT.equals(failType)) {
            // 下载failUrl时出现连接超时
            Log.i("xinxi", "下载failUrl时出现url错误" + url);
        } else {
            // 更多错误....
//            Log.i("xinxi","更多错误"+url);
        }
        if (downloadFileInfo == null) {
            Log.i("xinxi", "更多错误" + url + failType);
            FileDownloader.start(url);
            count = SharedPreferences.getInstance().getInt("files", 0) + 1;
            SharedPreferences.getInstance().putInt("files", count);
            return;
        }
        Log.i("xinxi", "error" + downloadFileInfo.getFileName());
        count = SharedPreferences.getInstance().getInt("files", 0) + 1;
        SharedPreferences.getInstance().putInt("files", count);
        // 查看详细异常信息
        Throwable failCause = failReason.getCause();// 或：failReason.getOriginalCause()

        // 查看异常描述信息
        String failMsg = failReason.getMessage();// 或：failReason.getOriginalCause().getMessage()
    }

    private boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) DownloadService.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    public boolean addNewDownloadFileInfo(DownloadFileInfo downloadFileInfo) {
        if (downloadFileInfo != null) {
            if (!mDownloadFileInfos.contains(downloadFileInfo)) {
                boolean isFind = false;
                for (DownloadFileInfo info : mDownloadFileInfos) {
                    if (info != null && info.getUrl().equals(downloadFileInfo.getUrl())) {
                        isFind = true;
                        break;
                    }
                }
                if (isFind) {
                    return false;
                }
                mDownloadFileInfos.add(downloadFileInfo);
                return true;
            }
        }
        return false;
    }

    public void downAllFile() {
        Log.i("xinxi", "xiazai");
        if (!isNetworkAvailable())
            return;
        if (mDownloadFileInfos.size() <= 0) {
            initDownloadFileInfos();
        }
        if (mDownloadFileInfos.size() > 0) {
            splitAry(urls, 10);
            Thread thread = new ThreadInterrupt();
            thread.interrupt();
            thread.start();
        } else {
            splitAry(urls, 10);
            MyTask mTask = new MyTask();
            mTask.execute(urls);

        }
    }

    List<List<String>> subAryList = new ArrayList<List<String>>();

    private void splitAry(List<String> ary, int subSize) {
        int count = ary.size() % subSize == 0 ? ary.size() / subSize : ary.size() / subSize + 1;
        for (int i = 0; i < count; i++) {
            int index = i * subSize;

            List<String> list = new ArrayList<>();
            int j = 0;
            while (j < subSize && index < ary.size()) {
                list.add(ary.get(index++));
                j++;
            }

            subAryList.add(list);
        }
    }

    public class ThreadInterrupt extends Thread {
        public void run() {
//                initDownloadFileInfos();
            List<String> urls = new ArrayList<>();
            boolean is = true;
            if (SharedPreferences.getInstance().getInt("files", 0) % 10 != 0) {
                for (int j = 0; j < mDownloadFileInfos.size(); j++) {
                    switch (mDownloadFileInfos.get(j).getStatus()) {
                        case Status.DOWNLOAD_STATUS_ERROR:
                            Log.i("xinxi", mDownloadFileInfos.get(j).getUrl());
//                            Dtype = "error";
                            urls.add(mDownloadFileInfos.get(j).getUrl());
                            break;
                        case Status.DOWNLOAD_STATUS_PAUSED:
                            Log.i("xinxi", mDownloadFileInfos.get(j).getFileName());
                            urls.add(mDownloadFileInfos.get(j).getUrl());
                            break;
                        case Status.DOWNLOAD_STATUS_DOWNLOADING:
                            is = false;
                            break;
                    }
                }
            }
            if (!is) {
                return;
            }
            MyTask mTask = new MyTask();
            mTask.execute(urls);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING) {
            Toast.makeText(this, "当前网络为数据网络，未下载任务", Toast.LENGTH_SHORT).show();
            return false;
        }
        //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
//            Toast.makeText(context, "wifi is open! wifi", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private class MyTask extends AsyncTask<List<String>, Integer, List<String>> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected List<String> doInBackground(List<String>... params) {
            if (mDownloadFileInfos.size() > 0) {
                if (params[0].size() > 0) {
                    FileDownloader.start(params[0]);
                } else {
                    if (SharedPreferences.getInstance().getInt("files", 0) == urls.size())
                        return null;
                    int su = SharedPreferences.getInstance().getInt("files", 0) / 10;
                    if (mDownloadFileInfos.size() % 10 != 0 && mDownloadFileInfos.size() < urls.size()) {
                        Log.i("xinxi", "加以");
//                        su = su + 1;
                    }
                    FileDownloader.start(subAryList.get(su));
                }
            } else {
//                for (int j = 0; j < subAryList.size(); j++) {
                FileDownloader.start(subAryList.get(0));
//                    publishProgress((int) ((j / (float) subAryList.size()) * 100));
//                }
            }
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i("xinxi", "loading..." + Integer.parseInt(progresses[0].toString()) + "%");
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(List<String> result) {
            Log.i("xinxi", "ok");
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 将当前service取消注册为FileDownloader下载状态监听器
        FileDownloader.unregisterDownloadStatusListener(this);
    }
}
