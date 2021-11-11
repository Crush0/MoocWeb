package cn.edu.just.moocweb.utils;

import java.io.*;

public class RunScrapy {
    public static final String path = "E:\\Code\\Py\\MoocScrapy";

    public static final String MOOC = "mooc";

    public static Integer run(String runWhich,int u) {
        try {
            String executePath = path + "\\start.py";
            String[] cmdArr = new String[]{"python",executePath,runWhich,String.valueOf(u)};
            Process process = Runtime.getRuntime().exec(cmdArr,null,new File(path));
            return process.waitFor();
        }
        catch (InterruptedException | IOException e){
            e.printStackTrace();
            return -1;
        }
    }
}
