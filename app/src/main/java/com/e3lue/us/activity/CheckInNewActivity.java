package com.e3lue.us.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.e3lue.us.R;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.service.HeartService;
import com.e3lue.us.service.UploadService;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.ui.view.NumberProgressBar;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Leo on 2017/3/26.
 */

public class CheckInNewActivity extends SwipeBackActivity {

    BaiduMap mBaiduMap;
    boolean isFirstLoc = true;
    private LocationClient mLocationClient;

    private String noteStr = "";
    File checkin_picture;

    private ArrayList<ImageItem> images;
    private static final int PHOTO_GRAPH = 1;

    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.checkin_new_progress)
    NumberProgressBar progressBar;

    private String longitude = "";
    private String latitude = "";
    private String province = "";
    private String city = "";
    private String addr = "";
    @BindView(R.id.newcheckin_addr)
    TextView addrText;

    @BindView(R.id.newcheckin_info)
    EditText info;

    @BindView(R.id.newcheckin_mapview)
    MapView mMapView;


    @BindView(R.id.newcheckin_takepicture)
    Button tackpicture;
    private Intent intent;

    @OnClick(R.id.newcheckin_takepicture)
    public void submit() {
        if (!valid()) return;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "temp.jpg")));
        startActivityForResult(intent, PHOTO_GRAPH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_activity_new);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //view
        textHeadTitle.setText("签到");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        intent = new Intent(CheckInNewActivity.this, UploadService.class);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        LatLng cenpt = new LatLng(39.912897, 116.402724);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(5)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));
        mLocationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(6000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        mLocationClient.registerLocationListener(myListener);
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
    }

    @Override
    protected void onStop() {
        mLocationClient.unRegisterLocationListener(myListener);
        super.onStop();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
    }

    //检验获得地址
    public boolean valid() {
        noteStr = info.getText() == null ? "" : info.getText().toString();
        if (longitude.length() == 0 || longitude.toLowerCase().contains("null")) {
            showToast("获取地址失败,请确认打开位置权限,并联网.");
            tackpicture.setEnabled(true);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0)
            return;
        if (requestCode == PHOTO_GRAPH) {
            File picture = new File(Environment.getExternalStorageDirectory()
                    + "/temp.jpg");
            if (picture != null) {
//                checkin_picture = new CompressHelper.Builder(this)
//                        .setMaxWidth(1280)
//                        .setMaxHeight(960)
//                        .setQuality(95)
//                        .build()
//                        .compressToFile(picture);
                submitcheckin();
            } else {
                Toast.makeText(this, "获取照片失败.", Toast.LENGTH_LONG).show();
                tackpicture.setEnabled(true);
            }
        }
    }

    public void submitcheckin() {
        showProgress();
        tackpicture.setEnabled(false);
        OkGo.<String>post(HttpUrl.Url.CHECKINSAVE)
                .params("Province", province)
                .params("City", city)
                .params("Address", addr)
                .params("Note", noteStr)
                .params("Longitude", longitude)
                .params("Latitude", latitude)
                .params("Picture", "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        if (result.getRet() == 1) {
                            showToast(result.getMsg());
                            return;
                        }
                        showToast("签到成功,请再等待保存图片.");
                        intent.putExtra("data",result.getMsg());
                        startService(intent);
                        finish();
//                        uploadfile(result.getMsg());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        showToast("签到失败");
                    }
                });
    }

    public void uploadfile(final String ID) {
        tackpicture.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        OkGo.<String>post(HttpUrl.Url.UPLOADFILE)
                .params("CheckinID", ID)
                .params("operation", "CHECKIN")
                .params("file", checkin_picture)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        showToast("图片保存完成");
                        finish();
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }

                    @Override
                    public void uploadProgress(Progress progress) {

                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), progress.currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), progress.totalSize);
                        String speed = Formatter.formatFileSize(getApplicationContext(), progress.speed);
                        progressBar.setMax(10000);
                        progressBar.setProgress((int) (progress.fraction * 10000));
                    }

                });
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            if (province.length() == 0 || province.toLowerCase().contains("null"))
                province = location.getProvince();
            if (city.length() == 0 || city.toLowerCase().contains("null"))
                city = location.getCity();
            if (addr.length() == 0 || addr.toLowerCase().contains("null")) {
                addr = location.getAddrStr();
                addrText.setText(addr);
            }
            if (longitude.length() == 0 || longitude.toLowerCase().contains("null"))
                longitude = "" + location.getLongitude();
            if (latitude.length() == 0 || latitude.toLowerCase().contains("null"))
                latitude = "" + location.getLatitude();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
