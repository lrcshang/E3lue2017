package com.e3lue.us.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.e3lue.us.R;
import com.e3lue.us.http.HttpClient;
import com.e3lue.us.model.BaseUser;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.SharedPreferences;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by tiansj on 15/7/31.
 */
public class LoginActivity extends SwipeBackActivity {

    @BindView(R.id.login_mobile)
    EditText mobile;

    @BindView(R.id.login_pw)
    EditText password;

    @BindView(R.id.login_btnSure)
    Button submit;

    @OnClick(R.id.login_btnClose)
    void cancel() {
        finish();
    }

    @OnClick(R.id.login_btnSure)
    void sure() {
        submit.setBackgroundResource(R.drawable.btn_gray);
        submit.setEnabled(false);
        final String usercode = mobile.getText().toString();
        final String pw = password.getText().toString();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("txtUserName", usercode);
        params.put("txtUserPWD", pw);
        params.put("txtUserIP", "");
        params.put("txtMac", "");

        OkGo.<String>post(HttpUrl.Url.LOGIN)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult r = HttpClient.GetResult(response.body());
                        if (r.getRet() == 0) {
                            Gson gson = new Gson();
                            BaseUser user = gson.fromJson(r.getData().toString(),BaseUser.class);
                            SharedPreferences.getInstance().putString("UserCode", usercode);
                            SharedPreferences.getInstance().putString("Pw", pw);
                            SharedPreferences.getInstance().putInt("UserID", user.getUserId());
                            SharedPreferences.getInstance().putString("BaseUser", r.getData().toString());
                            UIHelper.showHome(LoginActivity.this);
                        } else {
                            Toast.makeText(LoginActivity.this, r.getMsg(), Toast.LENGTH_LONG).show();
                            submit.setEnabled(true);
                            submit.setBackgroundResource(R.drawable.btn_orange);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        submit.setBackgroundResource(R.drawable.btn_orange);
                        submit.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        String usercode = SharedPreferences.getInstance().getString("UserCode", "");
        String pw = SharedPreferences.getInstance().getString("Pw", "");
        mobile.setText(usercode);
        password.setText(pw);
    }

}
