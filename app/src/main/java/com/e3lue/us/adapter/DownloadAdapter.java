package com.e3lue.us.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.e3lue.us.model.FileShare;
import com.e3lue.us.model.FileShares;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.ui.view.NumberProgressBar;
import com.e3lue.us.utils.CheckFile;
import com.e3lue.us.utils.FileUtil;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnDeleteDownloadFileListener;
import org.wlf.filedownloader.listener.OnDeleteDownloadFilesListener;
import org.wlf.filedownloader.listener.OnRetryableFileDownloadStatusListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leo on 2017/6/23.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> implements OnRetryableFileDownloadStatusListener {
    List<FileShare> filelists;
    List<FileShares> fileRes;
    private LayoutInflater inflater;
    private Context context;
    FileShare fileShare;
    List<Integer> isvis = new ArrayList<>();
    List<Integer> isvis1 = new ArrayList<>();
    List<String> down = new ArrayList<>();
    List<Integer> progress = new ArrayList<>();
    private List<DownloadFileInfo> mDownloadFileInfos = Collections.synchronizedList(new ArrayList<DownloadFileInfo>());
    /***
     *
     */
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int position = (int) msg.obj;
            if (msg.what == 0x01) {
                if (!down.get(position).equals("下载")) {
                    isvis1.set(position, View.VISIBLE);
                    isvis.set(position, View.GONE);
                    down.set(position, "下载");
                    notifyDataSetChanged();
                }
            }
            if (msg.what == 0x02) {
                if (!isExist(position))
                    return;
                if (!down.get(position).equals("继续")) {
                    isvis1.set(position, View.GONE);
                    isvis.set(position, View.VISIBLE);
                    down.set(position, "继续");
                    notifyDataSetChanged();
                }
            }
            if (msg.what == 0x03) {
                if (!down.get(position).equals("暂停")) {
                    down.set(position, "暂停");
                    isvis1.set(position, View.GONE);
                    isvis.set(position, View.VISIBLE);
                    notifyDataSetChanged();
                }
            }
            if (msg.what == 0x04) {
                if (!down.get(position).equals("等待")) {
                    down.set(position, "等待");
                    isvis1.set(position, View.GONE);
                    isvis.set(position, View.VISIBLE);
                    notifyDataSetChanged();
                }
            }
            if (msg.what == 0x05) {
                if (!isExist(position))
                    return;
                if (!down.get(position).equals("打开")) {
                    down.set(position, "打开");
                    isvis1.set(position, View.GONE);
                    isvis.set(position, View.VISIBLE);
                    notifyDataSetChanged();
                }
            }
        }
    };

    public void setFileRes(List<FileShares> fileRes) {
        Log.i("xinxi",fileRes.size()+fileRes.get(1).getPath());
        this.fileRes = fileRes;
    }

    private Boolean isExist(int position) {
        initDownloadFileInfos();
        for (int i = 0; i < mDownloadFileInfos.size(); i++) {
            if (mDownloadFileInfos.get(i).getFileName().equals(filelists.get(position).getFileName())) {
                return true;
            }
        }
        return false;
    }

    public DownloadAdapter(Context context, List<FileShare> filelists) {
        this.context = context;
        this.filelists = filelists;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initDownloadFileInfos();
    }

    // init DownloadFileInfos
    private void initDownloadFileInfos() {
        this.mDownloadFileInfos = FileDownloader.getDownloadFiles();
    }

    void updateDownloadFileInfos() {
        initDownloadFileInfos();
        notifyDataSetChanged();
    }

    public void setFilelists(List<FileShare> filelists) {
        this.filelists = filelists;
        for (int i = 0; i < filelists.size(); i++) {

            isvis.add(i, View.GONE);
            isvis1.add(i, View.VISIBLE);
            progress.add(0);
            down.add(i, "下载");


        }
        notifyDataSetChanged();
    }

    public void unRegister() {
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
        fileShare = filelists.get(position);
        holder.setFileShare(fileShare);
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
        private String SDcardDir = android.os.Environment.getExternalStorageDirectory().toString() + "/e3lue/";

        public ViewHolder(View view) {
            super(view);
            itemView.setClickable(true);
            ButterKnife.bind(this, itemView);
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
            Message msg = new Message();
            for (int i = 0; i < mDownloadFileInfos.size(); i++) {
                if (mDownloadFileInfos.get(i).getFileName().equals(fileShare.getFileName())) {
                    Log.i("xinxi", mDownloadFileInfos.get(i).getFileSizeLong() + "    " + mDownloadFileInfos.get(i).getDownloadedSizeLong());
                    int totalSize = (int) mDownloadFileInfos.get(i).getFileSizeLong();
                    int downloaded = (int) mDownloadFileInfos.get(i).getDownloadedSizeLong();
                    progress.set(position, (int) (downloaded * 100.0 / totalSize));
                    pbProgress.setProgress(progress.get(position));
                    switch (mDownloadFileInfos.get(i).getStatus()) {
                        // download file status:unknown
                        case Status.DOWNLOAD_STATUS_UNKNOWN:
//                            tvText.setText(context.getString(R.string.main__can_not_download));
                            break;
                        // download file status:retrying
                        case Status.DOWNLOAD_STATUS_RETRYING:
//                            tvText.setText(context.getString(R.string.main__retrying_connect_resource));
                            break;
                        // download file status:preparing
                        case Status.DOWNLOAD_STATUS_PREPARING:
//                            tvText.setText(context.getString(R.string.main__getting_resource));
                            break;
                        // download file status:prepared
                        case Status.DOWNLOAD_STATUS_PREPARED:

                            break;
                        // download file status:paused
                        case Status.DOWNLOAD_STATUS_PAUSED:
                            msg.what = 0x02;
                            msg.obj = position;
                            handler.sendMessage(msg);
                            break;
                        // download file status:downloading
                        case Status.DOWNLOAD_STATUS_DOWNLOADING:
                            msg.what = 0x03;
                            msg.obj = position;
                            handler.sendMessage(msg);
                            break;
                        // download file status:error
                        case Status.DOWNLOAD_STATUS_ERROR:
                            break;
                        // download file status:waiting
                        case Status.DOWNLOAD_STATUS_WAITING:
                            msg.what = 0x04;
                            msg.obj = position;
                            handler.sendMessage(msg);
                            break;
                        // download file status:completed
                        case Status.DOWNLOAD_STATUS_COMPLETED:
                            msg.what = 0x05;
                            msg.obj = position;
                            handler.sendMessage(msg);
                            break;
                        // download file status:file not exist
                        case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST:
                            break;
                    }
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
            int pos = (int) v.getTag();
            isNetworkAvailable();
            if (down.get((int) v.getTag()).equals("下载")) {
                Log.i("xinxi", "task");
                isvis.set((int) v.getTag(), View.VISIBLE);
                isvis1.set((int) v.getTag(), View.GONE);
                down.set((int) v.getTag(), "暂停");
                notifyDataSetChanged();
                downloadFile((int) v.getTag());
            } else if (down.get((int) v.getTag()).equals("暂停")) {
                String t = filelists.get(pos).getDateStr().substring(0, 4) + "/" + filelists.get(pos).getDateStr();
                String url = HttpUrl.Url.BASIC + "/userfiles/Planning/" + t + "/" + filelists.get(pos).getFileName();
                FileDownloader.pause(url);// 暂停单个下载任务
                down.set((int) v.getTag(), "继续");
//                notifyDataSetChanged();
            } else if (down.get((int) v.getTag()).equals("继续")) {
                downloadFile((int) v.getTag());
                down.set((int) v.getTag(), "暂停");
                notifyDataSetChanged();
            } else if (down.get((int) v.getTag()).equals("打开")) {
                checkFile = new CheckFile(context);
                for (int i = 0; i < checkFile.findFile().size(); i++) {
                    if (checkFile.findFile().get(i).equals(fileShare.getFileName())) {
                        File file = new File(SDcardDir + fileShare.getFileName());
                        FileUtil.openFile(file, context);
                    }
                }
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
            FileDownloader.start(url);
            updateDownloadFileInfos();
        }

        public void downAllFile() {
            isNetworkAvailable();
            List<String> urls = new ArrayList<>();
            urls.clear();
            for (int i = 0; i < down.size(); i++) {
                String t = filelists.get(i).getDateStr().substring(0, 4) + "/" + filelists.get(i).getDateStr();
                String url = HttpUrl.Url.BASIC + "/userfiles/Planning/" + t + "/" + filelists.get(i).getFileName();
                if (down.get(i).equals("下载")) {
                    Log.i("xinxi", url);
                    urls.add(url);
                }
                isvis.set(i, View.VISIBLE);
                isvis1.set(i, View.GONE);
                down.set(i, "暂停");
                notifyDataSetChanged();
            }
            for (int j = 0; j < mDownloadFileInfos.size(); j++) {
                switch (mDownloadFileInfos.get(j).getStatus()) {
                    case Status.DOWNLOAD_STATUS_PAUSED:
                    case Status.DOWNLOAD_STATUS_ERROR:
                        urls.add(mDownloadFileInfos.get(j).getUrl());
                        break;
                }
            }
            FileDownloader.start(urls);
            updateDownloadFileInfos();
            Log.i("xinxi", urls.size() + "  :");
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
        public void delete(final View v) {
            String t = fileShare.getDateStr().substring(0, 4) + "/" + fileShare.getDateStr();
            String url = HttpUrl.Url.BASIC + "/userfiles/Planning/" + t + "/" + fileShare.getFileName();
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
    }

    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
        // 正在重试下载（如果你配置了重试次数，当一旦下载失败时会尝试重试下载），retryTimes是当前第几次重试

        if (downloadFileInfo == null) {
            return;
        }
//        String url = downloadFileInfo.getUrl();
//            TextView tvText = (TextView) cacheConvertView.findViewById(R.id.tvText);
//            tvText.setText(cacheConvertView.getContext().getString(R.string.main__retrying_connect_resource) + "(" +
//                    retryTimes + ")");

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
            notifyDataSetChanged();
        } else {
            for (int i = 0; i < filelists.size(); i++) {
                if (downloadFileInfo.getFileName().equals(filelists.get(i).getFileName())) {
                    if (down.get(i).equals("暂停")&&checkNetworkState())return;
                    down.set(i, "等待");
                    notifyDataSetChanged();
                }
            }
        }
    }
    private boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager  manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                notifyDataSetChanged();
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

        for (int i = 0; i < filelists.size(); i++) {
            if (downloadFileInfo.getFileName().equals(filelists.get(i).getFileName()) && !down.get(i).equals("暂停")) {
                down.set(i, "暂停");
                isvis1.set(i, View.GONE);
                isvis.set(i, View.VISIBLE);
                notifyDataSetChanged();
            }
        }
        for (int i = 0; i < filelists.size(); i++) {
            if (downloadFileInfo.getFileName().equals(filelists.get(i).getFileName())) {
                progress.set(i, (int) (downloaded * 100.0 / totalSize));
                notifyDataSetChanged();
            }
        }
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
//            tvText.setText(speed);
//            tvText.setTag(speed);

//            setBackgroundOnClickListener(lnlyDownloadItem, downloadFileInfo);
    }

    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
        // 下载已被暂停

        if (downloadFileInfo == null) {
            return;
        }

        for (int i = 0; i < filelists.size(); i++) {
            if (downloadFileInfo.getFileName().equals(filelists.get(i).getFileName())) {
                down.set(i, "继续");
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
        // 下载完成（整个文件已经全部下载完成）
        if (downloadFileInfo == null) {
            return;
        }

        String url = downloadFileInfo.getUrl();
        for (int i = 0; i < filelists.size(); i++) {
            if (downloadFileInfo.getFileName().equals(filelists.get(i).getFileName())) {
                down.set(i, "打开");

                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        // 下载失败了，详细查看失败原因failReason，有些失败原因你可能必须关心

        String failType = failReason.getType();
        String failUrl = failReason.getUrl();// 或：failUrl = url，url和failReason.getUrl()会是一样的

        if (FileDownloadStatusFailReason.TYPE_URL_ILLEGAL.equals(failType)) {
            // 下载failUrl时出现url错误
        } else if (FileDownloadStatusFailReason.TYPE_STORAGE_SPACE_IS_FULL.equals(failType)) {
            // 下载failUrl时出现本地存储空间不足
        } else if (FileDownloadStatusFailReason.TYPE_NETWORK_DENIED.equals(failType)) {
            // 下载failUrl时出现无法访问网络
        } else if (FileDownloadStatusFailReason.TYPE_NETWORK_TIMEOUT.equals(failType)) {
            // 下载failUrl时出现连接超时
        } else {
            // 更多错误....
        }

        // 查看详细异常信息
        Throwable failCause = failReason.getCause();// 或：failReason.getOriginalCause()

        // 查看异常描述信息
        String failMsg = failReason.getMessage();// 或：failReason.getOriginalCause().getMessage()
    }
}
