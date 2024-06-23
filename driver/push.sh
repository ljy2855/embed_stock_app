#!/bin/bash

cd module
make clean
make
cd ..


cp module/new_driver.ko objs/


adb push objs /data/local/tmp
