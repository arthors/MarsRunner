
package com.manyi.commandline.test;

//import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.manyi.action.translatePixel;
import com.manyi.commandline.runCommandline;

public class commandlineTest {

//    @Test
//    public void test() {
//        runCommandline rd = new runCommandline();
//        try {
//            rd.runCommandlines("adb shell getevent -l");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
    public void testTransLate() {
        translatePixel tp = new translatePixel();
            tp.transHexDem("0000032b");
            System.out.println(tp.transHexDem("0000032b"));
    }
    @Test
    public void testgetHorizenPixel(){
        translatePixel tp = new translatePixel();
//        int H = tp.transHexDem("0000032b");
        int H = 719;
        int Hmax = 720;
        System.out.println((int)tp.getPixel(0.355, tp.transHexDem("0000032b")));
    }

}
