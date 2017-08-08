package com.e3lue.us.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.e3lue.us.R;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.viewpagerindicator.CirclePageIndicator;
import com.e3lue.us.utils.SharedPreferences;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.config.Config;

/**
 * Created by e3lue on 15/7/29.
 */
public class SplashActivity extends FragmentActivity {

    private Button btnHome;
    private CirclePageIndicator indicator;
    private ViewPager pager;
    private GalleryPagerAdapter adapter;
    private int[] images = {
            R.drawable.newer01,
            R.drawable.newer02,
            //R.drawable.newer03,
            //R.drawable.newer04
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        final boolean firstTimeUse = SharedPreferences.getInstance().getBoolean("first-time-use", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firstTimeUse) {
                    Animation fadeOut = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fadeout);
                    fadeOut.setFillAfter(true);
                    findViewById(R.id.guideImage).startAnimation(fadeOut);
                    initGuideGallery();
                } else {
                    //UIHelper.showLogin(SplashActivity.this);
                    login();
                }
            }
        }, 3000);
    }

    private void init() {
        try {
            //custom download database.
//      DBController dbController = DBController.getInstance(getApplicationContext());
            Config config = new Config();
            //set database path.
//    config.setDatabaseName("/sdcard/a/d.db");
//      config.setDownloadDBController(dbController);

            //set download quantity at the same time.
            config.setDownloadThread(3);

            //set each download info thread number
            config.setEachDownloadThread(2);

            // set connect timeout,unit millisecond
            config.setConnectTimeout(10000);

            // set read data timeout,unit millisecond
            config.setReadTimeout(10000);
            DownloadService.getDownloadManager(this.getApplicationContext(), config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGuideGallery() {
        final Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.getInstance().putBoolean("first-time-use", false);
                UIHelper.showLogin(SplashActivity.this);
                finish();
            }
        });

        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setVisibility(View.VISIBLE);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setVisibility(View.VISIBLE);

        adapter = new GalleryPagerAdapter();
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == images.length - 1) {
                    btnHome.setVisibility(View.VISIBLE);
                    btnHome.startAnimation(fadeIn);
                } else {
                    btnHome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView item = new ImageView(SplashActivity.this);
            item.setScaleType(ImageView.ScaleType.CENTER_CROP);
            item.setImageResource(images[position]);
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }

    public void login() {
        String usercode = SharedPreferences.getInstance().getString("UserCode", "");
        String pw = SharedPreferences.getInstance().getString("Pw", "");
        if (usercode.isEmpty() || pw.isEmpty() || usercode == "") {
            UIHelper.showLogin(SplashActivity.this);
            finish();
            return;
        }

        OkGo.<String>post(HttpUrl.Url.LOGIN)
                .tag(this)
                .params("txtUserName", usercode)
                .params("txtUserPWD", pw)
                .params("txtUserIP", "")
                .params("txtMac", "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            SharedPreferences.getInstance().putString("BaseUser", result.getData().toString());
                            UIHelper.showHome(SplashActivity.this);
                        } else {
                            Toast.makeText(SplashActivity.this, result.getMsg(), Toast.LENGTH_LONG).show();
                            UIHelper.showLogin(SplashActivity.this);
                        }
                        finish();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Toast.makeText(SplashActivity.this, "请检查网络状态", Toast.LENGTH_LONG).show();
                        UIHelper.showLogin(SplashActivity.this);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }
}
