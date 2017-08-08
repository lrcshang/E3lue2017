
package com.e3lue.us.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.e3lue.us.R;
import com.e3lue.us.fragment.MainPagerFragment;
import com.e3lue.us.fragment.MemberFragment;
import com.e3lue.us.fragment.MessageFragment;
import com.e3lue.us.fragment.TongXunLuFragment;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.service.HeartService;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.popmenu.MoreWindow;
import com.e3lue.us.utils.DownloadAppUtils;
import com.e3lue.us.utils.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.bugly.Bugly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseFragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CURR_INDEX = "currIndex";
    private static int currIndex = 0;

    private RadioGroup group;

    private ArrayList<String> fragmentTags;
    private FragmentManager fragmentManager;

    //弹出菜单
    MoreWindow mMoreWindow;
    private TextView checkin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        initData(savedInstanceState);
        Bugly.init(getApplicationContext(), "5c986dcc37", false);//Tencent
        initView();
        getAPPServerVersion();//检查更新
        Heart();//心跳
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURR_INDEX, currIndex);
    }

    private void initData(Bundle savedInstanceState) {
        List<String>list=new ArrayList<>();
        list.add("0000000000");
        if (SharedPreferences.getInstance().getDataList("ERRORID").size()<=0)
        SharedPreferences.getInstance().setDataList("ERRORID",list);
        fragmentTags = new ArrayList<>(Arrays.asList("HomeFragment", "ImFragment", "InterestFragment", "MemberFragment"));
        currIndex = 0;
        if (savedInstanceState != null) {
            currIndex = savedInstanceState.getInt(CURR_INDEX);
            hideSavedFragment();
        }
    }

    private void hideSavedFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if (fragment != null) {
            fragmentManager.beginTransaction().hide(fragment).commit();
        }
    }

    private void initView() {
        group = (RadioGroup) findViewById(R.id.group);
        checkin=(TextView)findViewById(R.id.check_text);
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showCheckInNew(MainActivity.this);
            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.foot_bar_home:
                        currIndex = 0;
                        break;
                    case R.id.foot_bar_im:
                        currIndex = 1;
                        break;
                    case R.id.foot_bar_add:
                        group.clearCheck();
                        currIndex = -1;
                        break;
                    case R.id.foot_bar_interest:
                        currIndex = 2;
                        break;
                    case R.id.main_footbar_user:
                        currIndex = 3;
                        break;
                    default:
                        break;
                }
                showFragment();
            }
        });
        showFragment();
    }

    private void showFragment() {
        //显示弹出菜单
        if (currIndex == -1) {
            View view = findViewById(R.id.foot_bar_add);
            showMoreWindow(view);
            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if (fragment == null) {
            fragment = instantFragment(currIndex);
        }
        for (int i = 0; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if (f != null && f.isAdded()) {
                fragmentTransaction.hide(f);
            }
        }
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0:
                return new MainPagerFragment();
            case 1:
                return new MessageFragment();
            case 2:
                return new TongXunLuFragment();
            case 3:
                return new MemberFragment();
            default:
                return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view, 100);
    }

    //检查服务器新版本
    private void getAPPServerVersion() {
        OkGo.<String>get(HttpUrl.Url.UPDATE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        Map<String, String> ret = gson.fromJson(response.body(),
                                new TypeToken<Map<String, String>>() {
                                }.getType());
                        if (ret != null) {
                            int nCode = Integer.parseInt(ret.get("Code"));
                            int cCode = com.e3lue.us.utils.Utils.getAPPLocalVersion(MainActivity.this);
                            if (nCode > cCode) {
                                Update(ret.get("Msg"), ret.get("Url"));
                            }
                        }
                    }
                });
    }

    private void Update(String content, final String url) {
        new AlertView("是否更新", content, "取消", null,
                new String[]{"确认"},
                this, AlertView.Style.Alert, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                if (position > -1) {
                    DownloadAppUtils.downloadForAutoInstall(MainActivity.this, url, "E3lue", "E3lue Update");
                }
            }
        }).show();
    }

    private void Heart() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, HeartService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
                0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP ,
                System.currentTimeMillis(), 10*1000, pendingIntent);
    }
}
