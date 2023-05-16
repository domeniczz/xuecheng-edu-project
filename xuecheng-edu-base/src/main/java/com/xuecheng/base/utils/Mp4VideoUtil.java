package com.xuecheng.base.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Domenic
 * @Classname Mp4VideoUtil
 * @Description 视频处理工具类，输出 mp4 格式视频
 * @Created by Domenic
 */
public class Mp4VideoUtil extends AbstractVideoUtil {

    /**
     * 转换视频编码，生成 mp4 文件
     * @param originalVideoPath 源视频路径
     * @param outputVideoPath 输出视频路径
     * @return 成功返回 success，失败返回控制台日志
     */
    public static String generateMp4(String originalVideoPath, String outputVideoPath) {
        // (若存在) 清除已生成的 mp4
        clearMp4(outputVideoPath);

        /*
         * ffmpeg.exe -i example.avi -c:v libx264 -s 1280x720 -pix_fmt yuv420p -b:a 63k -b:v 753k -r 18 .\example.mp4
         */
        List<String> cmd = new ArrayList<String>();
        cmd.add(AbstractVideoUtil.FFMPEG_PATH);
        cmd.add("-i");
        cmd.add(originalVideoPath);
        cmd.add("-c:v");
        cmd.add("libx264");
        // 覆盖输出文件
        cmd.add("-y");
        cmd.add("-s");
        cmd.add("1280x720");
        cmd.add("-pix_fmt");
        cmd.add("yuv420p");
        cmd.add("-b:a");
        cmd.add("63k");
        cmd.add("-b:v");
        cmd.add("753k");
        cmd.add("-r");
        cmd.add("18");
        cmd.add(outputVideoPath);

        String outstring = null;

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(cmd);
        // 将标准输入流和错误输入流合并，通过标准输入流程读取信息
        builder.redirectErrorStream(true);

        try {
            Process p = builder.start();
            outstring = execute(p);
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 校验视频时长是否一致
        if (!checkVideoTime(originalVideoPath, outputVideoPath)) {
            return outstring;
        } else {
            return "success";
        }
    }

    /**
     * 清除已生成的 mp4
     * @param mp4FilePath 
     */
    private static void clearMp4(String mp4FilePath) {
        // 删除原来已经生成的视频文件
        Path filePath = Paths.get(mp4FilePath);
        try {
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}