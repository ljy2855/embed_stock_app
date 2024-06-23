#ifndef OUTPUT_DRIVER_H
#define OUTPUT_DRIVER_H

void print_to_lcd(const char *text);
void print_dot(const char *text);
void print_fnd(const char *text);
void print_led(unsigned char data);
void run_motor(int action, int direction, int speed, int duration);
#endif