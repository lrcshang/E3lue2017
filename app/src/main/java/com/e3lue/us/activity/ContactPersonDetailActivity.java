package com.e3lue.us.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e3lue.us.R;
import com.e3lue.us.model.ContactPerson;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.model.NameCardFile;
import com.e3lue.us.ui.loopviewpager.AutoLoopViewPager;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.ui.viewpagerindicator.CirclePageIndicator;
import com.e3lue.us.utils.BitmapUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leo on 2017/4/13.
 */

public class ContactPersonDetailActivity extends SwipeBackActivity {

    private List<String> imageList = new ArrayList<String>();
    Gson gson;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.contactperson_detail_pager)
    AutoLoopViewPager pager;
    @BindView(R.id.contactperson_detail_indicator)
    CirclePageIndicator indicator;
    private ContactPersonDetailActivity.GalleryPagerAdapter galleryAdapter;

    @BindView(R.id.contactperson_detail_name)
    TextView ContactPersonName;

    @BindView(R.id.contactperson_detail_status)
    TextView ContactPersonStatus;

    @BindView(R.id.contactperson_detail_company)
    TextView ContactPersonCompany;

    @BindView(R.id.contactperson_detail_mobile)
    TextView ContactPersonMobile;

    @OnClick(R.id.contactperson_detail_mobile)
    public void click() {
        if (ContactPersonMobile.getText().toString().length() > 0) {
            Intent myInt = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + ContactPersonMobile.getText()));
            startActivity(myInt);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_person_detail);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    void initView() {
        textHeadTitle.setText("个人信息");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initData() {
        int cpid = getIntent().getIntExtra("cpid", 0);
        if (cpid == 0) {
            showToast("未找到联系人");
            finish();
        }
        gson = new Gson();

        //联系人数据
        OkGo.<String>post(HttpUrl.Url.ContactPersonDetail)
                .params("CpID", cpid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            ContactPerson cp = gson.fromJson(result.getData().toString(), ContactPerson.class);
                            if (cp != null) okPersonData(cp);
                        } else {
                            showToast(result.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        showToast("网络连接失败,请重试.");
                    }
                });
        //名片数据
        OkGo.<String>post(HttpUrl.Url.ContactPersonCards)
                .params("CpID", cpid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            List<NameCardFile> list = gson.fromJson(
                                    result.getData().toString(),
                                    new TypeToken<ArrayList<NameCardFile>>() {
                                    }.getType());
                            if (list != null && list.size() > 0) okNameCardData(list);
                        } else {
                            showToast(result.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        showToast("网络连接失败,请重试.");
                    }
                });
    }

    //联系人
    public void okPersonData(ContactPerson person) {
        ContactPersonName.setText(person.getName());
        ContactPersonCompany.setText(person.getCompanyName());
        ContactPersonMobile.setText(person.getMobile());
        if (person.getState() < 2)
            ContactPersonStatus.setText(person.getStateName());
    }


    public void okNameCardData(List<NameCardFile> cards) {
        String url;
        for (NameCardFile file : cards) {
            url = BitmapUtil.ImgPath("NameCard", file.getDateStr(), file.getImgUrl());
            imageList.add(url);
        }
        galleryAdapter = new ContactPersonDetailActivity.GalleryPagerAdapter();
        pager.setAdapter(galleryAdapter);
        indicator.setViewPager(pager);
        indicator.setPadding(5, 5, 10, 5);
    }

    //轮播图适配器
    public class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView item = new ImageView(ContactPersonDetailActivity.this);
            Glide.with(ContactPersonDetailActivity.this)
                    .load(imageList.get(position))
                    .thumbnail(0.5f)//先显示缩略图  缩略图为原图的1/10
                    .error(R.drawable.nocard)
                    .into(item);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            item.setLayoutParams(params);
            item.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(item);

            final int pos = position;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ContactPersonDetailActivity.this, ImageViewerActivity.class);
                    intent.putStringArrayListExtra("list", (ArrayList<String>) imageList);
                    intent.putExtra("position", pos);
                    startActivity(intent);
                }
            });

            return item;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }
}
