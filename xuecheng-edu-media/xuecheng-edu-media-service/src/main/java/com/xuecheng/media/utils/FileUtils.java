package com.xuecheng.media.utils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname FileOperation
 * @Description 本地文件操作的工具类
 * @Created by Domenic
 */
@Slf4j
public class FileUtils {

    private FileUtils() {
        // prevents other classes from instantiating it
    }

    /**
     * 获取文件在 minio 中的默认存储路径，示例：年/月/日
     * @param year 路径是否包含 年
     * @param month 路径是否包含 月
     * @param day 路径是否包含 日
     * @return 文件夹路径
     */
    public static String getFolderPathByDate(boolean year, boolean month, boolean day) {
        LocalDate now = LocalDate.now();

        StringBuilder path = new StringBuilder();

        if (year) {
            path.append(now.getYear());
        }

        if (month && path.length() > 0) {
            path.append('/');
            path.append(String.format("%02d", now.getMonthValue()));
        }

        if (day && path.length() > 0) {
            path.append('/');
            path.append(String.format("%02d", now.getDayOfMonth()));
        }

        return path.append('/').toString();
    }

    /**
     * <p>
     * 根据扩展名获取文件的 mimeType<br/>
     * 传入值为 {@code null} 或 空 表示没有扩展名，返回通用 mimeType，表示是一个字节流
     * </p>
     * @param ext 文件扩展名
     * @return mimeType 类型字符串
     */
    public static String getMimeTypeFromExt(String ext) {

        // 若 ext 是 null 或为空白，则设置其值为空字符串
        if (StringUtils.isBlank(ext)) {
            ext = "";
        }

        // 根据扩展名获取 mimeType
        ContentInfo extMatch = ContentInfoUtil.findExtensionMatch(ext);

        // 通用的 mimeType，表示是一个字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (extMatch != null) {
            mimeType = extMatch.getMimeType();
        }

        return mimeType;
    }

    /**
     * 获取文件的 MD5
     * @param file 文件
     * @return MD5 字符串
     */
    public static String getFileMd5(Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            log.error("获取文件 \"{}\" MD5 出错, errorMsg={}", path.getFileName(), e.getMessage());
            return null;
        }
    }

    /**
     * 生成 UUID (32 位 16 进制数)
     * @return {@link UUID} 字符串
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
