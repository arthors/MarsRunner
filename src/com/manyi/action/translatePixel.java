package com.manyi.action;

import java.util.Timer;

public class translatePixel {

    /*
     * Translation the Hexadecimal into Decimal
     */
    public static int transHexDem(String pixel) {
        return Integer.parseInt(pixel, 16);
    }

    /*
     * calculation the pixel point
     */
    public static double getPixel(double rate, int value) {
        double screen = 0;
        screen = rate * value;
        return screen;
    }
}
