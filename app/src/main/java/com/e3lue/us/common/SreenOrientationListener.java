package com.e3lue.us.common;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * Created by Leo on 2017/3/24.
 */

public class SreenOrientationListener extends OrientationEventListener {

    public SreenOrientationListener(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return; // 手机平放时，检测不到有效的角度
        }
        // 只检测是否有四个角度的改变
        if (orientation > 350 || orientation < 10) {
            // 0度：手机默认竖屏状态（home键在正下方）
            orientation = 0;
        } else if (orientation > 80 && orientation < 100) {
            // 90度：手机顺时针旋转90度横屏（home建在左侧）
        } else if (orientation > 170 && orientation < 190) {
            // 手机顺时针旋转180度竖屏（home键在上方）
            orientation = 180;
        } else if (orientation > 260 && orientation < 280) {
            // 手机顺时针旋转270度横屏，（home键在右侧）
        }
    }
}
