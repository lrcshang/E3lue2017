package com.e3lue.us.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.activity.BgchangeActivity;
import com.e3lue.us.model.BaseUser;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.pulltozoomview.PullToZoomScrollViewEx;
import com.e3lue.us.utils.SharedPreferences;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.path;
import static android.app.Activity.RESULT_OK;

public class MemberFragment extends Fragment implements View.OnClickListener {

    private Activity context;
    private View root;
    private PullToZoomScrollViewEx scrollView;
    Gson gson;
    private Dialog headDialog;
    ImageView head_change;
    private Bitmap head;// 头像Bitmap
    private static String path = "/sdcard/e3lue/";// sd路径
    ImageView imageView;
    int image[] = {R.drawable.ic_img_profile_bg, R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4,R.drawable.bg5
            ,R.drawable.bg6,R.drawable.bg7,R.drawable.bg8,R.drawable.bg9};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return root = inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        gson = new Gson();
        initData();
        initView();
    }

    void initView() {
        scrollView = (PullToZoomScrollViewEx) root.findViewById(R.id.scrollView);
        WindowManager wm = getActivity().getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        scrollView .setHeaderViewSize(width,480);
        View headView = LayoutInflater.from(context).inflate(R.layout.member_head_view, null, false);
        head_change = (ImageView) headView.findViewById(R.id.iv_user_head);
        head_change.setOnClickListener(this);
        View zoomView = LayoutInflater.from(context).inflate(R.layout.member_zoom_view, null, false);
        imageView = (ImageView) zoomView.findViewById(R.id.iv_zoom);
        imageView.setOnClickListener(this);
        imageView.setImageResource(image[SharedPreferences.getInstance().getInt("pos",0)]);
        View contentView = LayoutInflater.from(context).inflate(R.layout.member_content_view, null, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);
        String username = "", org = "";

        BaseUser user = gson.fromJson(SharedPreferences.getInstance().getString("BaseUser", ""), BaseUser.class);
        if (user != null) {
            username = user.getName();
            org = user.getMemo();
        }
        TextView orgview = (TextView) headView.findViewById(R.id.member_org);
        TextView usernameview = (TextView) headView.findViewById(R.id.member_username);
        orgview.setText(org);
        usernameview.setText(username);

        scrollView.getPullRootView().findViewById(R.id.member_diary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showDiaryList(getActivity());
            }
        });

        scrollView.getPullRootView().findViewById(R.id.member_fee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showExpensesMain(getActivity());
            }
        });
        scrollView.getPullRootView().findViewById(R.id.member_lbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showCheckInList(getActivity());
            }
        });
        scrollView.getPullRootView().findViewById(R.id.member_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        scrollView.getPullRootView().findViewById(R.id.member_suggestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSuggestionList(getActivity());
            }
        });

        scrollView.getPullRootView().findViewById(R.id.member_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSetting(getActivity());
            }
        });
        Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");// 从Sd中找头像，转换成Bitmap
        if (bt != null) {
            Drawable drawable = new BitmapDrawable(toRoundBitmap(bt));// 转换成drawable
            head_change.setImageDrawable(drawable);
        } else {
            /**
             * 如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
             *
             */
        }
//        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
//        context.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
//        int mScreenHeight = localDisplayMetrics.heightPixels;
//        int mScreenWidth = localDisplayMetrics.widthPixels;
//        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
//        scrollView.setHeaderLayoutParams(localObject);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_head:
                showHeaddialog();
                break;
            case R.id.photo:
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                headDialog.dismiss();
                break;
            case R.id.picture:
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                headDialog.dismiss();
                break;
            case R.id.cancel:
                headDialog.dismiss();
                break;
            case R.id.iv_zoom:
                Intent intent = new Intent();
                intent.setClass(getActivity(), BgchangeActivity.class);
                startActivityForResult(intent, 1000);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    public void showHeaddialog() {
        headDialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_head, null);
        //初始化控件
        initDialogView(inflate);
        //将布局设置给Dialog
        headDialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = headDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 50;//设置Dialog距离底部的距离
        lp.width = getResources().getDisplayMetrics().widthPixels - 40;
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        headDialog.show();//显示对话框
    }

    private void initDialogView(View inflate) {
        TextView photo = (TextView) inflate.findViewById(R.id.photo);
        TextView picture = (TextView) inflate.findViewById(R.id.picture);
        TextView cancel = (TextView) inflate.findViewById(R.id.cancel);
        picture.setOnClickListener(this);
        photo.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    if (data.getExtras() == null) {
                        return;
                    }
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        setPicToView(head);// 保存在SD卡中
                        head_change.setImageBitmap(toRoundBitmap(head));// 用ImageView显示出来
                    }
                }
                break;
            default:
                break;

        }
        if(requestCode == 1000 && resultCode == 1001)
        {
            int pos = data.getIntExtra("pos",0);
            imageView.setImageResource(image[pos]);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 保存图片到本地
     *
     * @param mBitmap
     */
    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "head.jpg";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 调用系统的裁剪
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 把bitmap转成圆形
     */
    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        // 取最短边做边长
        if (width < height) {
            r = width;
        } else {
            r = height;
        }
        // 构建一个bitmap
        Bitmap backgroundBm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBm);
        Paint p = new Paint();
        // 设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect = new RectF(0, 0, r, r);
        // 通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        // 且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, p);
        // 设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }

    private void initData() {

    }
}