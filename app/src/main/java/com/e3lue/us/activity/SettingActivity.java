package com.e3lue.us.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.e3lue.us.R;
import com.e3lue.us.common.AppManager;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.utils.SharedPreferences;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leo on 2017/4/19.
 */

public class SettingActivity extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.setting_sound)
    SwitchButton sound;

    @BindView(R.id.setting_vibration)
    SwitchButton vibration;

    @OnClick(R.id.setting_loginoff)
    public void loginoff() {
        new AlertView("是否退出", null, "取消", null,
                new String[]{"确认"},
                this, AlertView.Style.Alert, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                if (position > -1) {
                    SharedPreferences.getInstance().remove("UserCode");
                    SharedPreferences.getInstance().remove("Pw");
                    showToast("退出完成");
                    AppManager.getAppManager().AppExit(SettingActivity.this);
                }
            }
        }).show();
    }

    @OnClick(R.id.setting_changepw)
    public void changepw() {
        UIHelper.showChangePw(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("设置");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        boolean isSound = SharedPreferences.getInstance().getBoolean("IsSound",false);
        boolean isVibration = SharedPreferences.getInstance().getBoolean("IsVibration",false);
        sound.setChecked(isSound);
        vibration.setChecked(isVibration);

        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.getInstance().putBoolean("IsSound", isChecked);
            }
        });

        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.getInstance().putBoolean("IsVibration", isChecked);
            }
        });
    }

}
