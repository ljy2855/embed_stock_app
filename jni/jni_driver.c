#include "com_example_FinalProj_JniDriver.h"
#include <stdio.h>
#include "android/log.h"
#include "output_driver.h"
#include "input_driver.h"

#define LOG_TAG "MyTag"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)

JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printLCD(JNIEnv *env, jobject obj, jstring text)
{
    const char *nativeText = (*env)->GetStringUTFChars(env, text, 0);
    if (nativeText == NULL)
    {
        return; // GetStringUTFChars failed to allocate memory
    }
    LOGV("Print to LCD: %s", nativeText);
    print_to_lcd(nativeText);
    (*env)->ReleaseStringUTFChars(env, text, nativeText);
}

JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printDOT(JNIEnv *env, jobject obj, jstring text)
{
    const char *nativeString = (*env)->GetStringUTFChars(env, text, 0);
    if (nativeString == NULL)
    {
        return; // GetStringUTFChars failed to allocate memory
    }
    LOGV("Print to DOT Matrix: %s", nativeString);
    print_dot(nativeString);
    (*env)->ReleaseStringUTFChars(env, text, nativeString);
}

JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printFND(JNIEnv *env, jobject obj, jstring str)
{
    const char *numStr = (*env)->GetStringUTFChars(env, str, NULL);
    if (numStr == NULL)
    {
        return; // GetStringUTFChars failed to allocate memory
    }
    LOGV("Print to FND: %s", numStr);
    print_fnd(numStr);
    (*env)->ReleaseStringUTFChars(env, str, numStr);
}

JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printLED(JNIEnv *env, jobject obj, jint value)
{
    LOGV("Print to LED: %d", value);
    print_led((unsigned char)value);
}

JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_runMotor(JNIEnv *env, jobject obj, jint action, jint direction, jint speed, jint duration)
{
    LOGV("Run motor - Action: %d, Direction: %d, Speed: %d, Duration: %d", action, direction, speed, duration);
    run_motor(action, direction, speed, duration);
}

JNIEXPORT jint JNICALL Java_com_example_FinalProj_JniDriver_readKey(JNIEnv *env, jobject obj)
{
    int result = 0;
    result = readkey();
    LOGV("Read Key: %d", result);
    return result;
}
