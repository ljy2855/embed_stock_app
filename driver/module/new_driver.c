#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/interrupt.h>
#include <asm/irq.h>
#include <mach/gpio.h>
#include <linux/platform_device.h>
#include <asm/gpio.h>
#include <linux/wait.h>
#include <linux/fs.h>
#include <linux/init.h>
#include <asm/io.h>
#include <asm/uaccess.h>
#include <linux/ioport.h>
#include <linux/version.h>
#include <linux/cdev.h>
#include <linux/workqueue.h>
#include <linux/timer.h>
#include <linux/slab.h>
#include <linux/ktime.h>

#define DRIVER_NAME "new_driver"

#define FPGA_RST_BASE_ADDR 0x08000000
static int inter_major=242, inter_minor=0;
static int result;
static dev_t inter_dev;
static struct cdev inter_cdev;
static unsigned char * fpga_rst_addr;
static int inter_open(struct inode *, struct file *);
static int inter_release(struct inode *, struct file *);
static int inter_read(struct file *filp, const char *buf, size_t count, loff_t *f_pos);
irqreturn_t inter_handler(int irq, void* dev_id, struct pt_regs* reg);
static volatile int home_flag;
static struct timer_list my_timer;
wait_queue_head_t wq;
DECLARE_WAIT_QUEUE_HEAD(wq);
void timer_callback(struct timer_list *data);

enum read_result{
    NONE,
    HOME,
    RESET
};


static struct file_operations inter_fops =
{
	.open = inter_open,
	.release = inter_release,
    .read = inter_read,
};



irqreturn_t inter_handler(int irq, void* dev_id, struct pt_regs* reg) {
	printk(KERN_ALERT "Interrupt: Home button pressed.\n");
    home_flag = 1;
    __wake_up(&wq, 1, 1, NULL);   // Wake up processes in the wait queue
	return IRQ_HANDLED;
}


void timer_callback(struct timer_list *data) {
    printk(KERN_INFO "Timer Callback function called.\n");
    if(inw((unsigned int)fpga_rst_addr) == 0){
        printk(KERN_INFO "Reset button pressed.\n");
        __wake_up(&wq, 1, 1, NULL); 
    
    }
    my_timer.expires = jiffies +(HZ / 10);
    my_timer.function = timer_callback;
    add_timer(&my_timer);
}

static int inter_open(struct inode *minode, struct file *mfile){
	int ret;
	int irq;

	printk(KERN_ALERT "Open Module\n");

	// HOME
	gpio_direction_input(IMX_GPIO_NR(1,11));
	irq = gpio_to_irq(IMX_GPIO_NR(1,11));
	printk(KERN_ALERT "IRQ Number : %d\n",irq);
	ret=request_irq(irq, inter_handler, IRQF_TRIGGER_FALLING, "home", 0);

    
	return 0;
}

static int inter_release(struct inode *minode, struct file *mfile){
	free_irq(gpio_to_irq(IMX_GPIO_NR(1, 11)), NULL);

	
	printk(KERN_ALERT "Release Module\n");
	return 0;
}
static int inter_read(struct file *filp, const char *buf, size_t count, loff_t *f_pos){
    
    interruptible_sleep_on(&wq);
    if(home_flag){
        home_flag = false;
        return HOME;
    }
    return RESET;
    
}

static int inter_register_cdev(void)
{
	int error;
	if(inter_major) {
		inter_dev = MKDEV(inter_major, inter_minor);
		error = register_chrdev_region(inter_dev,1,DRIVER_NAME);
	}else{
		error = alloc_chrdev_region(&inter_dev,inter_minor,1,DRIVER_NAME);
		inter_major = MAJOR(inter_dev);
	}
	if(error<0) {
		printk(KERN_WARNING "inter: can't get major %d\n", inter_major);
		return result;
	}
	printk(KERN_ALERT "major number = %d\n", inter_major);
	cdev_init(&inter_cdev, &inter_fops);
	inter_cdev.owner = THIS_MODULE;
	inter_cdev.ops = &inter_fops;
	error = cdev_add(&inter_cdev, inter_dev, 1);
	if(error)
	{
		printk(KERN_NOTICE "inter Register Error %d\n", error);
	}
	return 0;
}

static int __init inter_init(void) {
	int result;
    // init intr
	if((result = inter_register_cdev()) < 0 )
		return result;
    home_flag = false;

    init_timer(&(my_timer));
    my_timer.expires = jiffies +(HZ / 10);
    my_timer.function = timer_callback;
    add_timer(&my_timer);

    // init device
    fpga_rst_addr = ioremap(FPGA_RST_BASE_ADDR, 0x1);

	printk(KERN_ALERT "Init Module Success \n");
	printk(KERN_ALERT "Device : /dev/new_driver, Major Num : 242 \n");
	return 0;
}

static void __exit inter_exit(void) {
	cdev_del(&inter_cdev);
    del_timer_sync(&my_timer);


    iounmap(fpga_rst_addr);
	unregister_chrdev_region(inter_dev, 1);
	printk(KERN_ALERT "Remove Module Success \n");
}

module_init(inter_init);
module_exit(inter_exit);
MODULE_LICENSE("GPL");
