package com.e3lue.us.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.e3lue.us.R;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Leo on 2017/5/27.
 */

public class GameClubMapActivity extends SwipeBackActivity implements View.OnClickListener , OnGetDistricSearchResultListener {

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.gameclub_map)
    MapView mMapView;
    LatLng point;//经纬度点
    BDLocation location;
    BaiduMap mBaiduMap;
    boolean isFirstLoc = true;// 是否首次定位
    List<Map<String, String>> coverArray = new ArrayList<Map<String, String>>();
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameclub_map_activity);
        ButterKnife.bind(this);
        textHeadTitle.setText("电竞馆地图");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
    }
    BitmapDescriptor mCurrentMarker;
    private void init() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this.getApplicationContext());
        LatLng cenpt = new LatLng(39.912897, 116.402724);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(5)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
//        mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.gps);
        for (int i = 0; i < 6; i++)
            biaoJi(39.963175 + i * 3);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            /**
             * 地图 Marker 覆盖物点击事件监听函数
             * @param marker 被点击的 marker
             */
            @Override
            public boolean onMarkerClick(Marker marker) {
                showHeaddialog();
                point = marker.getPosition();
                return false;
            }
        });
        InitLocation();
        completeLis();
    }
    @Override
    public void onGetDistrictResult(DistrictResult districtResult) {
        // mBaiduMap.clear();
        if (districtResult == null) {
            return;
        }
        if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<List<LatLng>> polyLines = districtResult.getPolylines();
            if (polyLines == null) {
                return;
            }
            // LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (List<LatLng> polyline : polyLines) {
                OverlayOptions ooPolygon = new PolygonOptions().points(polyline).stroke(new Stroke(5, 0xAA00FF88))
                        .fillColor(0xAAFFFF00);
                mBaiduMap.addOverlay(ooPolygon);
            }
//            if (aa<address.length) {
//                mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(city).districtName(address[aa]));
//                aa++;
//            }
        }
    }
    private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
//		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();  //开始定位
    }
    public void completeLis(){
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                // TODO Auto-generated method stub
//                Toast.makeText(GameClubMapActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                // TODO Auto-generated method stub
                LatLng latlng = mBaiduMap.getMapStatus().target;
                Toast.makeText(GameClubMapActivity.this, latlng.latitude+"   " +latlng.longitude+"    "+ mapStatus.target.longitude, Toast.LENGTH_LONG).show();
                System.out.println("*****************lat = " + latlng.latitude+"   "+location.getLatitude());
                System.out.println("*****************lng = " + latlng.longitude+"   "+location.getLongitude());
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    public class  MyLocationListenner implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            GameClubMapActivity.this.location = location;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);    //设置定位数据

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng cenpt = new LatLng(location.getLatitude(), location.getLongitude());
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(cenpt)
                        .zoom(17)
                        .build();
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                mBaiduMap.setMapStatus(mMapStatusUpdate);
//                LatLng ll = new LatLng(location.getLatitude(),
//                        location.getLongitude());
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);    //设置地图中心点以及缩放级别
//					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//                mBaiduMap.animateMapStatus(u);
            }
        }
    };
    Dialog headDialog;
    public void showHeaddialog() {
        headDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_mapview, null);
        //初始化控件
        initDialogView(inflate);
        //将布局设置给Dialog
        headDialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = headDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;//设置Dialog距离底部的距离
        lp.width = getResources().getDisplayMetrics().widthPixels;
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        headDialog.show();//显示对话框
    }

    private void initDialogView(View inflate) {
        LinearLayout baidu = (LinearLayout) inflate.findViewById(R.id.baidu);
        LinearLayout gaode = (LinearLayout) inflate.findViewById(R.id.gaode);
        baidu.setOnClickListener(this);
        gaode.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baidu:
                if (checkApkExist(getApplicationContext(), "com.baidu.BaiduMap")) {
                    Intent i1 = new Intent();
                    i1.setData(Uri.parse("baidumap://map/marker?location=" + point.latitude + "," + point.longitude + "&title=宜博&content=宜博电竞馆&traffic=on"));
                    startActivity(i1);
                    Toast.makeText(GameClubMapActivity.this, point.longitude + "  " + point.latitude, Toast.LENGTH_SHORT).show();
                } else
                    notMapApp();
//                    Toast.makeText(GameClubMapActivity.this, "未安装百度地图", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gaode:
                if (checkApkExist(getApplicationContext(), "com.autonavi.minimap"))
                    openGaoDeMap();
                else
                    notMapApp();
//                    Toast.makeText(GameClubMapActivity.this, "未安装高德地图", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void notMapApp() {
        // 坐标1
        double mLat1 = location.getLatitude();
        double mLon1 = location.getLongitude();
        //坐标2
        double mLat2 = point.latitude;
        double mLon2 = point.longitude;
        LatLng pt_start = new LatLng(mLat1, mLon1);
        LatLng pt_end = new LatLng(mLat2, mLon2);
        // 构建 route搜索参数以及策略，起终点也可以用name构造
        RouteParaOption para = new RouteParaOption()
                .startPoint(pt_start)
                .endPoint(pt_end)
                .busStrategyType(RouteParaOption.EBusStrategyType.bus_recommend_way);
        try {
            BaiduMapRoutePlan.openBaiduMapTransitRoute(para, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //结束调启功能时调用finish方法以释放相关资源
        BaiduMapRoutePlan.finish(this);
    }

    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void openGaoDeMap() {
        try {
            Intent intent = Intent.getIntent("androidamap://viewMap?sourceApplication=宜博&poiname=宜博电竞馆&lat=" + point.latitude + "&lon=" + point.longitude + "&dev=0");
            startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void biaoJi(double a) {
        //定义Maker坐标点
        LatLng point = new LatLng(a, 116.400244);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.gps);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions optio = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(optio);
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mLocationClient.registerLocationListener(myListener);
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mLocationClient.unRegisterLocationListener(myListener);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 关闭定位图层
        mLocationClient.stop();
        if (mMapView != null) {
            mBaiduMap.setMyLocationEnabled(false);
            mMapView.onDestroy();
            mMapView = null;
        }
        // 回收 bitmap 资源
        //bitmap.recycle();
        super.onDestroy();
    }
}
