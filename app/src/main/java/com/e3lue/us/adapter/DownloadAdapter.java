package com.e3lue.us.adapter;

import android.content.Context;
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
    /***
     *
     */
    private DBController dbController;
    DownloadManager downloadManager;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            ArrayList<String> list = b.getStringArrayList("list");
            for (int i = 0; i < list.size(); i++) {
                Log.i("xinxi", list.get(i));

            }
            refreshFile();
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
        notifyDataSetChanged();
    }

    public void unRegister() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.file_share_mana_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        fileShare = filelists.get(position);
        holder.setFileShare(fileShare);
        holder.bindData();
        holder.bind();
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData() {
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
                                    viewHolder.refresh();
                                }
                            }
                        });

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

        public void refresh() {

            if (downloadInfo == null) {
                pbProgress.setProgress(0);
                download.setText("下载");
            } else {
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.STATUS_NONE:
                        download.setText("下载");
                        break;
                    case DownloadInfo.STATUS_PAUSED:
                    case DownloadInfo.STATUS_ERROR:
                        download.setText("继续");
//                        tv_status.setText("paused");
                        try {
                            pbProgress.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case DownloadInfo.STATUS_DOWNLOADING:
                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                        download.setText("暂停");
                        try {
                            pbProgress.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case STATUS_COMPLETED:
                        pbProgress.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        download.setText("打开");
                        try {
                            pbProgress.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case STATUS_REMOVED:
                        pbProgress.setProgress(0);
                        download.setText("下载");
                        linearLayout.setVisibility(View.VISIBLE);
                        pbProgress.setVisibility(View.GONE);
                        break;
                    case STATUS_WAIT:
                        pbProgress.setProgress(0);
                        download.setText("等待中");
                        break;
                }

            }
        }

        CheckFile checkFile;//检查本地是否存在类

        public void setFileShare(FileShare fileShare) {
            this.fileShare = fileShare;
        }

        public void bind() {
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
                }else if (getExtensionName(fileShare.getFileName()).contains("doc")||getExtensionName(fileShare.getFileName()).contains("dot")) {
                   filetype.setImageResource(R.drawable.home_icon_word);
               }
                else {
                    filetype.setImageResource(R.drawable.home_icon_other);
                }
            }
            if (downloadInfo != null) {
                linearLayout.setVisibility(View.GONE);
                pbProgress.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                refresh();
            }
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
        public void download() {
            if (downloadInfo != null) {
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.STATUS_NONE:
                    case DownloadInfo.STATUS_PAUSED:
                    case DownloadInfo.STATUS_ERROR:
                        //resume downloadInfo
                        downloadManager.resume(downloadInfo);
                        break;
                    case DownloadInfo.STATUS_DOWNLOADING:
                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                    case STATUS_WAIT:
                        //pause downloadInfo
                        downloadManager.pause(downloadInfo);
                        break;
                    case DownloadInfo.STATUS_COMPLETED:
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
                        Log.i("xinxi", "downloadInfo");
                        downloadInfo = null;
                        download();
                        break;

                }
            } else {
//            Create new download task
                Log.i("xinxi", "download");
                downloadFile();
            }
        }

        private void downloadFile() {
            String t = fileShare.getDateStr().substring(0, 4) + "/" + fileShare.getDateStr();
            String url = HttpUrl.Url.BASIC + "/userfiles/Planning/" + t + "/" + fileShare.getFileName();
            File d = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "e3lue");
            pbProgress.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            if (!d.exists()) {
                d.mkdirs();
            }
            String path = d.getAbsolutePath().concat("/").concat(fileShare.getFileName());
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
                                viewHolder.refresh();
                            }
                        }
                    });
            downloadManager.download(downloadInfo);
        }


        @OnClick(R.id.delete)
        public void delete() {
            delete.setVisibility(View.GONE);
            downloadManager.remove(downloadInfo);
            if (deleteFile(SDcardDir  + fileShare.getFileName())) {
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
