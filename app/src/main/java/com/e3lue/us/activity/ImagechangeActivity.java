package com.e3lue.us.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.e3lue.us.R;
import com.e3lue.us.common.AppContext;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.ui.loopviewpager.AutoLoopViewPager;
import com.e3lue.us.utils.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagechangeActivity extends Activity {
    private GalleryPagerAdapter galleryAdapter;
    //    int image[] = {R.drawable.ic_img_profile_bg, R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5
//            , R.drawable.bg6, R.drawable.bg7, R.drawable.bg8, R.drawable.bg9};
//    List<Bitmap> images;
    List<String> filelists;
    Bitmap bitmap;
    @BindView(R.id.text)
    TextView title;
    private PhotoViewAttacher mAttacher;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgchange);
        ButterKnife.bind(this);
        imageView = (ImageView) findViewById(R.id.sure);
//        images = new ArrayList<>();
        filelists = getIntent().getStringArrayListExtra("filelists");
        pos = getIntent().getIntExtra("getPosition", 0);
        initViewPager();

    }

    ImageView imageView;
    int pos = 0;

    private void initViewPager() {
        AutoLoopViewPager viewPager = (AutoLoopViewPager) findViewById(R.id.viewPager);
        viewPager.stopAutoScroll(-1);
        title.setText(filelists.get(pos).substring(filelists.get(pos).lastIndexOf('/') + 1));
        imageView.setVisibility(View.GONE);
        viewPager.setPageMargin(40);
        galleryAdapter = new GalleryPagerAdapter();
        viewPager.setAdapter(galleryAdapter);
        viewPager.setCurrentItem(pos);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                title.setText(filelists.get(pos).substring(filelists.get(pos).lastIndexOf('/') + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //轮播图适配器
    public class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return filelists.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView item = new PhotoView(ImagechangeActivity.this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
            item.setLayoutParams(params);
            item.setScaleType(ImageView.ScaleType.CENTER);
//            item.setImageBitmap(images.get(position));
            mAttacher = new PhotoViewAttacher(item);
            Glide.with(ImagechangeActivity.this)
                    .load(filelists.get(position))
                    .override(600, 400) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .into(item);
            container.addView(item);
            final int pos = position;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    UIHelper.showWebViewer(getActivity(), messageList.get(pos).getMessageTitle(), messageList.get(pos).getRelationURL());
                }
            });

            return item;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(AppContext.getInstance()).clearMemory();
        new Thread() {
            @Override
            public void run() {
                Glide.get(AppContext.getInstance()).clearDiskCache();
            }
        }.start();

    }
}
