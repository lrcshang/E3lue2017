package com.e3lue.us.utils;

import com.e3lue.us.model.FileShares;

import java.util.List;

/**
 * Created by x-lrc on 2017/9/1.
 */

public class FiledataUtil {
    static {
        System.loadLibrary("native-lib");
    }

    public static native List<FileShares> setFileRess(List<FileShares> Dfiles, int a);

    public static native List<FileShares> setFileRes(int a);
}
