//
// Created by bullypaulo on 2019/10/21.
//
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_signalprocess_facecapture_MainActivity_stringFromMyTry(
        JNIEnv* env,
        jobject /* this */) {

    std::string hello = "this is my try ";
    for (int i = 0; i <= 10; i++)
        hello += ('A' + i);
    return env->NewStringUTF(hello.c_str());
}
