package com.xuecheng.media.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname TempFileUtils
 * @Description 文件操作的工具类
 * @Created by Domenic
 */
@Slf4j
public class TempFileUtils {

    /**
    * 将给定的 MultipartFile 文件存储为临时文件，并返回临时文件的绝对路径
    * @param filedata {@link MultipartFile} 文件
    * @return 临时文件的绝对路径
    * @throws IOException 创建临时文件时的 IO 异常
    */
    public static String storeAsTempFile(MultipartFile filedata) throws IOException {
        // 创建一个临时文件
        File tempFile = File.createTempFile("upload-to-minio-" + getUuid(), ".temp");
        filedata.transferTo(tempFile);

        // 返回临时文件路径
        return tempFile.getAbsolutePath();
    }

    /**
    * 判断给定的 MultipartFile 的文件类型
    * @param file {@link MultipartFile} 文件
    * @return 表示文件类型的编码 ("001001" - 图片，"001002" - 视频，"001003" - 其他，null - 未知)
    */
    public static String getFileType(MultipartFile file) {
        // 获取文件的 MIME 类型
        String contentType = file.getContentType();

        String imageType = "image/";
        String videoType = "video/";

        if (contentType != null) {
            if (contentType.startsWith(imageType)) {
                // 图片
                return "001001";
            } else if (contentType.startsWith(videoType)) {
                // 视频
                return "001002";
            } else {
                // 其他
                return "001003";
            }
        }

        return null;
    }

    /**
     * 删除临时文件
     * @param tempFilePath 临时文件的绝对路径
     */
    public static void deleteTempFile(String tempFilePath) {
        if (!new File(tempFilePath).delete()) {
            log.error("删除临时文件 {} 出错", tempFilePath);
        }
    }

    /**
     * 生成 UUID
     * @return {@link UUID} 字符串
     */
    private static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
