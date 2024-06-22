package com.example.FinalProj;

public class JniDriver {
    static {
        System.loadLibrary("jni_driver");
    }

    // 네이티브 메소드 선언
    public native void initializeDriver();
    public native void printLCD(String text);
    public native void printDOT(String text);
    public native void printFND(String text);
    public native void printLED(Integer num);
    public native void runMotor(Integer num);
    public native void releaseDriver();
}
