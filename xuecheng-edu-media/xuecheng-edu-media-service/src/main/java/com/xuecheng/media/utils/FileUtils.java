package com.xuecheng.media.utils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname FileUtils
 * @Description 本地文件操作的工具类
 * @Created by Domenic
 */
@Slf4j
public class FileUtils {

    /**
     * 删除文件
     * @param filePath 文件路径 (路径 + 文件名)
     * @throws IOException IO 异常
     */
    public static void deleteLocalFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            Files.delete(path);
        } else {
            throw new IllegalArgumentException("The provided path is not a file OR the file does not exist!");
        }
    }

    /**
     * 判断文件是否为空
     * @param filePath 文件路径
     * @return {@link Boolean} 是否为空
     */
    public static boolean isFileEmpty(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            try {
                long fileSize = Files.size(path);
                return fileSize == 0;
            } catch (IOException e) {
                System.out.println("Error occurred while checking if file is empty: " + e.getMessage());
            }
            return false;
        } else {
            throw new IllegalArgumentException("The provided path is not a file OR the file does not exist!");
        }
    }

    /**
     * 判断文件夹是否为空
     * @param folderPath 文件夹路径
     * @return {@link Boolean} 是否为空
     * @throws IOException IO 异常
     */
    public static boolean isFolderEmpty(String folderPath) throws IOException {
        Path dir = Paths.get(folderPath);
        if (Files.isDirectory(dir)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
                return !directoryStream.iterator().hasNext();
            }
        } else {
            throw new IllegalArgumentException("The provided path is not a directory!");
        }
    }

    /**
     * 递归地清空文件夹 (不删除文件夹本身)
     * @param folderPath 文件夹路径
     * @throws IOException IO 异常
     */
    public static void clearFolderRecursively(String folderPath) throws IOException {
        Path dir = Paths.get(folderPath);
        if (Files.isDirectory(dir)) {
            Files.walk(dir)
                    // 反向排序，这样文件和子目录会在它们的父目录之前被删除
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile).forEach(File::delete);
        } else {
            throw new IllegalArgumentException("The provided path is not a directory.");
        }
    }

    /**
     * 非递归地清空文件夹 (不删除文件夹本身)
     * @param folderPath 文件夹路径
     * @throws IOException IO 异常
     */
    public static void clearFolderNonRecursively(String folderPath) throws IOException {
        Path dir = Paths.get(folderPath);
        if (Files.isDirectory(dir)) {
            // 只删除顶层目录中的文件
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
                for (Path path : directoryStream) {
                    Files.deleteIfExists(path);
                }
            }
        } else {
            throw new IllegalArgumentException("The provided path is not a directory.");
        }
    }

    /**
     * 递归地删除文件夹 (包括此文件夹本身)
     * @param folderPath 文件夹路径
     * @throws IOException IO 异常
     */
    public static void deleteFolder(String folderPath) throws IOException {
        Path dir = Paths.get(folderPath);
        if (Files.isDirectory(dir)) {
            try (Stream<Path> stream = Files.list(dir)) {
                stream.forEach(file -> {
                    try {
                        if (Files.isDirectory(file)) {
                            deleteFolder(file.toString());
                        } else {
                            Files.delete(file);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            Files.delete(dir);
        }
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
        if (ext == null || ext.trim().isEmpty()) {
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
    public static String getFileMd5(File file) {
        try (InputStream is = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            log.error("获取文件 \"" + file.getName() + "\" MD5 出错!", e);
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
