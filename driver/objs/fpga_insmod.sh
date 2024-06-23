#!/bin/bash
insmod fpga_led_driver.ko
insmod fpga_fnd_driver.ko
insmod fpga_dot_driver.ko
insmod fpga_text_lcd_driver.ko
insmod fpga_dip_switch_driver.ko
insmod fpga_push_switch_driver.ko
insmod fpga_buzzer_driver.ko
insmod fpga_step_motor_driver.ko
insmod new_driver.ko

mknod /dev/fpga_led c 260 0
mknod /dev/fpga_fnd c 261 0
mknod /dev/fpga_dot c 262 0
mknod /dev/fpga_text_lcd c 263 0
mknod /dev/fpga_dip_switch c 266 0
mknod /dev/fpga_push_switch c 265 0
mknod /dev/fpga_buzzer c 264 0
mknod /dev/fpga_step_motor c 267 0
mknod /dev/new_driver c 242 0

chmod 666 /dev/fpga_led
chmod 666 /dev/fpga_fnd
chmod 666 /dev/fpga_dot
chmod 666 /dev/fpga_text_lcd
chmod 666 /dev/fpga_dip_switch
chmod 666 /dev/fpga_push_switch
chmod 666 /dev/fpga_buzzer
chmod 666 /dev/fpga_step_motor
chmod 666 /dev/new_driver
