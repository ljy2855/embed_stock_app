#include "output_driver.h"
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include "android/log.h"

#define LOG_TAG "MyTag"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define MAX_BUFF 32
#define LINE_BUFF 16
#define MAX_DIGIT 4

#define FPGA_TEXT_LCD_DEVICE "/dev/fpga_text_lcd"
#define FPGA_STEP_MOTOR_DEVICE "/dev/fpga_step_motor"
#define FPGA_DOT_DEVICE "/dev/fpga_dot"
#define LED_DEVICE "/dev/fpga_led"
#define FND_DEVICE "/dev/fpga_fnd"

unsigned char fpga_alphabet_patterns[2][10] = {
    {// 'S'
     0x3e, 0x7f, 0x60, 0x60, 0x3e, 0x7f, 0x03, 0x03, 0x7f, 0x7e},
    {// 'B'
     0x7e, 0x7f, 0x63, 0x63, 0x7f, 0x7f, 0x63, 0x63, 0x7f, 0x7e}};

unsigned char fpga_set_blank[10] = {
    // memset(array,0x00,sizeof(array));
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

void print_to_lcd(const char *text)
{
    int dev;
    char buffer[MAX_BUFF];
    int length;

    // 디바이스 파일 열기
    dev = open(FPGA_TEXT_LCD_DEVICE, O_WRONLY);
    if (dev < 0)
    {
        LOGV("Failed to open the LCD device");
        return;
    }

    // 문자열을 LCD 버퍼 크기에 맞게 복사
    memset(buffer, ' ', sizeof(buffer));
    length = strlen(text);
    if (length > MAX_BUFF)
    {
        length = MAX_BUFF;
    }
    strncpy(buffer, text, length);

    // 디바이스로 문자열 쓰기
    write(dev, buffer, MAX_BUFF);
    LOGV("Finish write to LCD");
    // 디바이스 파일 닫기
    close(dev);
}

void print_dot(const char *text)
{
    int dev, i;
    size_t str_size;

    dev = open(FPGA_DOT_DEVICE, O_WRONLY);
    if (dev < 0)
    {
        LOGV("Device open error: %s\n", FPGA_DOT_DEVICE);
        return;
    }

    if (strcmp(text, "S") == 0)
    {
        str_size = sizeof(fpga_alphabet_patterns[0]);
        write(dev, fpga_alphabet_patterns[0], str_size);
    }
    else if (strcmp(text, "B") == 0)
    {
        str_size = sizeof(fpga_alphabet_patterns[1]);
        write(dev, fpga_alphabet_patterns[1], str_size);
    }
    else
    {
        str_size = sizeof(fpga_set_blank);
        write(dev, fpga_set_blank, str_size);
    }

    close(dev);
}

void print_fnd(const char *text)
{
    int dev, i;
    unsigned char data[MAX_DIGIT] = {0};
    int str_size = strlen(text);

    if (str_size > MAX_DIGIT)
    {
        str_size = MAX_DIGIT;
        LOGV("Warning! 4 Digit number only!\n");
    }

    for (i = 0; i < str_size; i++)
    {
        if (text[i] >= '0' && text[i] <= '9')
            data[3 - i] = text[i] - '0';
    }

    dev = open(FND_DEVICE, O_RDWR);
    if (dev < 0)
    {
        LOGV("Device open error : %s\n", FND_DEVICE);
        return;
    }

    if (write(dev, &data, 4) < 0)
    {
        LOGV("Write Error!\n");
    }

    close(dev);
}

void print_led(unsigned char data)
{
    int dev;

    if (data < 0 || data > 255)
    {
        LOGV("Invalid range! Value must be between 0 and 255.\n");
        return;
    }

    dev = open(LED_DEVICE, O_RDWR);
    if (dev < 0)
    {
        LOGV("Device open error : %s\n", LED_DEVICE);
        return;
    }

    if (write(dev, &data, 1) < 0)
    {
        LOGV("Write Error!\n");
    }

    close(dev);
}

void run_motor(int action, int direction, int speed, int duration)
{
    int dev;
    unsigned char motor_state[3];

    dev = open(FPGA_STEP_MOTOR_DEVICE, O_WRONLY);
    if (dev < 0)
    {
        perror("Failed to open the motor device");
        return;
    }

    motor_state[0] = (unsigned char)action;
    motor_state[1] = (unsigned char)direction;
    motor_state[2] = (unsigned char)speed;

    write(dev, motor_state, 3);
    sleep(duration);

    motor_state[0] = 0;
    motor_state[1] = (unsigned char)direction;
    motor_state[2] = (unsigned char)speed;
    write(dev, motor_state, 3);
    close(dev);
}