package com.example.FinalProj;

public class JniDriver {
    static {
        System.loadLibrary("jni_driver");
    }

    public void clearDevice() {
        this.printDOT("");
        this.printFND("0000");
        this.printLCD("");
        this.printLED(0);
    }

    public void printAccount(double total, double balance) {
        // Format the strings to fit within the LCD's display lines
        String firstLine = String.format("Total:  $%.2f", total);
        String secondLine = String.format("Balance: $%.2f", balance);

        // Ensure each line does not exceed 16 characters
        if (firstLine.length() > 16) {
            firstLine = firstLine.substring(0, 16);
        }
        if (secondLine.length() > 16) {
            secondLine = secondLine.substring(0, 16);
        }

        // Concatenate the two lines with enough spaces to fill each line if necessary
        String fullMessage = String.format("%-16s%-16s", firstLine, secondLine);

        // Send the formatted string to the LCD display
        this.printLCD(fullMessage);
    }

    public void printTransactionMode(boolean isSell) {
        if (isSell) {
            this.printDOT("S");
        } else {
            this.printDOT("B");
        }
    }

    public void printOwnedStockQuantity(Integer quantity) {
        String str = String.valueOf(quantity); // 수정된 부분
        this.printFND(str);
    }

    public void printPriceChangeEvent() {
        this.printLED(255);
        try {
            Thread.sleep(1000); // 수정된 부분, 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.printLED(0);
    }

    public void printTransactionEvent() {
        this.runMotor(1, 1, 1, 1);
    }

    public native int readKey();

    // 네이티브 메소드 선언
    private native void printLCD(String text);

    private native void printDOT(String text);

    private native void printFND(String text);

    private native void printLED(int num);

    private native void runMotor(int action, int direction, int speed, int time);

}
