package com.e3lue.us.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.adapter.DragAdapter;
import com.e3lue.us.utils.DataTools;

import java.lang.reflect.Field;

/**
 * Created by Leo on 2017/4/26.
 */

public class LineScrollGridView_1 extends GridView {
    /**
     * 点击时候的X位置
     */
    public int downX;
    /**
     * 点击时候的Y位置
     */
    public int downY;
    /**
     * 点击时候对应整个界面的X位置
     */
    public int windowX;
    /**
     * 点击时候对应整个界面的Y位置
     */
    public int windowY;
    /**
     * 屏幕上的X
     */
    private int win_view_x;
    /**
     * 屏幕上的Y
     */
    private int win_view_y;
    /**
     * 拖动的里x的距离
     */
    int dragOffsetX;
    /**
     * 拖动的里Y的距离
     */
    int dragOffsetY;
    /**
     * 长按时候对应postion
     */
    public int dragPosition;
    /**
     * Up后对应的ITEM的Position
     */
    private int dropPosition;
    /**
     * 开始拖动的ITEM的Position
     */
    private int startPosition;
    /**
     * item高
     */
    private int itemHeight;
    /**
     * item宽
     */
    private int itemWidth;
    /**
     * 拖动的时候对应ITEM的VIEW
     */
    private View dragImageView = null;
    /**
     * 长按的时候ITEM的VIEW
     */
    private ViewGroup dragItemView = null;
    /**
     * WindowManager管理器
     */
    private WindowManager windowManager = null;
    /** */
    private WindowManager.LayoutParams windowParams = null;
    /**
     * item总量
     */
    private int itemTotalCount;
    /**
     * 一行的ITEM数量
     */
    private int nColumns = 4;
    /**
     * 行数
     */
    private int nRows;
    /**
     * 剩余部分
     */
    private int Remainder;
    /**
     * 是否在移动
     */
    private boolean isMoving = false;
    /** */
    private int holdPosition;
    /**
     * 拖动的时候放大的倍数
     */
    private double dragScale = 1.2D;
    /**
     * 震动器
     */
    private Vibrator mVibrator;
    /**
     * 每个ITEM之间的水平间距
     */
    private int mHorizontalSpacing = 15;
    /**
     * 每个ITEM之间的竖直间距
     */
    private int mVerticalSpacing = 15;
    /* 移动时候最后个动画的ID */
    private String LastAnimationID;

    public LineScrollGridView_1(Context context) {
        super(context);
        init(context);
    }

    public LineScrollGridView_1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineScrollGridView_1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //将布局文件中设置的间距dip转为px
        mHorizontalSpacing = DataTools.dip2px(context, mHorizontalSpacing);
    }

    /**
     * 在ScrollView内，所以要进行计算高度
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
