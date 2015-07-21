package com.manyi.commandline;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.manyi.action.AppendFile;

public class runCommandline {
    private long time_b = 0;
    private String d1;
    private int flag = 0;
    Runtime r = Runtime.getRuntime();
    AppendFile af = new AppendFile();

    private void readProcessOutput(final Process process) {
        read(process.getInputStream(), System.out);
        read(process.getErrorStream(), System.err);
    }

    private void read(InputStream inputStream, PrintStream out) {
        try {
            SimpleDateFormat df = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            if (flag == 1) {
                out.print("start");
                flag = 0;
            }

            while ((line = reader.readLine()) != null) {
                out.println(line);
                af.method1("tmp", line + "\n");
                Date now = new Date();
//                long bet;
                long date1 = now.getTime();
                if ((date1 - time_b) > 300) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.0");
                    out.println("WAIT|{'seconds':" + decimalFormat.format((date1 - time_b) / 1000.0f) + ",}");
                    af.method1("tmp",
                            "WAIT|{'seconds':" + decimalFormat.format((date1 - time_b) / 1000.0f) + ",}" + "\n");
                    time_b = date1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void runCommandlines(String commandLine) throws IOException {
        try {
            af.deleteFile("tmp");
        } catch (Exception e) {
            System.out.println("Let begin now");
        }
        Process process = r.exec(commandLine);
        readProcessOutput(process);
    }

    // public void main(String[] args){
    //
    // }
}
