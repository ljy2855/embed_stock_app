## Embedd Stock trading Application
> driver module와 jni를 통한 fpga device 기반 모의 주식 거래 어플 

## Env
* Linux 3.10.17
* Android 4.3 Jellybean
* Android 4.4W (API 20)

## Build
안드로이드 커널이 보드에 빌드되어 있는 것을 가정
### board disk mount
`mount -o rw,remount,size=6G /dev/mmcblk0p4 /data`

### insert module
#### adb connect
`adb start-server`
#### host push driver file to target board
`./push`
#### target board insert module
`cd /data/local/tmp && sh fpga_insmod.sh`

### install apk (host)
`adb install FinalProj.apk`