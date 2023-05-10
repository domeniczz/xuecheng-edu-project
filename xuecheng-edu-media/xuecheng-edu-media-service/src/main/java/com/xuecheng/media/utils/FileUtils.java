package com.xuecheng.media.utils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import org.springframework.http.MediaType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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
     * 创建临时文件
     * @param fileName 文件名
     * @param fileExt 文件扩展名
     * @return {@link Path}
     * @throws IOException IO 异常
     */
    public static Path createTempFileIfNotExist(String fileName, String fileExt) throws IOException {
        String tempDirectoryPath = System.getProperty("java.io.tmpdir");
        Path filePath = Paths.get(tempDirectoryPath, fileName + fileExt);

        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }

        return filePath;
    }

    /**
     * 删除文件
     * @param filePath 文件路径 (路径 + 文件名)
     * @throws IOException IO 异常
     */
    public static void deleteFile(String filePath) throws IOException {
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
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
                throw e;
            }
        });
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
            log.error("获取文件 \"{}\" MD5 出错, errorMag={}", file.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * 将 item 的值写入 txt 文件 (文件保存在临时目录中)
     * @param items {@link T}
     * @param outputFileName 输出文件的名称
     * @throws IOException IO 异常
     */
    public static <T> void writeItemToFile(T item, String outputFileName) throws IOException {
        Path outputPath = createTempFileIfNotExist(outputFileName, ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            String val = String.valueOf(item);
            // 若值为 null 或 为空 或 只包含空格，则跳过
            // 若不是则写入
            if (val != null && !val.trim().isEmpty()) {
                writer.write(val);
                writer.newLine();
            }
        }
    }

    /**
     * 将 {@link List}&lt;{@link T}&gt; 中的值一行行写入 txt 文件 (文件保存在临时目录中)
     * @param items {@link List}&lt;{@link T}&gt;
     * @param outputFileName 输出文件的名称
     * @throws IOException IO 异常
     */
    public static <T> void writeListToFile(List<T> items, String outputFileName) throws IOException {
        Path outputPath = createTempFileIfNotExist(outputFileName, ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            for (T item : items) {
                String val = String.valueOf(item);
                // 若值为 null 或 为空 或 只包含空格，则跳过
                // 若不是则写入
                if (val != null && !val.trim().isEmpty()) {
                    writer.write(val);
                    writer.newLine();
                }
            }
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
