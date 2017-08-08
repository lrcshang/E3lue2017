package com.e3lue.us.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.e3lue.us.R;
import com.e3lue.us.activity.MainActivity;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.PushService;

/**
 * Created by Enzate on 2017/7/19.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SoundPool soundPool;
        Vibrator vibrator;
        Bundle bundle = intent.getExtras();
        if (intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Toast.makeText(context, title + msg, Toast.LENGTH_SHORT).show();
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d("xinxi", "[MyReceiver] 用户点击打开了通知");

            //打开自定义的Activity
            Intent i = new Intent(context, MainActivity.class);
            i.putExtras(bundle);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
            context.startActivity(i);
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            soundPool = new SoundPool(10, AudioManager.STREAM_RING, 5);
            soundPool.load(context, R.raw.notification, 1);
            soundPool.play(1, 1, 1, 0, 0, 1);
            Toast.makeText(context, title + content, Toast.LENGTH_SHORT).show();
        }else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")||intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
            Log.d("xinxi", "[MyReceiver] 自启");
            JPushInterface.resumePush(context.getApplicationContext());
        }
        else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern,-1);
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
            soundPool.load(context, R.raw.notification, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,
                                           int status) {
                    // 每次装载完成均会回调
                    Log.i("main", "音频池资源id为：" + sampleId + "的资源装载完成");
                    // 当前装载完成ID为map的最大值，即为最后一次装载完成
                    if (sampleId == 1) {
                        soundPool.play(1, 1, 1, 0, 0, 1);
                    }

                }
            });
//            MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.notification);
//            mPlayer.setLooping(false);
//            mPlayer.start();
            Toast.makeText(context, title + content + "   " + notifactionId, Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        }
    }
}
