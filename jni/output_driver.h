#ifndef OUTPUT_DRIVER_H
#define OUTPUT_DRIVER_H

void print_to_lcd(const char *text);
void run_motor(int action, int direction, int speed, int duration);
#endif