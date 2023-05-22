package com.xuecheng.base.utils;

import com.xuecheng.base.config.CustomPropertiesReader;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname FileUtil
 * @Description 文件工具类
 * @Created by Domenic
 */
@Slf4j
public class FileUtil {

    private FileUtil() {
        // prevents other classes from instantiating it
    }

    /**
     * 读取文件内容，并作为字符串返回
     * @param filePath 文件路径
     * @return 文件内容 {@link String}
     * @throws IOException IO 异常
     */
    public static String readFileAsString(String filePath) throws IOException {
        Path file = Paths.get(filePath);
        if (!Files.exists(file)) {
            throw new FileNotFoundException("File not found, filePath=(" + filePath + ")");
        }

        long fileSize = Files.size(file);
        // 文件最大字节数
        long maxLen = CustomPropertiesReader.MAX_FILE_READ_SIZE;

        if (fileSize > maxLen) {
            throw new IOException("File is too large, it should be less than " + maxLen + ", filePath=(" + filePath + ")");
        }

        StringBuilder sb = new StringBuilder((int) (fileSize));

        // 使用输入流来读取文件内容
        try (FileInputStream fis = new FileInputStream(filePath)) {
            // 创建一个的缓冲区
            byte[] buffer = new byte[10240];
            // 记录实际读取的字节数
            int readBytes = -1;

            while ((readBytes = fis.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, readBytes));
            }
        }

        return sb.toString();
    }

    /**
     * 读取文件内容，并作为字节数组返回
     * @param filePath 文件路径
     * @return 文件内容 {@link Byte}[]
     * @throws IOException IO 异常
     */
    public static byte[] readFileAsBytes(String filePath) throws IOException {
        Path file = Paths.get(filePath);
        if (!Files.exists(file)) {
            throw new FileNotFoundException("File not found, filepath=(" + filePath + ")");
        }

        long fileSize = Files.size(file);
        // 文件最大字节数
        long maxLen = CustomPropertiesReader.MAX_FILE_READ_SIZE;

        if (fileSize > maxLen) {
            throw new IOException("File is too large, it should be less than " + maxLen + ", filePath=(" + filePath + ")");
        }

        // 使用输入流来读取文件内容
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) fileSize);
                BufferedInputStream bis = new BufferedInputStream(file.toUri().toURL().openStream())) {
            int bufSize = 10240;
            // 创建一个的缓冲区
            byte[] buffer = new byte[bufSize];
            // 记录实际读取的字节数
            int readBytes = -1;

            while ((readBytes = bis.read(buffer, 0, bufSize)) != -1) {
                bos.write(buffer, 0, readBytes);
            }

            return bos.toByteArray();
        }
    }

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
                log.error("Error occurred while checking if file is empty: " + e.getMessage());
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
            try (Stream<Path> stream = Files.walk(dir)) {
                // 反向排序，这样文件和子目录会在它们的父目录之前被删除
                stream.sorted(Comparator.reverseOrder())
                        .map(Path::toFile).forEach(File::delete);
            }
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
            if (!StringUtils.isBlank(val)) {
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
                if (!StringUtils.isBlank(val)) {
                    writer.write(val);
                    writer.newLine();
                }
            }
        }
    }

}
