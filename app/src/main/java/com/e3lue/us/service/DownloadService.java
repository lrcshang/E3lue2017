package com.e3lue.us.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.e3lue.us.activity.FileShareActivity;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;
import org.wlf.filedownloader.listener.OnRetryableFileDownloadStatusListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by x-lrc on 2017/8/31.
 */

public class DownloadService extends Service implements OnRetryableFileDownloadStatusListener {
    private List<DownloadFileInfo> mDownloadFileInfos = Collections.synchronizedList(new ArrayList<DownloadFileInfo>());
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
        // 如果希望service启动就开始下载所有未完成的任务，则开启以下实现
//        FileDownloader.continueAll(true);
        initDownloadFileInfos();
    }
    private void initDownloadFileInfos() {
        this.mDownloadFileInfos = FileDownloader.getDownloadFiles();
        Log.i("xinxi", "  " + mDownloadFileInfos.size());
    }
    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
        // 正在重试下载（如果你配置了重试次数，当一旦下载失败时会尝试重试下载），retryTimes是当前第几次重试
        if (downloadFileInfo == null) {
            return;
        }
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

    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
        // 准备中（即，正在连接资源）
    }

    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
        // 已准备好（即，已经连接到了资源）
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
        bundle.putInt("totalSize",totalSize);
        bundle.putInt("downloaded",downloaded);
        bundle.putString("FileName",downloadFileInfo.getFileName());
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

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
        // 下载完成（整个文件已经全部下载完成）
        if (downloadFileInfo == null) {
            return;
        }
        String url = downloadFileInfo.getUrl();
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
            return;
        }
        // 查看详细异常信息
        Throwable failCause = failReason.getCause();// 或：failReason.getOriginalCause()

        // 查看异常描述信息
        String failMsg = failReason.getMessage();// 或：failReason.getOriginalCause().getMessage()
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 将当前service取消注册为FileDownloader下载状态监听器
        FileDownloader.unregisterDownloadStatusListener(this);
        // 如果希望service停止就停止所有下载任务，则开启以下实现
        FileDownloader.pauseAll();// 暂停所有下载任务
    }
}
