package com.e3lue.us.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
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
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leo on 2017/4/7.
 */

public class ExpensesNewActivity extends SwipeBackActivity implements  ImagePickerAdapter.OnRecyclerViewItemClickListener {

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int IMAGE_ITEM_ADD = -1;

    ArrayList<ImageItem> selImageList;
    private int maxImgCount = 5;
    private LocationClient mLocationClient;
    public BaiduListenner myListener = new BaiduListenner();
    private ImagePickerAdapter adapter;

    List<String> ListType1;
    List<String> ListType2;
    List<String> ListType3;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.Expenses_New_Type1)
    TagFlowLayout Type1;

    @BindView(R.id.Expenses_New_Type2)
    TagFlowLayout Type2;

    String province, city, longitude, latitude;

    @BindView(R.id.Expenses_New_address)
    TextView address;

    @BindView(R.id.Expenses_New_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.expenses_new_progress)
    NumberProgressBar progressBar;

    @BindView(R.id.Expenses_New_amount)
    EditText amount;

    @BindView(R.id.Expenses_New_info)
    EditText info;

    @BindView(R.id.Expenses_New_submit)
    Button submit;

    @OnClick(R.id.Expenses_New_submit)
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
        setContentView(R.layout.expenses_new_activity);
        ButterKnife.bind(this);

        init();
        baiduMap();
        initPhoto();

        //view
        textHeadTitle.setText("保存报销单");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        selImageList = new ArrayList<>();
    }

    public void init() {
        final LayoutInflater mInflater = LayoutInflater.from(this);
        ListType1 = new ArrayList<String>();
        ListType1.add("差旅");
        ListType1.add("行政");
        ListType2 = new ArrayList<String>();
        ListType2.add("交通");
        ListType2.add("住宿");
        ListType2.add("餐饮");
        ListType2.add("其他");
        ListType3 = new ArrayList<String>();
        ListType3.add("办公用品");
        ListType3.add("电脑");
        ListType3.add("其他");

        Type1.setMaxSelectCount(1);
        Type1.setAdapter(new TagAdapter<String>(ListType1) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv,
                        Type1, false);
                tv.setText(s);
                return tv;
            }
        });

        Type1.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {

                Type2.setAdapter(new TagAdapter<String>(position == 0 ? ListType2 : ListType3) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv,
                                Type2, false);
                        tv.setText(s);
                        return tv;
                    }
                });
                return true;
            }
        });
        Type2.setMaxSelectCount(1);
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

    public boolean valid() {
        if (address.getText().toString().trim().length() == 0 || address.getText().toString().contains("null")) {
            showToast("请打开GPS.");
            return false;
        }
        if (info.getText().length() == 0) {
            showToast("填写报销内容");
            return false;
        }
        if (amount.getText().length() == 0) {
            showToast("请填写金额");
            return false;
        }
        if (Type1.getSelectedList().size() == 0) {
            showToast("请选择大类别.");
            return false;
        }
        if (Type2.getSelectedList().size() == 0) {
            showToast("请选择小类别.");
            return false;
        }
        if (selImageList.size() == 0) {
            showToast("请选择报销图片.");
            return false;
        }
        return true;
    }

    //保存单据
    public void save() {
        String type1 = ListType1.get((int) Type1.getSelectedList().toArray()[0]);
        String type2 = type1.equals("差旅") ? ListType2.get((int) Type2.getSelectedList().toArray()[0]) : ListType3.get((int) Type2.getSelectedList().toArray()[0]);
        OkGo.<String>post(HttpUrl.Url.EXPENSESSAVE)
                .params("Province", province)
                .params("City", city)
                .params("Address", address.getText().toString())
                .params("Note", info.getText().toString())
                .params("Longitude", longitude)
                .params("Latitude", latitude)
                .params("Type1", type1)
                .params("Type2", type2)
                .params("Amount", amount.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        Gson gson = new Gson();
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            upload(result.getData().toString());
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
        progressBar.setVisibility(View.VISIBLE);
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
                .params("ExpensesID", eid)
                .params("operation", "Expenses")
                .addFileParams("file", files)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        showToast("报销单保存完成");
                        finish();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        showToast("报销单保存失败");
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

    public class BaiduListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            if (province == null || province.isEmpty()) {
                province = "" + location.getProvince();
                city = "" + location.getCity();
                address.setText("" + location.getAddrStr());
                longitude = "" + location.getLongitude();
                latitude = "" + location.getLatitude();
            }
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
