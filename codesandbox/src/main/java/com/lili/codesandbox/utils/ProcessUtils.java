package com.lili.codesandbox.utils;

import com.lili.codesandbox.codeSandbox.model.ExecuteCodeResponse;
import com.lili.codesandbox.codeSandbox.model.ExecuteMessage;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessUtils{

    /**
     * 创建进程, 执行命令
     * @param cmd 命令
     * @return 获取正常/错误输出
     */
    public static ExecuteMessage runProcess(String cmd) throws InterruptedException, IOException{
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ExecuteMessage executeMessage = new ExecuteMessage();
        int waitFor = 0;

            Process process = Runtime.getRuntime().exec(cmd);
            waitFor = process.waitFor();
            StringBuilder exeProcessOutput = new StringBuilder();

            if(waitFor == 0){
                // 获取读入IO流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output;
                // 逐行读取输出, 直到为空
                while((output = bufferedReader.readLine()) != null){
                    exeProcessOutput.append(output);
                }
                executeMessage.setMessage(exeProcessOutput.toString());
            }else{
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorOutput;
                while((errorOutput = errorBufferedReader.readLine()) != null){
                    exeProcessOutput.append(errorOutput);
                }
                executeMessage.setErrorMessage(exeProcessOutput.toString());
            }


        stopWatch.stop();
        executeMessage.setTime(stopWatch.getTotalTimeMillis());
        executeMessage.setExitValue(waitFor);
        return executeMessage;
    }
}
