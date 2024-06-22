#include "output_driver.h"
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include "android/log.h"
#define LOG_TAG "MyTag"
#define LOGV(...)   __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define MAX_BUFF 32
#define LINE_BUFF 16

#define FPGA_TEXT_LCD_DEVICE "/dev/fpga_text_lcd"
#define FPGA_STEP_MOTOR_DEVICE "/dev/fpga_step_motor"
void print_to_lcd(const char *text) {
    int dev;
    char buffer[MAX_BUFF];
    int length;

    // 디바이스 파일 열기
    dev = open(FPGA_TEXT_LCD_DEVICE, O_WRONLY);
    if (dev < 0) {
        LOGV("Failed to open the LCD device");
        return;
    }

    // 문자열을 LCD 버퍼 크기에 맞게 복사
    memset(buffer, ' ', sizeof(buffer));
    length = strlen(text);
    if (length > MAX_BUFF) {
        length = MAX_BUFF;
    }
    strncpy(buffer, text, length);

    // 디바이스로 문자열 쓰기
    write(dev, buffer, MAX_BUFF);
    LOGV("Finish write to LCD");
    // 디바이스 파일 닫기
    close(dev);
}

void run_motor(int action, int direction, int speed, int duration) {
    int dev;
    unsigned char motor_state[3];

    dev = open(FPGA_STEP_MOTOR_DEVICE, O_WRONLY);
    if (dev < 0) {
        perror("Failed to open the motor device");
        return;
    }

    motor_state[0] = (unsigned char)action;
    motor_state[1] = (unsigned char)direction;
    motor_state[2] = (unsigned char)speed;

    write(dev, motor_state, 3);
    usleep(duration * 1000);  // Convert milliseconds to microseconds
    close(dev);
}