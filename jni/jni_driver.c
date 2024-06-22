#include "com_example_FinalProj_JniDriver.h"
#include <stdio.h>
#include "android/log.h"
#include "output_driver.h"

#define LOG_TAG "MyTag"
#define LOGV(...)   __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_initializeDriver(JNIEnv *env, jobject obj) {
	LOGV("init driver");
    // 실제 드라이버 초기화 코드
}

JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printLCD(JNIEnv * env, jobject obj, jstring text){
	const char *nativeText = (*env)->GetStringUTFChars(env, text, 0);
    if (nativeText == NULL) {
        return; // GetStringUTFChars가 NULL을 반환했다면 메모리 할당 실패
    }
	LOGV("print lcd :%s",nativeText);
    print_to_lcd(nativeText);

    (*env)->ReleaseStringUTFChars(env, text, nativeText);
}

JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_runMotor
  (JNIEnv *env, jobject obj, jobject action, jobject direction, jobject speed, jobject duration) {

    jclass IntegerClass = (*env)->FindClass(env, "java/lang/Integer");
    jmethodID intValue = (*env)->GetMethodID(env, IntegerClass, "intValue", "()I");

    int motor_action = (*env)->CallIntMethod(env, action, intValue);
    int motor_direction = (*env)->CallIntMethod(env, direction, intValue);
    int motor_speed = (*env)->CallIntMethod(env, speed, intValue);
    int motor_duration = (*env)->CallIntMethod(env, duration, intValue);

    run_motor(motor_action, motor_direction, motor_speed, motor_duration);
}

void Java_com_example_FinalProj_JniDriver_testString(JNIEnv *env, jobject this, jstring string)
{
	const char *str=(*env)->GetStringUTFChars( env, string, 0);
	jint len = (*env)->GetStringUTFLength( env, string );
	LOGV("native testString len %d", len);
	LOGV("native testString %s", str);

	(*env)->ReleaseStringUTFChars( env, string, str );
}

