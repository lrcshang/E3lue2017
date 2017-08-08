package com.e3lue.us.utils;

/**
 * 类说明：   APK Patch工具�?
 * 
 * @author  Cundong
 * @date    2013-9-6
 * @version 1.0
 */
public class PatchUtils {
	static {
		System.loadLibrary("native-lib");
	}

	/**
	 * native方法
	 * 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
	 * @param oldApkPath
	 * @param newApkPath
	 * @param patchPath
	 * @return
	 */
	public static native int patch(String oldApkPath, String newApkPath,
			String patchPath);
}