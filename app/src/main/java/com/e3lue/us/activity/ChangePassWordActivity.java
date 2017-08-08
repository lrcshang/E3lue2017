package com.e3lue.us.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.SharedPreferences;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Leo on 2017/4/24.
 */

public class ChangePassWordActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.changepw_oldpw)
    EditText mOldpw;

    @BindView(R.id.changepw_newpw)
    EditText mNewpw;

    @BindView(R.id.changepw_newpw2)
    EditText mNewpw2;

    @BindView(R.id.changepw_btn)
    Button submit;

    @OnClick(R.id.changepw_btn)
    public void change() {
        if (valid()) {
            submit.setEnabled(false);
            showProgress();
            submit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("修改密码");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //提交修改
    public void submit() {
        String oldpw = mOldpw.getText().toString().trim();
        final String newpw = mNewpw.getText().toString().trim();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("oldpasswd", oldpw);
        params.put("newpasswd", newpw);
        OkGo.<String>post(HttpUrl.Url.LoggingChangePw)
                .tag(this)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult r = HttpClient.GetResult(response.body());
                        showToast(r.getMsg());
                        if (r.getRet() == 0) {
                            SharedPreferences.getInstance().putString("Pw", newpw);
                            finish();
                        } else {
                            submit.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        progressDimss();
                        showToast("网络好像开小差了~请检查");
                        submit.setEnabled(true);
                    }
                });
    }

    //检验
    public boolean valid() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);
        }
        if (mOldpw.getText().length() < 6) {
            showToast("旧密码不可以小于6个字符");
            return false;
        }

        if (mNewpw.getText().length() < 6) {
            showToast("新密码不可以小于6个字符");
            return false;
        }

        String p1 = mNewpw.getText().toString().trim();
        String p2 = mNewpw2.getText().toString().trim();
        if (!p1.equals(p2)) {
            showToast("两次输入的密码不一致");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }
}
