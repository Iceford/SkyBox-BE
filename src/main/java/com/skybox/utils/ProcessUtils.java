package com.skybox.utils;

import com.skybox.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.utils
 * @ClassName: ProcessUtils
 * @Datetime: 2023/11/12 21:27
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于执行命令行指令
 */

@Slf4j
public class ProcessUtils {
    /**
     * @param cmd         要执行的命令行指令
     * @param outprintLog 一个布尔值，用于确定是否在日志中输出执行结果
     * @return String
     * @description 用于执行命令行指令并返回执行结果的字符串
     */
    public static String executeCommand(String cmd, Boolean outprintLog) throws BusinessException {
        // 首先检查cmd是否为空，如果为空则记录错误日志并返回null
        if (StringTools.isEmpty(cmd)) {
            log.error("--- 指令执行失败，因为要执行的FFmpeg指令为空！ ---");
            return null;
        }
        // 创建一个Runtime对象，并使用exec方法执行传入的命令行指令，返回一个Process对象表示正在执行的进程
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            // 执行ffmpeg指令
            // 取出输出流和错误流的信息
            // 注意：必须要取出ffmpeg在执行命令过程中产生的输出信息，如果不取的话当输出流信息填满jvm存储输出留信息的缓冲区时，线程就回阻塞住
            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputStream.start();
            // 等待ffmpeg命令执行完
            process.waitFor();
            // 获取执行结果字符串
            String result = errorStream.stringBuffer.append(inputStream.stringBuffer + "\n").toString();
            // 如果outprintLog为true，则在日志中记录执行命令和执行结果
            if (outprintLog) {
                log.info("执行命令:{}，已执行完毕,执行结果:{}", cmd, result);
            } else {
                // 如果outprintLog为false，则仅在日志中记录执行命令
                log.info("执行命令:{}，已执行完毕", cmd);
            }
            return result;
            // 如果执行过程中发生异常，将打印堆栈跟踪信息并抛出BusinessException异常，其中包含错误消息"视频转换失败"
        } catch (Exception e) {
            // logger.error("执行命令失败:{} ", e.getMessage());
            e.printStackTrace();
            throw new BusinessException("视频转换失败");
        } finally {
            // 添加一个ProcessKiller的钩子，在程序退出前销毁正在执行的进程
            if (null != process) {
                ProcessKiller ffmpegKiller = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }

    /**
     * @param null
     * @return null
     * @description 在程序退出前结束已有的FFmpeg进程
     */
    private static class ProcessKiller extends Thread {
        private final Process process;

        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
        }
    }


    /**
     * @param null
     * @return null
     * @description 获取FFmpeg线程执行过程中产生的输出和错误流的信息
     */
    static class PrintStream extends Thread {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        public PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        // 使用BufferedReader逐行读取输入流的内容，并将其存储在StringBuffer中
        public void run() {
            try {
                if (null == inputStream) {
                    return;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            } catch (Exception e) {
                log.error("读取输入流出错了！错误信息：" + e.getMessage());
            } finally {
                try {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    log.error("调用PrintStream读取输出流后，关闭流时出错！");
                }
            }
        }
    }
}

