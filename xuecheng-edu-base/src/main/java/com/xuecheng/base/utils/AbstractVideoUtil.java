package com.xuecheng.base.utils;

import com.xuecheng.base.config.CustomPropertiesReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname VideoUtil
 * @Description 视频文件处理工具类父类，提供：1. 查看视频时长; 2. 校验两个视频的时长是否相等
 * @Created by Domenic
 */
@Slf4j
public abstract class AbstractVideoUtil {

    /**
     * <a href="https://ffmpeg.org/">ffmpeg</a> 的安装位置
     */
    public static final String FFMPEG_PATH = CustomPropertiesReader.FFMPEG_PATH;

    protected AbstractVideoUtil() {
        // prevents other classes from instantiating it
    }

    /**
     * 检查视频时间是否一致
     * @param source 源视频路径
     * @param target 目标视频路径
     * @return {@code true} or {@code false}
     */
    public static Boolean checkVideoTime(String source, String target) {
        String sourceTime = getVideoTime(source);
        String targetTime = getVideoTime(target);

        if (sourceTime != null && targetTime != null) {
            // 取出时分秒 (去除毫秒)
            sourceTime = sourceTime.substring(0, sourceTime.lastIndexOf("."));

            // 取出时分秒 (去除毫秒)
            targetTime = targetTime.substring(0, targetTime.lastIndexOf("."));
            if (sourceTime == null || targetTime == null) {
                return false;
            }
            if (sourceTime.equals(targetTime)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取视频时间 (时:分:秒:毫秒)
     * @param videoPath 视频路径
     * @return 视频时间
     */
    public static String getVideoTime(String videoPath) {
        List<String> cmd = new ArrayList<>();
        cmd.add(FFMPEG_PATH);
        cmd.add("-i");
        cmd.add(videoPath);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(cmd);
        // 将标准输入流和错误输入流合并，通过标准输入流程读取信息
        builder.redirectErrorStream(true);

        try {
            Process p = builder.start();
            String outstring = execute(p);
            log.debug("获取视频时长, 进程执行结果: {}", outstring);

            int start = outstring.trim().indexOf("Duration: ");

            if (start >= 0) {
                int end = outstring.trim().indexOf(", start:");
                if (end >= 0) {
                    String time = outstring.substring(start + 10, end);
                    if (time != null && !time.equals("")) {
                        log.debug("获取视频时长, videoPath={}, videoTime={}", videoPath, time);
                        return time.trim();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 等待进程执行完毕，并获取进程执行结果
     * @param p 进程
     * @return 进程执行结果："error" 表示重试次数超过阈值后仍未执行完毕；执行完毕则返回进程执行结果
     */
    public static String execute(Process p) {
        StringBuilder outputString = new StringBuilder();

        boolean finished = false;

        // 最多执行 600 次，若出错就睡眠 1 秒再重试
        int maxRetry = CustomPropertiesReader.MAX_RETRY_COUNT;
        int retry = 0;

        try (InputStream in = p.getInputStream();
                InputStream error = p.getErrorStream()) {

            while (!finished) {
                if (retry > maxRetry) {
                    return "error";
                }
                while (in.available() > 0) {
                    Character c = (char) in.read();
                    outputString.append(c);
                }
                while (error.available() > 0) {
                    Character c = (char) error.read();
                    outputString.append(c);
                }

                // 若进程已结束
                if (!p.isAlive()) {
                    finished = true;
                }
                // 若进程还未结束，则睡眠 1 秒，再重试
                else {
                    Thread.sleep(1000);
                    retry++;
                }
            }
        } catch (InterruptedException e) {
            log.error("Thread {} Interrupted! errorMsg={}", Thread.currentThread().getName(), e.getMessage());
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("进程执行出错, errorMsg={}", e.getMessage());
        } finally {
            p.destroy();
        }

        return outputString.toString();
    }

}
