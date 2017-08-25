package com.e3lue.us.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
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
import com.e3lue.us.callback.MyDownloadListener;
import com.e3lue.us.db.DBController;
import com.e3lue.us.model.FileShare;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.ui.view.NumberProgressBar;
import com.e3lue.us.utils.CheckFile;
import com.e3lue.us.utils.FileUtil;

import java.io.File;
import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.domain.DownloadInfo;

import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_COMPLETED;
import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_REMOVED;
import static cn.woblog.android.downloader.domain.DownloadInfo.STATUS_WAIT;

/**
 * Created by Leo on 2017/6/23.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    List<FileShare> filelists;
    private LayoutInflater inflater;
    private Context context;
    FileShare fileShare;
    List<Integer> isvis = new ArrayList<>();
    List<Integer> isvis1 = new ArrayList<>();
    List<String> down = new ArrayList<>();
    /***
     *
     */
    private DBController dbController;
    DownloadManager downloadManager;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int position = (int) msg.obj;
            if (msg.what == 0x01) {
                if (!down.get(position).equals("继续")) {
                    Log.i("xinxi", "0x01  "+position);
                    isvis.set(position, View.VISIBLE);
                    isvis1.set(position, View.GONE);
                    down.set(position, "继续");
                    notifyDataSetChanged();
                }
            }
            if (msg.what == 0x02) {
                down.set(position, "暂停");
                isvis1.set(position, View.GONE);
                isvis.set(position, View.VISIBLE);
                notifyDataSetChanged();
            }
            if (msg.what == 0x03) {
                isvis1.set(position, View.GONE);
                isvis.set(position, View.VISIBLE);
                down.set(position, "打开");
                notifyDataSetChanged();
            }
        }
    };

    public DownloadAdapter(Context context, List<FileShare> filelists) {
        this.context = context;
        this.filelists = filelists;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        downloadManager = DownloadService.getDownloadManager(context.getApplicationContext());
        try {
            dbController = DBController.getInstance(context.getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshFile() {
    }


    public void setFilelists(List<FileShare> filelists) {
        this.filelists = filelists;
//        Message msg = new Message();
//        msg.obj = "网络数据";//可以是基本类型，可以是对象，可以是List、map等；
//        handler.sendMessage(msg);
        for (int i = 0; i < filelists.size(); i++) {
            isvis.add(i, View.GONE);
            isvis1.add(i, View.VISIBLE);
            down.add(i, "下载");
        }
        notifyDataSetChanged();
    }

    public void unRegister() {
    }

    public ViewHolder vh;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.file_share_mana_item, parent, false);
        vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        fileShare = filelists.get(position);
        holder.setFileShare(fileShare);
        holder.bindData(position);
        holder.bind(position);
        holder.download.setTag(position);
        holder.delete.setTag(position);
    }

    @Override
    public int getItemCount() {
        return filelists == null ? 0 : filelists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
        FileShare fileShare;
        private DownloadInfo downloadInfo;
        private String SDcardDir = android.os.Environment.getExternalStorageDirectory().toString() + "/e3lue/";

        public ViewHolder(View view) {
            super(view);
            itemView.setClickable(true);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(final int position) {
            String t = fileShare.getDateStr().substring(0, 4) + "/" + fileShare.getDateStr();
            String url = HttpUrl.Url.BASIC + "/userfiles/Planning/" + t + "/" + fileShare.getFileName();
            downloadInfo = downloadManager.getDownloadById(url.hashCode());
            if (downloadInfo != null) {
                downloadInfo
                        .setDownloadListener(new MyDownloadListener(new SoftReference(ViewHolder.this)) {
                            //  Call interval about one second
                            @Override
                            public void onRefresh() {
                                if (getUserTag() != null && getUserTag().get() != null) {
                                    ViewHolder viewHolder = (ViewHolder) getUserTag().get();
                                    viewHolder.refresh(position);
                                }
                            }
                        });

            }
        }

        public void bind(int position) {
            filetitle.setText(fileShare.getFileTitle());
            fileperson.setText(fileShare.getDocumentMaker());
            filedate.setText(fileShare.getCreateDate().substring(0, fileShare.getCreateDate().indexOf("T")));
            if (FileUtil.getMIMEtype(fileShare.getFileName()).startsWith("audio/")) {
                filetype.setImageResource(R.drawable.home_icon_music);
            } else if (FileUtil.getMIMEtype(fileShare.getFileName()).startsWith("video/")) {
                filetype.setImageResource(R.drawable.home_icon_videonormal);

            } else if (FileUtil.getMIMEtype(fileShare.getFileName()).startsWith("image/")) {
                filetype.setImageResource(R.drawable.home_icon_picturenormal);
            } else if (FileUtil.getMIMEtype(fileShare.getFileName()).startsWith("text/")) {
                filetype.setImageResource(R.drawable.home_icon_txt);
            } else if (FileUtil.getMIMEtype(fileShare.getFileName()).contains("powerpoint")) {
                filetype.setImageResource(R.drawable.home_icon_ppt);
            } else {
                if (getExtensionName(fileShare.getFileName()).contains("xls")) {
                    filetype.setImageResource(R.drawable.home_icon_excel);
                } else if (getExtensionName(fileShare.getFileName()).contains("pdf")) {
                    filetype.setImageResource(R.drawable.home_icon_pdf);
                } else if (getExtensionName(fileShare.getFileName()).contains("apk")) {
                    filetype.setImageResource(R.drawable.home_icon_apknormal);
                } else if (getExtensionName(fileShare.getFileName()).contains("zip") || getExtensionName(fileShare.getFileName()).contains("rar")) {
                    filetype.setImageResource(R.drawable.home_icon_zip);
                } else if (getExtensionName(fileShare.getFileName()).contains("doc") || getExtensionName(fileShare.getFileName()).contains("dot")) {
                    filetype.setImageResource(R.drawable.home_icon_word);
                } else {
                    filetype.setImageResource(R.drawable.home_icon_other);
                }
            }
            if (isvis1.get(position) == View.VISIBLE) {
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
            if (isvis.get(position) == View.VISIBLE) {
                pbProgress.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            } else {
                pbProgress.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
            download.setText(down.get(position));
            if (downloadInfo != null) {
                refresh(position);
            }
        }

        private void notifyDownloadStatus() {
            if (downloadInfo.getStatus() == STATUS_REMOVED) {
                try {
                    dbController.deleteMyDownloadInfo(downloadInfo.getUri().hashCode());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        public void refresh(int position) {
            if (downloadInfo == null) {
                if (pbProgress.getVisibility() != View.GONE) {
                    pbProgress.setProgress(0);
                }
                down.set(position, "下载");
                notifyDataSetChanged();
            } else {
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.STATUS_NONE:
                        notifyDataSetChanged();
                        break;
                    case DownloadInfo.STATUS_PAUSED:
//                    case DownloadInfo.STATUS_ERROR:
                        Message msg = new Message();
//                        Log.i("xinxi", "0x01");
                        msg.what = 0x01;//可以是基本类型，可以是对象，可以是List、map等；
                        msg.obj = position;
                        handler.sendMessage(msg);
//                       tv_status.setText("paused");
                        try {
                            pbProgress.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case DownloadInfo.STATUS_DOWNLOADING:
                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                        if (down.get(position).equals("下载")) {
                            Message msg1 = new Message();
                            msg1.what = 0x02;//可以是基本类型，可以是对象，可以是List、map等；
                            msg1.obj = position;
                            handler.sendMessage(msg1);
                        }
                        try {
                            pbProgress.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case STATUS_COMPLETED:
                        Log.i("xinxi", "打开");
                        if (down.get(position).equals("打开")) return;
                        Message msg1 = new Message();
                        msg1.what = 0x03;//可以是基本类型，可以是对象，可以是List、map等；
                        msg1.obj = position;
                        handler.sendMessage(msg1);
                        try {
                            pbProgress.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case STATUS_REMOVED:
                        pbProgress.setProgress(0);
                        down.set(position, "下载");
                        isvis1.set(position, View.VISIBLE);
                        isvis.set(position, View.GONE);
                        notifyDataSetChanged();
                        break;
                    case STATUS_WAIT:
                        isvis.set(position, View.VISIBLE);
                        isvis1.set(position, View.GONE);
                        pbProgress.setProgress(0);
                        down.set(position, "等待中");
                        notifyDataSetChanged();
                        break;
                }

            }
        }

        CheckFile checkFile;//检查本地是否存在类

        public void setFileShare(FileShare fileShare) {
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
            isNetworkAvailable();
            if (downloadInfo != null) {
                List<DownloadInfo> downloadInfos= downloadManager.findAllDownloading();
                if (downloadInfos.get(0).equals(downloadInfo)){
                    Log.i("xinxi", "相等");
                }
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.STATUS_NONE:
                    case DownloadInfo.STATUS_PAUSED:
                    case DownloadInfo.STATUS_ERROR:
                        //resume downloadInfo
                        Log.i("xinxi", "resume");
                        down.set((int) v.getTag(), "暂停");
                        notifyDataSetChanged();
                        downloadManager.resume(downloadInfo);
                        break;
                    case DownloadInfo.STATUS_DOWNLOADING:
                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                    case DownloadInfo.STATUS_WAIT:
                        //pause downloadInfo
                        Log.i("xinxi", "pause");
                        down.set((int) v.getTag(), "继续");
                        notifyDataSetChanged();
                        downloadManager.pause(downloadInfo);
                        break;
                    case DownloadInfo.STATUS_COMPLETED:
                        down.set((int) v.getTag(), "打开");
                        notifyDataSetChanged();
                        Log.i("xinxi", "STATUS_COMPLETED");
                        checkFile = new CheckFile(context);
                        checkFile.findFile();
                        for (int i = 0; i < checkFile.findFile().size(); i++) {
                            if (checkFile.findFile().get(i).equals(fileShare.getFileName())) {
                                File file = new File(SDcardDir + fileShare.getFileName());
                                FileUtil.openFile(file, context);
                            }
                        }
//                        downloadManager.remove(downloadInfo);
                        break;
                    default:
                        downloadInfo = null;
                        Log.i("xinxi", "null");
                        download(v);
                        break;

                }
            } else {
//            Create new download task
                Log.i("xinxi", "task");
                isvis.set((int) v.getTag(), View.VISIBLE);
                isvis1.set((int) v.getTag(), View.GONE);
                down.set((int) v.getTag(), "暂停");
                notifyDataSetChanged();
                downloadFile((int) v.getTag());
            }
        }

        public void downloadFile(final int position) {
//            notifyDataSetChanged();
            String t = filelists.get(position).getDateStr().substring(0, 4) + "/" + filelists.get(position).getDateStr();
            String url = HttpUrl.Url.BASIC + "/userfiles/Planning/" + t + "/" + filelists.get(position).getFileName();
            File d = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "e3lue");
            if (!d.exists()) {
                d.mkdirs();
            }
            String path = d.getAbsolutePath().concat("/").concat(filelists.get(position).getFileName());
            downloadInfo = new DownloadInfo.Builder().setUrl(url)
                    .setPath(path)
                    .build();
            downloadInfo
                    .setDownloadListener(new MyDownloadListener(new SoftReference(ViewHolder.this)) {

                        @Override
                        public void onRefresh() {
                            notifyDownloadStatus();

                            if (getUserTag() != null && getUserTag().get() != null) {
                                ViewHolder viewHolder = (ViewHolder) getUserTag().get();
                                viewHolder.refresh(position);
                            }
                        }
                    });
            downloadManager.download(downloadInfo);
        }

        public void downAllFile() {
//            download.performClick();
            isNetworkAvailable();
            for (int i=0; i<down.size();i++){
                if (down.get(i).equals("下载")){
                    isvis.set(i, View.VISIBLE);
                    isvis1.set(i, View.GONE);
                    down.set(i, "暂停");
                    notifyDataSetChanged();
                    downloadFile(i);
                }if (downloadInfo!=null){
//                    down.set(i,"暂停");
//                    notifyDataSetChanged();
//                    List<DownloadInfo> downloadInfos= downloadManager.findAllDownloading();
//                    for (int j=0;j< downloadInfos.size();j++){
//                        switch (downloadInfos.get(j).getStatus()) {
//                            case DownloadInfo.STATUS_NONE:
//                            case DownloadInfo.STATUS_PAUSED:
//                            case DownloadInfo.STATUS_ERROR:
//                                String file = downloadInfos.get(j).getPath().substring(downloadInfos.get(j).getPath().lastIndexOf("/")+1);
//                                Log.i("xinxi",file+"     "+downloadInfos.size());
//                                downloadManager.resume(downloadInfos.get(j));
//                                break;
//                        }
//                    }
                }
            }
//            if (down.get(i).equals("下载")) {
//                isvis.set(i, View.VISIBLE);
//                isvis1.set(i, View.GONE);
//                down.set(i, "暂停");
//                notifyDataSetChanged();
//                downloadFile(i);
//            } else {
//                String t = filelists.get(i).getDateStr().substring(0, 4) + "/" + filelists.get(i).getDateStr();
//                String url = HttpUrl.Url.BASIC + "/userfiles/Planning/" + t + "/" + filelists.get(i).getFileName();
//                if(down.get(i).equals("继续")) {
//                    Log.i("xinxi", "dfvdg  "+i +url);
//                    down.set(i, "暂停");
//                    notifyDataSetChanged();
//                    List<DownloadInfo> downloadInfos= downloadManager.findAllDownloading();
//                    for (int j=0;j< downloadInfos.size();j++){
//                        downloadManager.resume(downloadInfos.get(j));
//                    }
//                } else if (down.get(i).equals("暂停")) {
//                    down.set(i,"继续");
//                    notifyDataSetChanged();
//                    List<DownloadInfo> downloadInfos= downloadManager.findAllDownloading();
//                    for (int j=0;j< downloadInfos.size();j++){
//                        Log.i("xinxi", "继续  "+i +downloadInfos.size());
////                        downloadManager.pause(downloadInfos.get(j));
//                    }
//                    Log.i("xinxi", "dfvdg " + "继续"+i);
//                } else if (down.get(i).equals("打开")) {
//                    down.set(i, "打开");
//                    notifyDataSetChanged();
//                    Log.i("xinxi", "STATUS_COMPLETED");
//                    checkFile = new CheckFile(context);
//                    checkFile.findFile();
//                    for (int j = 0; j < checkFile.findFile().size(); j++) {
//                        if (checkFile.findFile().get(j).equals(filelists.get(i).getFileName())) {
//                            File file = new File(SDcardDir + filelists.get(i).getFileName());
//                            FileUtil.openFile(file, context);
//                        }
//                    }
//
//                }
//            }
//                Toast.makeText(context,"文件已在下载中",Toast.LENGTH_SHORT).show();
        }

        private void isNetworkAvailable() {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
                Toast.makeText(context, "当前网络为数据网络", Toast.LENGTH_SHORT).show();
            }
            //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
            if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
                Toast.makeText(context, "wifi is open! wifi", Toast.LENGTH_SHORT).show();
            }

        }

        @OnClick(R.id.delete)
        public void delete(View v) {
            isvis1.set((int) v.getTag(), View.VISIBLE);
            isvis.set((int) v.getTag(), View.GONE);
            notifyDataSetChanged();
            downloadManager.remove(downloadInfo);
            if (deleteFile(SDcardDir + fileShare.getFileName())) {
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
        }

        public boolean deleteFile(String filePath) {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                return file.delete();
            }
            return false;
        }

    }
}
