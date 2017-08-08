package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.e3lue.us.R;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.ui.view.NumberProgressBar;
import com.e3lue.us.utils.RegexUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Leo on 2017/3/28.
 */

public class ContactPersonAddActivity extends SwipeBackActivity {

    public static final int REQUEST_CODE_SELECT1 = 100;
    public static final int REQUEST_CODE_SELECT2 = 101;

    private ImageItem CardPhotoOne; //名片正面
    private ImageItem CardPhotoTwo; //名片反面
    private File CardFileOne;
    private File CardFileTwo;
    private ArrayList<ImageItem> CardImageList;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.contactperson_new_name)
    EditText UserName;

    @BindView(R.id.contactperson_new_mobile)
    EditText Mobile;

    @BindView(R.id.contactperson_new_weixin)
    EditText WeiXin;

    @BindView(R.id.contactperson_new_qq)
    EditText QQ;

    @BindView(R.id.contactperson_new_note)
    EditText Note;

    @BindView(R.id.contactperson_new_man)
    RadioButton Sex;

    @BindView(R.id.contactperson_new_card_one)
    ImageView Cardone;

    @BindView(R.id.contactperson_new_card_two)
    ImageView Cardtwo;

    @BindView(R.id.contactperson_new_submit)
    Button submit;

    @BindView(R.id.contactperson_new_progress)
    NumberProgressBar progressBar;

    @OnClick(R.id.contactperson_new_card_one)
    public void cardone() {
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT1);
    }

    @OnClick(R.id.contactperson_new_card_two)
    public void cardtwo() {
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT2);
    }

    @OnClick(R.id.contactperson_new_submit)
    public void submit() {
        if (valid()) {
            submit.setVisibility(View.GONE);
            showProgress();
            if (validupload()) {
                CardImageList = new ArrayList<ImageItem>();
                CardImageList.add(CardPhotoOne);
                CardImageList.add(CardPhotoTwo);

                File file1 = new File(CardPhotoOne.path);
                File file2 = new File(CardPhotoTwo.path);
                CardFileOne = new CompressHelper.Builder(this)
                        .setMaxWidth(1280)
                        .setMaxHeight(960)
                        .setQuality(95)
                        .build()
                        .compressToFile(file1);
                CardFileTwo = new CompressHelper.Builder(this)
                        .setMaxWidth(1280)
                        .setMaxHeight(960)
                        .setQuality(95)
                        .build()
                        .compressToFile(file2);
            }
            Save();
        }
    }

    //Save
    public void Save() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Name", UserName.getText().toString().trim());
        params.put("Mobile", Mobile.getText().toString().trim());
        params.put("Sex", Sex.isChecked() ? "1" : "2");
        params.put("WeiXin",WeiXin.getText().toString().trim());
        params.put("QQ",QQ.getText().toString().trim());
        params.put("Note", Note.getText().toString());

        OkGo.<String>post(HttpUrl.Url.ContactPersonSave)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult r = HttpClient.GetResult(response.body());
                        if (r.getRet() == 0) {
                            if (RegexUtil.isNumeric(r.getMsg().toString())) {
                                submit.setVisibility(View.GONE);
                                int ID = Integer.parseInt(r.getMsg().toString());
                                if (validupload())
                                    Upload(ID);
                                else {
                                    showToast("联系人保存完成");
                                    finish();
                                }
                            } else {
                                progressDimss();
                                submit.setBackgroundResource(R.drawable.btn_orange);
                                submit.setEnabled(true);
                                showToast(r.getData().toString());
                            }
                        } else {
                            submit.setBackgroundResource(R.drawable.btn_orange);
                            submit.setEnabled(true);
                            Toast.makeText(ContactPersonAddActivity.this, r.getMsg(), Toast.LENGTH_LONG).show();
                        }
                        progressDimss();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        submit.setBackgroundResource(R.drawable.btn_orange);
                        submit.setEnabled(true);
                        progressDimss();
                    }
                });
    }

    //upload
    public void Upload(int ID) {
        submit.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        OkGo.<String>post(HttpUrl.Url.UPLOADFILE)
                .params("CpID", ID)
                .params("operation", "NameCard")
                .params("cardone", CardFileOne)
                .params("cardtwo", CardFileTwo)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        showToast("名片保存完成");
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

    //检查
    private boolean valid() {
        if (UserName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_LONG).show();
            return false;
        }
        if (Mobile.getText().toString().length() == 0) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validupload() {
        if (CardPhotoOne == null) {
            //Toast.makeText(this, "名片正面没有选择", Toast.LENGTH_LONG).show();
            return false;
        }
        if (CardPhotoTwo == null) {
            //Toast.makeText(this, "名片反面没有选择", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactperson_activity_new);
        ButterKnife.bind(this);

        //view
        textHeadTitle.setText("收集名片");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT1) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) {
                    CardPhotoOne = images.get(0); //正面
                    ImagePicker.getInstance().getImageLoader().displayImage(this, images.get(0).path, Cardone, 0, 0);
                }
            }
            if (data != null && requestCode == REQUEST_CODE_SELECT2) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) {
                    CardPhotoTwo = images.get(0); //反面
                    ImagePicker.getInstance().getImageLoader().displayImage(this, images.get(0).path, Cardtwo, 0, 0);
                }
            }
        }
    }
}
