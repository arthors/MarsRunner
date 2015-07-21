package com.manyi.action;
//import 

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class Parser {
    private String txtPage = null;
    private final static Pattern patx = Pattern.compile("ABS_MT_POSITION_X+[\\s]+[a-z0-9]+");
    private final static Pattern paty = Pattern.compile("ABS_MT_POSITION_Y+[\\s]+[a-z0-9]+");
    private final static Pattern pat2 = Pattern.compile("[a-z0-9]+");
    private final static Pattern pat_NUM = Pattern.compile("[0-9]+\\.");
    private final static Pattern pat_BACK = Pattern.compile("KEY_BACK(\\s*)UP");
    private final static Pattern pat_HOME = Pattern.compile("KEY_HOME(\\s*)UP");
    private final static Pattern pat_MENU = Pattern.compile("KEY_MENU(\\s*)UP");
    private final static Pattern pat_POWER = Pattern.compile("KEY_POWER(\\s*)UP");
    private final static String file_name = "tmp2";
    private final static String final_file = "final";
    // private final static Pattern patOFF = Pattern.compile("");

    /**
     * @param args
     * @throws IOException
     */

    @Test
    public void testGet() {
        AppendFile af4 = new AppendFile();
        af4.deleteFile(final_file);
        this.getFilelines();
        this.writeResult();
        af4.method1(final_file, this.getTxtPage());
        // System.out.println(this.getTxtPage());

    }

    private boolean readWaitFuction(String line) {
        if (line.startsWith("WAIT")) {
            Matcher matchr1 = pat_NUM.matcher(line);
            while (matchr1.find()) {
                if (matchr1.group().length() > 5) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private String writeResult() {
        txtPage = null;
        AppendFile af3 = new AppendFile();
        af3.deleteDirectory("tmp3");
        ArrayList<String> al3 = af3.readFileByLine(file_name);
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> tmp = new ArrayList<String>();
        for (String string3 : al3) {
            if (string3.startsWith("TOUCH")) {
                list.add(list.size(), string3);
            } else {
                String line1 = ((parseToDrag(tmp).startsWith("DRAG") | parseToDrag(tmp).startsWith("TOUCH")) ? "\n"
                        : "") + parseToDrag(tmp) + "\n";
                // System.out.print(line1);
                // System.out.print(string3);
                if (0 != string3.length()) {
                    txtPage = txtPage + line1 + string3;
                }
                list.clear();
                tmp.clear();
                list.add(string3);
                continue;
            }
            if (list.size() > 0) {
                tmp.add(list.get(list.size() - 1));
            } else {
                continue;
            }
        }
        return null;
    }

    private String getValue(Pattern pat, String line) {
        Matcher matchr1 = pat.matcher(line);
        while (matchr1.find()) {
            return matchr1.group();
        }
        return null;
    }

    private String parseToDrag(ArrayList<String> lines) {
        if (lines.size() != 0) {
            String lineb = lines.get(0);
            String result;
            String linee = lines.get(lines.size() - 1);
            if (lineb.length() == 0) {
                return null;
            } else if (lineb.equals(linee)) {
                return linee;
            } else {
                String x_1 = getValue(Pattern.compile("[0-9]+"), getValue(Pattern.compile("'x':[0-9]+"), lineb));
                String y_1 = getValue(Pattern.compile("[0-9]+"), getValue(Pattern.compile("'y':[0-9]+"), lineb));
                String x_2 = getValue(Pattern.compile("[0-9]+"), getValue(Pattern.compile("'x':[0-9]+"), linee));
                String y_2 = getValue(Pattern.compile("[0-9]+"), getValue(Pattern.compile("'y':[0-9]+"), linee));
                result = "DRAG|{'start':(" + x_1 + "," + y_1 + "),'end':(" + x_2 + ", " + y_2
                        + "),'duration':0.0,'steps':2,}";
                return result;
            }
        }
        return "";
    }

    private String getFilelines() {
        AppendFile af = new AppendFile();
        AppendFile af2 = new AppendFile();
        af2.deleteFile(file_name);
        ArrayList<String> al = af.readFileByLine("tmp");
        String tmp = null;
        int tmp1 = 0;
        for (String sting : al) {
            if (this.readWaitFuction(sting)) {
                af2.method1(file_name, sting + '\n');
            }
            if (this.getReadPhysicalKey(pat_BACK, sting)) {
                // System.out.println("PRESS|{'name':'BACK','type':'downAndUp',}");
                af2.method1(file_name, "PRESS|{'name':'BACK','type':'downAndUp',}" + '\n');
            } else if (this.getReadPhysicalKey(pat_HOME, sting)) {
                // System.out.println("PRESS|{'name':'HOME','type':'downAndUp',}");
                af2.method1(file_name, "PRESS|{'name':'HOME','type':'downAndUp',}" + '\n');
            } else if (this.getReadPhysicalKey(pat_MENU, sting)) {
                // System.out.println("PRESS|{'name':'MENU','type':'downAndUp',}");
                af2.method1(file_name, "PRESS|{'name':'MENU','type':'downAndUp',}" + '\n');
            } else if (this.getReadPhysicalKey(pat_POWER, sting)) {
                // System.out.println("PRESS|{'name':'POWER','type':'downAndUp',}");
                af2.method1(file_name, "PRESS|{'name':'POWER','type':'downAndUp',}" + '\n');
            }
            String xpixel, ypixel;
            xpixel = Integer.toString(this.getReadPixel(patx, sting));
            if (-1 != this.getReadPixel(paty, sting)) {
                ypixel = Integer.toString(this.getReadPixel(paty, sting));
                if (-1 != tmp1) {
                    String out = "TOUCH|{'x':" + tmp1 + ",'y':" + ypixel + ",'type':'downAndUp',}";
                    // System.out.println(out);
                    af2.method1(file_name, out + '\n');
                }
            }
            tmp1 = this.getReadPixel(patx, sting);
        }
        return tmp;
    }

    private boolean getReadPhysicalKey(Pattern pat, String line) {
        Matcher matchr1 = pat.matcher(line);
        while (matchr1.find()) {
            return true;
        }
        return false;
    }

    private int getReadPixel(Pattern pat, String txt) {
        translatePixel tp = new translatePixel();
        Matcher matchr1 = pat.matcher(txt);
        while (matchr1.find()) {
            String string1 = matchr1.group();
            Matcher matchr2 = pat2.matcher(string1);
            while (matchr2.find()) {
                String result = matchr2.group();
                return (int) tp.getPixel(1, tp.transHexDem(result));
            }
        }
        return -1;
    }

    public String getTxtPage() {
        return txtPage;
    }

}
