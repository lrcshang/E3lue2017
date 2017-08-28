package com.e3lue.us.common;

import android.app.Application;
import android.os.Environment;
import android.support.multidex.MultiDex;
import org.wlf.filedownloader.FileDownloadConfiguration.Builder;
import com.baidu.mapapi.SDKInitializer;
import com.e3lue.us.utils.GlideImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.squareup.leakcanary.LeakCanary;

import org.wlf.filedownloader.FileDownloadConfiguration;
import org.wlf.filedownloader.FileDownloader;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

public class AppContext extends Application {

    private static AppContext app;
    public AppContext() {
        app = this;
    }

    public static synchronized AppContext getInstance() {
        if (app == null) {
            app = new AppContext();
        }
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);
        registerUncaughtExceptionHandler();
        SDKInitializer.initialize(getApplicationContext());//BaiDu

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build());

        initImagePicker();
        // 1、创建Builder
        Builder builder1 = new FileDownloadConfiguration.Builder(this);

// 2.配置Builder
// 配置下载文件保存的文件夹
        builder1.configFileDownloadDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "FileDownloader");
// 配置同时下载任务数量，如果不配置默认为2
        builder1.configDownloadTaskSize(4);
// 配置失败时尝试重试的次数，如果不配置默认为0不尝试
        builder1.configRetryDownloadTimes(5);
// 开启调试模式，方便查看日志等调试相关，如果不配置默认不开启
        builder1.configDebugMode(true);
// 配置连接网络超时时间，如果不配置默认为15秒
        builder1.configConnectTimeout(25000);// 25秒

// 3、使用配置文件初始化FileDownloader
        FileDownloadConfiguration configuration = builder1.build();
        FileDownloader.init(configuration);
    }
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(6);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    // 注册App异常崩溃处理器
    private void registerUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
    }
}