package cn.edu.just.moocweb.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RunScrapy {
    public static Map<String,ScrapyThread> pools = new HashMap<>();

    private static final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");

    public static final String path;

    static{
        if(isWin) {
            path = "E:\\Code\\Py\\MoocScrapy";
        }
        else{
            path = "/usr/py/MoocScrapy";
        }
    }

    public static Integer run(String runWhich,String ... args) {
        try {
            String executePath;
            if(isWin) {
                executePath = path + "\\start.py";
            }
            else{
                executePath = path + "/start.py";
            }
            String py;
            if(isWin) {
                py = "python";
            }
            else{
                py = "python3";
            }
            StringBuilder params  = new StringBuilder();
            for(String param:args){
                params.append(param).append(" ");
            }
            log.info(String.format("运行py脚本，路径:%s ，参数:%s", executePath,params));
            String[] cmdArr = new String[]{py,executePath,runWhich,params.toString()};
            Process process = Runtime.getRuntime().exec(cmdArr,null,new File(path));
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String call;
            while ((call = in.readLine()) != null) {
                log.info(call);
            }
            while ((call = err.readLine()) != null) {
                log.info(call);
            }
            in.close();
            err.close();
            return process.waitFor();
        }
        catch (InterruptedException | IOException e){
            e.printStackTrace();
            return -1;
        }
    }

    public static class ScrapyThread implements Runnable {
        private Thread t;
        private final String threadId;
        private final String which;
        private final String[] args;
        private Integer status = -2;

        public ScrapyThread(String threadId,String runWhich,String ... args){
            this.threadId = threadId;
            this.which = runWhich;
            this.args = args;
        }

        @Override
        public void run() {
            status = RunScrapy.run(which,args);
            log.info("py脚本的返回值为:"+status);
        }

        public void start () {
            pools.put(this.threadId,this);
            if (t == null) {
                t = new Thread (this, threadId);
                t.start ();
            }
        }

        public String getThreadId(){
            return this.threadId;
        }

        public Integer getStatus(){
            return this.status;
        }
    }
}
