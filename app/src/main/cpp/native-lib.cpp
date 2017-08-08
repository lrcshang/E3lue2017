#include <jni.h>

#include "include/bsdiff.c"
#include "include/bspatch.c"

#include "include/bzlib.h"
#include <err.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
extern "C"
JNIEXPORT jint JNICALL
Java_com_e3lue_us_utils_DiffUtils_genDiff(JNIEnv *env, jclass type, jstring oldApkPath_,
                                          jstring newApkPath_, jstring patchPath_) {
    const char *oldApkPath = env->GetStringUTFChars(oldApkPath_, 0);
    const char *newApkPath = env->GetStringUTFChars(newApkPath_, 0);
    const char *patchPath = env->GetStringUTFChars(patchPath_, 0);

    // TODO

    env->ReleaseStringUTFChars(oldApkPath_, oldApkPath);
    env->ReleaseStringUTFChars(newApkPath_, newApkPath);
    env->ReleaseStringUTFChars(patchPath_, patchPath);
}
