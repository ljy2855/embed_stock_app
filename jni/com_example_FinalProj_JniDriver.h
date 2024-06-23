/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_FinalProj_JniDriver */

#ifndef _Included_com_example_FinalProj_JniDriver
#define _Included_com_example_FinalProj_JniDriver
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_FinalProj_JniDriver
 * Method:    readKey
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_FinalProj_JniDriver_readKey
  (JNIEnv *, jobject);

/*
 * Class:     com_example_FinalProj_JniDriver
 * Method:    printLCD
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printLCD
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_FinalProj_JniDriver
 * Method:    printDOT
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printDOT
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_FinalProj_JniDriver
 * Method:    printFND
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printFND
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_FinalProj_JniDriver
 * Method:    printLED
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_printLED
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_example_FinalProj_JniDriver
 * Method:    runMotor
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_com_example_FinalProj_JniDriver_runMotor
  (JNIEnv *, jobject, jint, jint, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
