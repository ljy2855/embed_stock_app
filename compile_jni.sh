#!/bin/bash

# Java 소스 파일 위치 설정
JAVA_SRC_PATH="./src"
# JNI 헤더가 저장될 폴더 설정
JNI_HEADER_PATH="./jni"

# 현재 디렉토리 저장
CURRENT_DIR=$(pwd)

# Java 클래스 파일을 컴파일하기 위해 src 폴더로 이동
cd $JAVA_SRC_PATH

# Java 파일 컴파일 (클래스 파일 생성)
javac com/example/FinalProj/JniDriver.java
if [ $? -ne 0 ]; then
    echo "Java compilation failed!"
    exit 1
fi

# JNI 헤더 파일 생성
javah -jni com.example.FinalProj.JniDriver
if [ $? -ne 0 ]; then
    echo "JNI header generation failed!"
    exit 1
fi

# 생성된 헤더 파일을 JNI 폴더로 이동
mv com_example_FinalProj_JniDriver.h $CURRENT_DIR/$JNI_HEADER_PATH/

# 원래 폴더로 돌아가기
cd $CURRENT_DIR

echo "JNI header generated and moved successfully."
