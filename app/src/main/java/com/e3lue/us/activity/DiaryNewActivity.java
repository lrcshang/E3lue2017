package com.e3lue.us.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.e3lue.us.model.ContactPerson;
import com.e3lue.us.model.Expenses;
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
 * Created by Leo on 2017/4/11.
 */

public class DiaryNewActivity extends SwipeBackActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int REQUEST_CODE_Contact = 102;
    public static final int REQUEST_CODE_Expenses = 103;
    public static final int IMAGE_ITEM_ADD = -1;

    private ArrayList<ImageItem> selImageList;

    private int maxImgCount = 5;
    private LocationClient mLocationClient;
    public DiaryNewActivity.BaiduListenner myListener = new DiaryNewActivity.BaiduListenner();
    private ImagePickerAdapter adapter;

    public int SelectedContactID;
    public String SelectedExpenses;
    public List<File> SelectedFile;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    private String longitude = "";
    private String latitude = "";
    private String province = "";
    private String city = "";
    private String addr = "";

    @BindView(R.id.Diary_New_address)
    TextView address;

    @BindView(R.id.Diary_New_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.Diary_New_content)
    EditText content;

    @BindView(R.id.diary_new_progress)
    NumberProgressBar progressBar;

    @BindView(R.id.Diary_New_submit)
    Button submit;

    @OnClick(R.id.Diary_New_submit)
    public void submit() {
        if (valid()) {
            submit.setVisibility(View.GONE);
            //收起键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                        0);
            }
            if (selImageList.size() == 0) {
                save();
            } else {
                zip();
            }
        }
    }

    @BindView(R.id.Diary_New_ContactPersonName)
    TextView ContactPerson;

    @OnClick(R.id.Diary_New_ContactPersonName)
    public void contact() {
        Intent intent = new Intent(this, ContactPersonSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE_Contact);
    }

    @BindView(R.id.Diary_New_Expenses)
    TextView ExpensesTitle;

    @OnClick(R.id.Diary_New_Expenses)
    public void expenses() {
        Intent intent = new Intent(this, ExpensesSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE_Expenses);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_new_activity);
        ButterKnife.bind(this);

        baiduMap();
        initPhoto();

        //view
        textHeadTitle.setText("写日记");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SelectedContactID = 0;
        SelectedExpenses = "";
        SelectedFile = new ArrayList<File>();
        selImageList = new ArrayList<ImageItem>();

    }

    public class BaiduListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            if (province.length() == 0 || province.toLowerCase().contains("null"))
                province = location.getProvince();
            if (city.length() == 0 || city.toLowerCase().contains("null"))
                city = location.getCity();
            if (addr.length() == 0 || addr.toLowerCase().contains("null")) {
                addr = location.getAddrStr();
                address.setText(addr);
            }
            if (longitude.length() == 0 || longitude.toLowerCase().contains("null"))
                longitude = "" + location.getLongitude();
            if (latitude.length() == 0 || latitude.toLowerCase().contains("null"))
                latitude = "" + location.getLatitude();
        }
    }

    //百度定位初始化
    private void baiduMap() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(6000);
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
        if (SelectedContactID == 0) {
            showToast("请选择拜访对象");
            return false;
        }
        if (content.getText().length() == 0) {
            showToast("填写日记内容");
            return false;
        }
        return true;
    }

    //保存单据
    public void save() {
        OkGo.<String>post(HttpUrl.Url.DIARYSAVE)
                .params("Province", province)
                .params("City", city)
                .params("Address", addr)
                .params("Longitude", longitude)
                .params("Latitude", latitude)
                .params("Content", content.getText().toString())
                .params("CpID", SelectedContactID)
                .params("Expenses", SelectedExpenses)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        Gson gson = new Gson();
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            if (SelectedFile.size() > 0) {
                                showToast(result.getMsg()+",正在上传图片");
                                upload(result.getData().toString());
                            } else {
                                saveok();
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

    //压缩
    public void zip() {
        showProgress();
        for (ImageItem item : selImageList) {
            File file = new CompressHelper.Builder(this)
                    .setMaxWidth(1280)
                    .setMaxHeight(960)
                    .setQuality(95)
                    .build()
                    .compressToFile(new File(item.path));
            SelectedFile.add(file);
        }
        save();
    }

    //保存图片
    public void upload(String id) {
        int eid = Double.valueOf(id).intValue();
        progressBar.setVisibility(View.VISIBLE);
        OkGo.<String>post(HttpUrl.Url.UPLOADFILE)
                .params("DiaryID", eid)
                .params("operation", "Diary")
                .addFileParams("file", SelectedFile)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        showToast("图片保存完成");
                        saveok();
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }

                    @Override
                    public void uploadProgress(Progress progress) {

                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), progress.currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), progress.totalSize);
                        //tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String speed = Formatter.formatFileSize(getApplicationContext(), progress.speed);
                        //tvNetSpeed.setText(String.format("%s/s", speed));
                        //tvProgress.setText(numberFormat.format(progress.fraction));
                        progressBar.setMax(10000);
                        progressBar.setProgress((int) (progress.fraction * 10000));
                    }

                });
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

    public void saveok() {
        showToast("日记保存完成");
        Intent intent = new Intent();
        intent.setClass(this, DiaryDayActivity.class);
        setResult(100, intent);
        finish();
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
        } else if (data != null && resultCode == REQUEST_CODE_Contact) {
            Bundle bundle = data.getExtras();
            ContactPerson contact = (ContactPerson) bundle.get("contact");
            SelectedContactID = contact.getID();
            String cname = contact.getCompanyName() == null ? "" : contact.getCompanyName();
            ContactPerson.setText(Html.fromHtml("<font color='#424242'><big>" + contact.getName() + "</big></font> &#160;<font color='gray' ><small>" + cname + "</small></font>"));
        } else if (data != null && resultCode == REQUEST_CODE_Expenses) {
            Bundle bundle = data.getExtras();
            ArrayList<Expenses> list = (ArrayList<Expenses>) bundle.getSerializable("expenses");
            String title = "";

            for (Expenses entity : list) {
                SelectedExpenses += entity.getID() + ",";
                title += "<font color='red'><big>" + entity.getAmount() + "元</big></font>&#160;<font color='gray'><small>" + entity.getNote() + "&#160;(" + entity.getCreateTime() + ")</small></font> &#160;";
            }
            if (SelectedExpenses.length() > 0) {
                SelectedExpenses = SelectedExpenses.substring(0, SelectedExpenses.length() - 1);
            }
            ExpensesTitle.setText(Html.fromHtml(title));
        }
    }
}
