package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by Leo on 2017/5/25.
 */

public class DiaryMessageAddActivity extends SwipeBackActivity {

    private int diaryID;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.diary_detail_message_content)
    EditText content;

    @BindView(R.id.diary_detail_message_submit)
    Button saveBtn;

    @OnClick(R.id.diary_detail_message_submit)
    void click() {
        if (content.getText().length() == 0) {
            showToast("请填写留言内容");
            return;
        }
        saveBtn.setEnabled(false);
        showProgress();
        save();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_detail_message_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("留言");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        diaryID = getIntent().getIntExtra("ID", 0);
    }

    //保存
    public void save() {
        String msg = content.getText().toString().trim();
        OkGo.<String>post(HttpUrl.Url.DiaryMessageSave)
                .params("DiaryID", diaryID)
                .params("Message", msg)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = HttpClient.GetResult(response.body());
                        showToast(result.getMsg());
                        Intent intent = new Intent();
                        intent.setClass(DiaryMessageAddActivity.this, DiaryDetailActivity.class);
                        setResult(100, intent);
                        finish();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        finish();
                    }
                });
    }
}
