LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := jni_driver
LOCAL_LDLIBS := -llog
LOCAL_SRC_FILES := jni_driver.c output_driver.c
include $(BUILD_SHARED_LIBRARY)
