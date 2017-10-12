package com.e3lue.us.http;

import android.os.Message;
import android.util.Log;

import com.e3lue.us.activity.FileShareActivity;
import com.e3lue.us.adapter.DownloadAdapter_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Enzate on 2017/10/11.
 */

public class Pingutil {
    /// host = 192.168.8.8

    /// pingcount = 2
    static String line = null;
    static Process process = null;
    static BufferedReader successReader = null;
    static String command;
    static boolean isSuccess = false;

    public static boolean ping(String host, int pingCount, final FileShareActivity activity) {
        command = "ping -w 2 -c " + pingCount + " " + host;
        new Thread() {
            public void run() {
                try {
                    process = Runtime.getRuntime().exec(command);
                    if (process == null) {
                        isSuccess = false;
                        Message msg = new Message();
                        msg.what = 0x06;
                        msg.obj = false;
                        activity.handler.sendMessage(msg);
                        return;
                    }
                    successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while ((line = successReader.readLine()) != null) {

                    }
                    int status = process.waitFor();

                    if (status == 0) {
                        Log.i("xinxi", status + "");
                        isSuccess = true;
                    } else {

                        isSuccess = false;
                    }
                    Message msg = new Message();
                    msg.what = 0x06;
                    msg.obj = isSuccess;
                    activity.handler.sendMessage(msg);
                } catch (IOException e) {

                } catch (InterruptedException e) {

                } finally {

                    if (process != null) {
                        process.destroy();
                    }
                    if (successReader != null) {
                        try {
                            successReader.close();
                        } catch (IOException e) {

                        }
                    }
                }
            }
        }.start();
        return isSuccess;
    }

    public static class ThreadInterrupt extends Thread {
        public void run() {
            try {
                process = Runtime.getRuntime().exec(command);
                if (process == null) {
                    isSuccess = false;
                    Message msg = new Message();
                    msg.what = 0x06;
                    return;
                }
                successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = successReader.readLine()) != null) {

                }
                int status = process.waitFor();

                if (status == 0) {
                    Log.i("xinxi", status + "");
                    isSuccess = true;
                } else {

                    isSuccess = false;
                }

            } catch (IOException e) {

            } catch (InterruptedException e) {

            } finally {

                if (process != null) {
                    process.destroy();
                }
                if (successReader != null) {
                    try {
                        successReader.close();
                    } catch (IOException e) {

                    }
                }
            }
        }

    }
}
