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

import com.e3lue.us.R;
import com.e3lue.us.ui.loopviewpager.AutoLoopViewPager;
import com.e3lue.us.utils.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagechangeActivity extends Activity {
    private GalleryPagerAdapter galleryAdapter;
    //    int image[] = {R.drawable.ic_img_profile_bg, R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5
//            , R.drawable.bg6, R.drawable.bg7, R.drawable.bg8, R.drawable.bg9};
    List<Bitmap> images;
    List<String> filelists;
    Bitmap bitmap;
    @BindView(R.id.text)
    TextView title;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initViewPager();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgchange);
        ButterKnife.bind(this);
        imageView = (ImageView) findViewById(R.id.sure);
        images = new ArrayList<>();
        filelists = getIntent().getStringArrayListExtra("filelists");
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < filelists.size(); i++) {
//                    bitmap = BitmapFactory.decodeFile(filelists.get(i).toString(), null);
                    images.add(getBitmap(filelists.get(i).toString()));

                }
                Message message=new Message();
                message.what=0x01;
                handler.sendMessage(message);
            }
        }.start();
    }
    public Bitmap getBitmap(String imgPath) {
        // Get bitmap through image path
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }
    ImageView imageView;
    int pos = 0;

    private void initViewPager() {
        AutoLoopViewPager viewPager = (AutoLoopViewPager) findViewById(R.id.viewPager);
        viewPager.stopAutoScroll(-1);
        title.setText(filelists.get(pos).substring(filelists.get(pos).lastIndexOf('/') + 1));
        imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("xinxi", pos + "");
                Intent intent = new Intent();
                intent.putExtra("pos", pos);
                /*
                 * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，这样就可以在onActivityResult方法中得到Intent对象，
                 */
                SharedPreferences.getInstance().putInt("pos", pos);
                setResult(1001, intent);
                //    结束当前这个Activity对象的生命
                finish();
            }
        });
        viewPager.setPageMargin(40);
        galleryAdapter = new GalleryPagerAdapter();
        viewPager.setAdapter(galleryAdapter);
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
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView item = new ImageView(ImagechangeActivity.this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
            item.setLayoutParams(params);
            item.setScaleType(ImageView.ScaleType.CENTER);
            item.setImageBitmap(images.get(position));
//            Glide.with(BgchangeActivity.this)
//                    .load(HttpUrl.Url.BASIC + "/userfiles/images/" + messageList.get(position).getMessagePicture())
//                    .into(item);
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
}
