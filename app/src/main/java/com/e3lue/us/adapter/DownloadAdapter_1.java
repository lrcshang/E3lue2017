package com.e3lue.us.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e3lue.us.R;
import com.e3lue.us.activity.FileShareActivity;
import com.e3lue.us.callback.MyBackChickListener;
import com.e3lue.us.model.FileShares;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.service.DownloadService;
import com.e3lue.us.ui.view.NumberProgressBar;
import com.e3lue.us.utils.CheckFile;
import com.e3lue.us.utils.FileUtil;
import com.e3lue.us.utils.SharedPreferences;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnDeleteDownloadFileListener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leo on 2017/6/23.
 */

public class DownloadAdapter_1 extends RecyclerView.Adapter<DownloadAdapter_1.ViewHolder> {
    List<FileShares> fileRess;
    private LayoutInflater inflater;
    private FileShareActivity context;
    FileShares fileShare;
    List<FileShares> Dfiles;
    List<Integer> isvis;
    List<Integer> isvis1;
    List<Integer> isprog = new ArrayList<>();
    List<Integer> progress;
    List<FileShares> fileRes = new ArrayList<>();
    CheckFile checkFile;//检查本地是否存在类
    String fPath = "";
    Thread thread;
    boolean isadd = false;
    List<DownloadFileInfo> list = new ArrayList<>();
    Intent intent;
    String Dtype = "";
    private List<DownloadFileInfo> mDownloadFileInfos = Collections.synchronizedList(new ArrayList<DownloadFileInfo>());
    List<List<String>> subAryList = new ArrayList<List<String>>();
    /***
     *
     */
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int position = (int) msg.obj;
            if (msg.what == 0x01) {
                progress.set(position, 0);
                isvis1.set(position, View.VISIBLE);
                isvis.set(position, View.GONE);
                notifyDataSetChanged();
            }
        }
    };

    public DownloadAdapter_1(final FileShareActivity context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checkFile = new CheckFile(context);
        thread = new ThreadInterrupt();
        intent = new Intent(context, DownloadService.class);
        new Thread() {
            @Override
            public void run() {
                int as = 0;
                initDownloadFileInfos();
                if (mDownloadFileInfos.size() <= 0)
                    return;
                for (int i = 0; i < mDownloadFileInfos.size(); i++) {
                    if (mDownloadFileInfos.get(i).getStatus() == Status.DOWNLOAD_STATUS_DOWNLOADING) {
                        Message msg = new Message();
                        msg.what = 0x03;
                        context.handler.sendMessage(msg);
                        return;
                    }
                    if (mDownloadFileInfos.get(i).getStatus() == Status.DOWNLOAD_STATUS_ERROR ||
                            mDownloadFileInfos.get(i).getStatus() == Status.DOWNLOAD_STATUS_PAUSED) {
                        as++;
                    }
                }
                if (mDownloadFileInfos.size() != urls.size())
                    as++;
                if (as == 0) {
                    Message msg = new Message();
                    msg.what = 0x04;
                    context.handler.sendMessage(msg);
                }
            }
        }.start();

    }

    public void setFileRess(List<FileShares> fileRess, int a) {
        List<FileShares> Dfiles = new ArrayList<>();
        this.fileRess = fileRess;
        for (int i = 0; i < fileRess.size(); i++) {
            if (fileRess.get(i).getType().equals("file")) {
                Dfiles.add(fileRess.get(i));
            }
        }
        this.Dfiles = Dfiles;
        setFileRes(a);
        thread.start();
    }

    public class ThreadInterrupt extends Thread {
        public void run() {
            try {
                getURLS();
                intent.putStringArrayListExtra("urls", (ArrayList<String>) urls);
                intent.putExtra("bool", false);
                context.startService(intent);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void setFileRes(int a) {
        List<Integer> isvis = new ArrayList<>();
        List<Integer> isvis1 = new ArrayList<>();
        List<Integer> isprog = new ArrayList<>();
        List<Integer> progress = new ArrayList<>();
        List<FileShares> fileRes = new ArrayList<>();
        for (int i = 0; i < fileRess.size(); i++) {
            if (fileRess.get(i).getPId() == a) {
                fileRes.add(fileRess.get(i));
                isvis.add(View.GONE);
                isvis1.add(View.VISIBLE);
                isprog.add(View.GONE);
                progress.add(0);
            }
        }
        this.progress = progress;
        this.isvis = isvis;
        this.isvis1 = isvis1;
        this.isprog = isprog;
        this.fileRes = fileRes;
        list = getDownloadFileInfo();
        notifyDataSetChanged();
    }

    public void getURLS() {
        for (int i = 0; i < Dfiles.size(); i++) {
            if (Dfiles.get(i).getPId() > 0) {
                for (int j = 0; j < fileRess.size(); j++) {
                    if (Dfiles.get(i).getId() == fileRess.get(j).getId()) {
                        path = fileRess.get(j).getPath();
                        findPath(j);
//                            FileDownloader.start(url + findPath(j));
                    }
                }
            } else {
                url = HttpUrl.Url.BASIC + "/userfiles/fileshare/" + fileRess.get(i).getPath();
                urls.add(url);
            }
        }
        int a=0x05;
        if (SharedPreferences.getInstance().getInt("files",0)==urls.size()){
           a= 0x04;
        }
        Message msg =new Message();
        msg.what=a;
        SharedPreferences.getInstance().getInt("files",0);


        context.handler.sendMessage(msg);
    }

    List<String> urls = new ArrayList<>();
    String url = HttpUrl.Url.BASIC + "/userfiles/fileshare/";
    String path = "";

    public String findPath(int a) {
        for (int j = 0; j < fileRess.size(); j++) {
            if (fileRess.get(a).getPId() == fileRess.get(j).getId()) {
                if (fileRess.get(j).getPId() > 0) {
                    path = fileRess.get(j).getPath() + "/" + path;
                    findPath(j);
                } else {
                    path = fileRess.get(j).getPath() + "/" + path;
                    Log.i("xinxi", HttpUrl.Url.BASIC + "/userfiles/fileshare/" + path);
                    urls.add(url + path);
                    return path;

                }
            }
        }
        return null;
    }

    public void backFileRess(int a) {
        List<FileShares> fileRes = new ArrayList<>();
        List<Integer> isvis = new ArrayList<>();
        List<Integer> isprog = new ArrayList<>();
        List<Integer> isvis1 = new ArrayList<>();
        List<Integer> progress = new ArrayList<>();
        int aa = -1;
        for (int i = 0; i < fileRess.size(); i++) {
            if (fileRess.get(i).getId() == a) {
                aa = fileRess.get(i).getPId();
            }
        }
        for (int i = 0; i < fileRess.size(); i++) {
            if (fileRess.get(i).getPId() == aa) {
                isvis.add(View.GONE);
                isvis1.add(View.VISIBLE);
                isprog.add(View.GONE);
                progress.add(0);
                fileRes.add(fileRess.get(i));
            }
        }
        this.progress = progress;
        this.isvis = isvis;
        this.isvis1 = isvis1;
        this.isprog = isprog;
        this.fileRes = fileRes;
        notifyDataSetChanged();
    }

    // init DownloadFileInfos
    private void initDownloadFileInfos() {
        this.mDownloadFileInfos = FileDownloader.getDownloadFiles();
        Log.i("xinxi", "  " + mDownloadFileInfos.size());
    }

    void updateDownloadFileInfos() {
        initDownloadFileInfos();
        notifyDataSetChanged();
    }

    public void updateDownInfo(int totalSize, int downloaded, String FileName) {
        for (int i = 0; i < fileRes.size(); i++) {
            if (FileName.contains(fileRes.get(i).getPath()) && fileRes.get(i).getType().equals("file")) {
                isvis1.set(i, View.GONE);
                isprog.set(i, View.VISIBLE);
                isvis.set(i, View.VISIBLE);
                progress.set(i, (int) (downloaded * 100.0 / totalSize));
                notifyDataSetChanged();
            }
        }
    }

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        Bundle bundle = new Bundle();

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            bundle = intent.getExtras();
            if (bundle.getString("FileName").equals("完成")) {
                if (bundle.getBoolean("isd")) {
                    vh.downAllFile();
                }
            } else {
                updateDownInfo(bundle.getInt("totalSize"), bundle.getInt("downloaded"), bundle.getString("FileName"));
            }
        }

    };

    public void unRegister() {
        intent.putStringArrayListExtra("urls", (ArrayList<String>) urls);
        intent.putExtra("bool", true);
        context.startService(intent);
    }

    public ViewHolder vh;
    View view;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.file_share_mana_item, parent, false);
        vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        fileShare = fileRes.get(position);
        holder.setFileShare(fileShare);
        holder.bind(position);
        holder.download.setTag(position);
        holder.delete.setTag(position);
    }

    public List<DownloadFileInfo> getDownloadFileInfo() {
        List<DownloadFileInfo> list = new ArrayList<>();
        for (int i = 0; i < mDownloadFileInfos.size(); i++) {
            for (int j = 0; j < fileRes.size(); j++) {
                if (mDownloadFileInfos.get(i).getFileName().contains(fileRes.get(j).getPath()) && fileRes.get(j).getType().equals("file")) {
                    list.add(mDownloadFileInfos.get(j));
                    int totalSize = (int) mDownloadFileInfos.get(i).getFileSizeLong();
                    int downloaded = (int) mDownloadFileInfos.get(i).getDownloadedSizeLong();
//                    list.add(mDownloadFileInfos.get(i));
                    progress.set(j, (int) (downloaded * 100.0 / totalSize));
                    switch (mDownloadFileInfos.get(i).getStatus()) {
                        // download file status:unknown
                        case Status.DOWNLOAD_STATUS_UNKNOWN:
                            break;
                        // download file status:retrying
                        case Status.DOWNLOAD_STATUS_RETRYING:
                            break;
                        // download file status:preparing
                        case Status.DOWNLOAD_STATUS_PREPARING:
                            break;
                        // download file status:prepared
                        case Status.DOWNLOAD_STATUS_PREPARED:
                            break;
                        // download file status:paused
                        case Status.DOWNLOAD_STATUS_PAUSED:
                            isvis1.set(j, View.GONE);
                            isprog.set(j, View.VISIBLE);
                            isvis.set(j, View.VISIBLE);
                            break;
                        // download file status:downloading
                        case Status.DOWNLOAD_STATUS_DOWNLOADING:
                            isvis1.set(j, View.GONE);
                            isprog.set(j, View.VISIBLE);
                            isvis.set(j, View.VISIBLE);
                            break;
                        // download file status:error
                        case Status.DOWNLOAD_STATUS_ERROR:
                            break;
                        // download file status:waiting
                        case Status.DOWNLOAD_STATUS_WAITING:
                            isvis1.set(j, View.GONE);
                            isvis.set(j, View.VISIBLE);
                            isprog.set(j, View.VISIBLE);
                            break;
                        // download file status:completed
                        case Status.DOWNLOAD_STATUS_COMPLETED:
                            isvis1.set(j, View.GONE);
                            isvis.set(j, View.VISIBLE);
                            isprog.set(j, View.GONE);
                            break;
                        // download file status:file not exist
                        case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST:
                            break;
                    }
                }
            }
        }
        return list;
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
                    if (SharedPreferences.getInstance().getInt("files", 0) == urls.size()) {
                        Message msg = new Message();
                        msg.what = 0x04;
                        context.handler.sendMessage(msg);
                        return null;
                    }
                    int su = SharedPreferences.getInstance().getInt("files", 0) / 10;
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
    public int getItemCount() {
        return fileRes == null ? 0 : fileRes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MyBackChickListener {

        @BindView(R.id.file_type)
        ImageView filetype;
        @BindView(R.id.file_title)
        TextView filetitle;
        @BindView(R.id.file_person)
        TextView fileperson;
        @BindView(R.id.file_date)
        TextView filedate;
        @BindView(R.id.file_size)
        TextView filesize;
        @BindView(R.id.file_share_mana_pbprogress)
        NumberProgressBar pbProgress;
        @BindView(R.id.file_download)
        Button download;
        @BindView(R.id.line1)
        LinearLayout linearLayout;
        @BindView(R.id.delete)
        ImageView delete;
        FileShares fileShare;
        private String SDcardDir = Environment.getExternalStorageDirectory().toString() + "/e3lue";

        public ViewHolder(View view) {
            super(view);
            itemView.setClickable(true);
            view.setOnClickListener(this);
            context.setBackChickListener(this);
            ButterKnife.bind(this, itemView);
        }

        /**
         * String t = filelists.get(pos).getDateStr().substring(0, 4) + "/" + filelists.get(pos).getDateStr();
         * String url = HttpUrl.Url.BASIC + "/userfiles/fileshare/" + 1PPT + "/" + 324公司简介20170704.pdf;
         */
        public void bind(int position) {
            filetitle.setText(fileShare.getPath());
//            fileperson.setText(fileShare.getDocumentMaker());
//            filedate.setText(fileShare.getCreateDate().substring(0, fileShare.getCreateDate().indexOf("T")));
            if (fileShare.getType().equals("fold")) {
                filetype.setImageResource(R.drawable.home_icon_flod);
            } else {
                if (FileUtil.getMIMEtype(fileShare.getPath()).startsWith("audio/")) {
                    if (getExtensionName(fileShare.getPath()).contains("wmv"))
                        filetype.setImageResource(R.drawable.home_icon_videonormal);
                    else
                        filetype.setImageResource(R.drawable.home_icon_music);
                } else if (FileUtil.getMIMEtype(fileShare.getPath()).startsWith("video/")) {
                    filetype.setImageResource(R.drawable.home_icon_videonormal);
                } else if (FileUtil.getMIMEtype(fileShare.getPath()).startsWith("image/")) {
                    filetype.setImageResource(R.drawable.home_icon_picturenormal);
                } else if (FileUtil.getMIMEtype(fileShare.getPath()).startsWith("text/")) {
                    filetype.setImageResource(R.drawable.home_icon_txt);
                } else if (FileUtil.getMIMEtype(fileShare.getPath()).contains("powerpoint")) {
                    filetype.setImageResource(R.drawable.home_icon_ppt);
                } else {
                    if (getExtensionName(fileShare.getPath()).contains("xls")) {
                        filetype.setImageResource(R.drawable.home_icon_excel);
                    } else if (getExtensionName(fileShare.getPath()).contains("pdf")) {
                        filetype.setImageResource(R.drawable.home_icon_pdf);
                    } else if (getExtensionName(fileShare.getPath()).contains("apk")) {
                        filetype.setImageResource(R.drawable.home_icon_apknormal);
                    } else if (getExtensionName(fileShare.getPath()).contains("zip") || getExtensionName(fileShare.getPath()).contains("rar")) {
                        filetype.setImageResource(R.drawable.home_icon_zip);
                    } else if (getExtensionName(fileShare.getPath()).contains("doc") || getExtensionName(fileShare.getPath()).contains("dot")) {
                        filetype.setImageResource(R.drawable.home_icon_word);
                    } else {
                        filetype.setImageResource(R.drawable.home_icon_other);
                    }
                }
            }

            if (isvis1.get(position) == View.VISIBLE) {
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
            if (isprog.get(position) == View.VISIBLE) {
                pbProgress.setVisibility(View.VISIBLE);
            } else {
                pbProgress.setVisibility(View.GONE);
            }
            if (isvis.get(position) == View.VISIBLE) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
            pbProgress.setProgress(progress.get(position));
        }

        @Override
        public void onClick(View v) {
            if (fileRes.get(getPosition()).getType().equals("fold")) {

                fPath = fPath + "/" + fileRes.get(getPosition()).getPath();
                Log.i("xinxi", fPath);
                Message msg = new Message();
                msg.what = 0x01;
                msg.obj = fileRes.get(getPosition()).getPath();
                context.handler.sendMessage(msg);
                setFileRes(fileRes.get(getPosition()).getId());
//                mHandler.post(mRunnable);
            } else {
                List<String> filelist = checkFile.findFile(fPath);
                for (int i = 0; i < filelist.size(); i++) {
                    if (filelist.get(i).equals(fileRes.get(getPosition()).getPath())) {
                        File file = new File(SDcardDir + fPath + "/" + fileRes.get(getPosition()).getPath());
                        FileUtil.openFile(file, context);
                        return;
                    }
                }
            }
        }

        public class ThreadInterrupt extends Thread {
            public void run() {
//                initDownloadFileInfos();
                List<String> urls = new ArrayList<>();
                boolean is = true;
                Log.i("xinxi", SharedPreferences.getInstance().getInt("files", 0) + "");
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

        @Override
        public void onBackChick() {
//            Toast.makeText(context,"返回",Toast.LENGTH_SHORT).show();
            if (fileRes.get(0).getPId() > 0) {
                fPath = fPath.replace(fPath.substring(fPath.lastIndexOf("/") + 1), "");
                fPath = fPath.substring(0, fPath.length() - 1);
                Log.i("xinxi", fPath);
                Message msg = new Message();
                msg.what = 0x02;
                msg.obj = fileRes.get(0).getPath();
                context.handler.sendMessage(msg);
                backFileRess(fileRes.get(0).getPId());
            } else {
                thread.interrupt();
                context.finish();
            }
        }

        public void setFileShare(FileShares fileShare) {
            this.fileShare = fileShare;
        }

        public String getExtensionName(String filename) {
            if ((filename != null) && (filename.length() > 0)) {
                int dot = filename.lastIndexOf('.');
                if ((dot > -1) && (dot < (filename.length() - 1))) {
                    return filename.substring(dot + 1);
                }
            }
            return filename;
        }

        @OnClick(R.id.file_download)
        public void download(final View v) {
            int pos = (int) v.getTag();
            isNetworkAvailable();
        }

        public void downAllFile() {
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

        public class Dthread extends Thread {
            @Override
            public void run() {
                FileDownloader.start(urls);
            }
        }

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
//            subList = new ArrayList<>();
            if (subAryList.size() <= 0) return;
//            for (i = 0; i < subAryList.size(); i++) {
//            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
            Toast.makeText(context, "当前网络为数据网络，未下载任务", Toast.LENGTH_SHORT).show();
            return false;
        }
        //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
//            Toast.makeText(context, "wifi is open! wifi", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @OnClick(R.id.delete)
    public void delete(final View v) {
        String url = getURL((int) v.getTag());
        if (url.equals(""))
            return;
        FileDownloader.delete(url, true, new OnDeleteDownloadFileListener() {
            @Override
            public void onDeleteDownloadFileSuccess(DownloadFileInfo downloadFileDeleted) {
                Message msg = new Message();
                msg.what = 0x01;
                msg.obj = v.getTag();
                handler.sendMessage(msg);
                Log.e("wlf", "onDeleteDownloadFileSuccess 成功回调，单个删除" + downloadFileDeleted.getFileName()
                        + "成功");
            }

            @Override
            public void onDeleteDownloadFilePrepared(DownloadFileInfo downloadFileNeedDelete) {
                if (downloadFileNeedDelete != null) {
                }
            }

            @Override
            public void onDeleteDownloadFileFailed(DownloadFileInfo downloadFileInfo,
                                                   DeleteDownloadFileFailReason failReason) {

                Log.e("wlf", "onDeleteDownloadFileFailed 出错回调，单个删除" + downloadFileInfo.getFileName() +
                        "失败");
            }
        });
//            if (deleteFile(SDcardDir + fileShare.getFileName())) {
//                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
//            } else
//                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public String getURL(int a) {
        Log.i("xinxi", fileShare.getPath());
        for (int i = 0; i < mDownloadFileInfos.size(); i++) {
            if (fileRes.get(a).getPath().equals(mDownloadFileInfos.get(i).getFileName())) {
                return mDownloadFileInfos.get(i).getUrl();
            }
        }
        return "";
    }
}
