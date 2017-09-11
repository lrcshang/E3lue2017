package com.e3lue.us.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.e3lue.us.adapter.DownloadAdapter;
import com.e3lue.us.model.FileShare;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Enzate on 2017/7/14.
 */

public class CheckFile {
    Context context;
//    List<FileShare>fileShares;
    static int size;
    public CheckFile(Context context){
        this.context=context;
//        size=list.size();
//        this.fileShares=list;
    }
    public ArrayList<String> findFile(String path) {
        ArrayList<String> list = new ArrayList<String>();
        list = folderScan("/mnt/sdcard/e3lue"+path);
        return  removeDuplicate(list);

    }

    public void fileScan(String file) {
        Uri data = Uri.parse("file://" + file);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

    public ArrayList<String> folderScan(String path) {
        File file = new File(path);
        ArrayList<String> list = new ArrayList<String>();
        if(file.exists()){
        if (file.isDirectory()) {
            File[] array = file.listFiles();

            for (int i = 0; i < array.length; i++) {
                File f = array[i];

                if (f.isFile()) {// FILE TYPE
                    String name = f.getName();
//                    for (int j = 0; j < fileShares.size(); j++) {
//                        if (name.equals(fileShares.get(j).getFileName())) {
                            list.add(name);
//                        }
//                    }
                    fileScan(f.getAbsolutePath());
                } else {// FOLDER TYPE
                    folderScan(f.getAbsolutePath());
                }
            }
        } }
        return list;
    }

    public  ArrayList<String>  removeDuplicate(ArrayList<String> list) {
        HashSet<String> set = new HashSet<String>(list);
//        String[] titles = new String[size];
        list.clear();
        list.addAll(set);
        String regex = "%(.*)%";
        Pattern pattern = Pattern.compile(regex);
        for (int i = 0; i < list.size(); i++) {
            Matcher matcher = pattern.matcher(list.get(i));// 匹配类
            while (matcher.find()) {
                Integer.valueOf(matcher.group(1));// 打印中间字符
//                titles[Integer.valueOf(matcher.group().replace("%", ""))] = list.get(i);
            }
        }
        return list;
    }
}
