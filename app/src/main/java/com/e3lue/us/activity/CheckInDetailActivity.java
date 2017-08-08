package com.e3lue.us.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.e3lue.us.R;
import com.e3lue.us.model.CheckIn;
import com.e3lue.us.model.GpsRecord;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.model.UserCheckInfo;
import com.e3lue.us.service.UploadService;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.BitmapUtil;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Leo on 2017/4/19.
 */

public class CheckInDetailActivity extends SwipeBackActivity {

    BaiduMap mBaiduMap;
    private ArrayList<String> imagelist;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;


    @BindView(R.id.checkin_detail_map)
    MapView map;

    @BindView(R.id.checkin_detail_user)
    TextView user;

    @BindView(R.id.checkin_detail_date)
    TextView date;

    @BindView(R.id.checkin_detail_addr)
    TextView addr;

    @BindView(R.id.checkin_detail_info)
    TextView note;

    @BindView(R.id.checkin_detail_img)
    ImageView img;
    @BindView(R.id.error_icon)
    ImageView errorimage;
    Intent intent;
    CheckIn entity;
    private static final int PHOTO_GRAPH = 0x02;
    public static final String ACTION = "com.e3lue.us.activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_activity_detail);
        ButterKnife.bind(this);

        textHeadTitle.setText("签到信息");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        entity = (CheckIn) intent.getSerializableExtra("entity");
        if (entity.getIsshow()) {
            errorimage.setVisibility(View.VISIBLE);
        }
        initDate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onResume() {
        map.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        map.onPause();
        super.onPause();
    }

    public void initDate() {
        intent = new Intent(CheckInDetailActivity.this, UploadService.class);
        imagelist = new ArrayList<String>();

        mBaiduMap = map.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));

        getGps("" + entity.getGpsID());
        user.setText(entity.getUserName());
        date.setText(entity.getCreateTime());
        addr.setText(entity.getAddress());
        note.setText(entity.getNote());
        if (entity.getPicture().trim().length() > 0) {
            String url = BitmapUtil.ImgPath("CheckIn",entity.getDateStr().substring(0,4)+entity.getPicture().substring(4,8), entity.getPicture());
            imagelist.add(url);
            Glide.with(this)
                    .load(url)
                    .into(img);
            Log.i("xinxi", url);
            img.setOnClickListener(clickListener);
        }
    }

    public void getGps(String id) {
        OkGo.<String>post(HttpUrl.Url.GETGPS)
                .params("id", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            GpsRecord gps = gson.fromJson(result.getData().toString(), GpsRecord.class);
                            if (gps.getLatitude() != "" && !gps.getLatitude().equals("null")) {
                                LatLng point = new LatLng(Double.valueOf(gps.getLatitude()), Double.valueOf(gps.getLongitude()));
                                BitmapDescriptor bitmap = null;
                                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.gps);
                                OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                                mBaiduMap.addOverlay(option);
                                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        showToast("获取gps信息失败了,检查网络.");
                    }
                });
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (errorimage.getVisibility() == View.VISIBLE) {
                submit();

            } else {
                Intent intent = new Intent(CheckInDetailActivity.this, ImageViewerActivity.class);
                intent.putStringArrayListExtra("list", imagelist);
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        }
    };
    public BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            getImage("" + entity.getID());
            errorimage.setVisibility(View.GONE);
        }

    };

    public void getImage(String id) {
        OkGo.<String>post(HttpUrl.Url.CHECKINGET)
                .params("ID", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            UserCheckInfo userCheckInfo = gson.fromJson(result.getData().toString(), UserCheckInfo.class);
//                            Log.i("xinxi", result.getData().toString());
                            Log.i("xinxi", result.getData().toString());
                            if (userCheckInfo.getPicture().trim().length() > 0) {
                                String url = BitmapUtil.ImgPath("CheckIn",userCheckInfo.getDateStr().substring(0,4)+userCheckInfo.getPicture().substring(4,8), userCheckInfo.getPicture());
                                imagelist.clear();
                                imagelist.add(url);
                                Glide.with(CheckInDetailActivity.this)
                                        .load(url)
                                        .into(img);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        showToast("获取gps信息失败了,检查网络.");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        map.onDestroy();
        map = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0)
            return;
        if (requestCode == PHOTO_GRAPH) {
            File picture = new File(Environment.getExternalStorageDirectory()
                    + "/temp.jpg");
            if (picture != null) {
                intent.putExtra("data", String.valueOf(entity.getID()).toString());
                startService(intent);
            } else {
                Toast.makeText(this, "获取照片失败.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void submit() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "temp.jpg")));
        startActivityForResult(intent, PHOTO_GRAPH);
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}
