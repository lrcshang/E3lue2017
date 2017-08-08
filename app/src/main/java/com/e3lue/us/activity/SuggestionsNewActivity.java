package com.e3lue.us.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.e3lue.us.R;
import com.e3lue.us.adapter.ImagePickerAdapter;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.ui.view.NumberProgressBar;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Leo on 2017/4/14.
 */

public class SuggestionsNewActivity extends SwipeBackActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int IMAGE_ITEM_ADD = -1;

    private String province, city, longitude, latitude;
    private ArrayList<ImageItem> selImageList;

    private int maxImgCount = 10;
    private LocationClient mLocationClient;
    public BaiduListenner myListener = new BaiduListenner();
    private ImagePickerAdapter adapter;


    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.Suggestions_New_address)
    TextView address;

    @BindView(R.id.Suggestions_New_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.suggestions_new_progress)
    NumberProgressBar progressBar;

    @BindView(R.id.Suggestions_New_info)
    EditText info;

    @BindView(R.id.Suggestions_New_submit)
    Button submit;

    @OnClick(R.id.Suggestions_New_submit)
    public void submit() {
        if (valid()) {
            submit.setVisibility(View.GONE);
            //收起键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                        0);
            }
            save();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestions_new_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("我有建议或意见");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        baiduMap();
        initPhoto();
    }

    //保存信息
    public void save() {
        showProgress();
        OkGo.<String>post(HttpUrl.Url.SuggestionsSave)
                .params("Province", province)
                .params("City", city)
                .params("Address", address.getText().toString())
                .params("Content", info.getText().toString())
                .params("Longitude", longitude)
                .params("Latitude", latitude)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //progressDimss();
                        Gson gson = new Gson();
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            if (selImageList.size() > 0)
                                upload(result.getData().toString());
                            else {
                                showToast("提交完成");
                                finish();
                            }
                        } else {
                            submit.setVisibility(View.VISIBLE);
                            showToast(result.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        showToast("网络连接失败,请重试.");
                        submit.setVisibility(View.VISIBLE);
                    }
                });
    }

    //保存图片
    public void upload(String id) {
        int eid = Double.valueOf(id).intValue();

        List<File> files = new ArrayList<File>();

        for (ImageItem item : selImageList) {
            File file = new CompressHelper.Builder(this)
                    .setMaxWidth(1280)
                    .setMaxHeight(960)
                    .setQuality(95)
                    .build()
                    .compressToFile(new File(item.path));
            files.add(file);
        }

        progressBar.setVisibility(View.VISIBLE);
        OkGo.<String>post(HttpUrl.Url.UPLOADFILE)
                .params("InfoID", eid)
                .params("operation", "Suggestions")
                .addFileParams("file", files)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        showToast("提交完成");
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

    public boolean valid() {
        if (address.getText().toString().trim().length() == 0 || address.getText().toString().contains("null")) {
            showToast("请打开GPS.");
            return false;
        }
        if (info.getText().length() == 0) {
            showToast("请填写内容");
            return false;
        }
        return true;
    }

    //百度定位初始化
    private void baiduMap() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(900);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    //相册初始化
    private void initPhoto() {
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, IMAGE_ITEM_ADD, 6);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public class BaiduListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            province = location.getProvince();
            city = location.getCity();
            address.setText("" + location.getAddrStr());
            longitude = "" + location.getLongitude();
            latitude = "" + location.getLatitude();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                selImageList.clear();
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            }
        }
    }
}
