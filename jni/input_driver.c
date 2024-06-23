#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdint.h>
#include <asm/ioctl.h>

#define DEVICE_NAME "/dev/new_driver"
int readkey()
{
    int dev, key;
    dev = open(DEVICE_NAME, O_RDONLY);
    if (dev < 0)
    {
        perror("Failed to open the motor device");
        return;
    }
    key = read(dev, "", 1);
    close(dev);
    return key;
}