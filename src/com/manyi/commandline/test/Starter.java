package com.manyi.commandline.test;

import java.io.File;
import java.io.IOException;

import com.manyi.commandline.runCommandline;

public class Starter {
    public static void main(String[] args) {
        runCommandline rd = new runCommandline();
        try{
            rd.runCommandlines("adb shell getevent -l");
        } catch(IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
