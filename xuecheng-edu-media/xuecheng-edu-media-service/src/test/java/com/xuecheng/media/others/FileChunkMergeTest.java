package com.xuecheng.media.others;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Domenic
 * @Classname FileChunkingTest
 * @Description 文件分块/分块合并 测试
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileChunkMergeTest {

    @Autowired
    CommonCode commonCode;

    /**
     * 文件分块大小 (1MB)
     */
    private int CHUNK_SIZE;

    private String sourceFilename;
    private String sourceFilenameWithoutExt;
    private String sourceFileExt;
    private String sourceFolderPath;
    // 分块文件的存储路径
    private String chunkFolderPath;

    private String bucketName;
    private String objectFolderPath;

    @BeforeAll
    void setUp() throws Exception {
        CHUNK_SIZE = 1024 * 1024 * 1;

        sourceFilename = "test.mp4";
        sourceFolderPath = "D:/Download/Video/";
        chunkFolderPath = "D:/Download/Video/chunk/";
        sourceFilenameWithoutExt = sourceFilename.substring(0, sourceFilename.lastIndexOf("."));
        sourceFileExt = sourceFilename.substring(sourceFilename.lastIndexOf("."));

        // 创建分块文件夹 (若不存在)
        Path path = Paths.get(chunkFolderPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // minio 中的桶名称
        bucketName = "testbucket" + (int) Math.floor(Math.random() * (100 + 1));
        // 文件在 minio 中的保存路径
        objectFolderPath = "chunk/";

        // 创建桶
        commonCode.createBucket(bucketName);
    }

    /**
     * (本地) 文件分块测试
     * @throws IOException
     */
    @Test
    @Order(1)
    void test_chunking() throws IOException {

        File sourceFile = new File(sourceFolderPath + sourceFilename);

        // 文件分块的数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / CHUNK_SIZE);

        // 从源文件读数据，向分块文件中写数据
        try (RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");) {
            // 缓冲区
            byte[] buffer = new byte[1024];

            // 逐一向各个分块中写入数据
            for (int i = 0; i < chunkNum; i++) {
                // 分块文件名称示例：xxx-1.chunk
                File chunkedFile = new File(chunkFolderPath + sourceFilenameWithoutExt + "-" + i + ".chunk");

                // 向分块文件中写入数据
                try (RandomAccessFile raf_w = new RandomAccessFile(chunkedFile, "rw")) {
                    int len = -1;
                    // 按照缓冲区大小，从源文件中读取数据
                    while ((len = raf_r.read(buffer)) != -1) {
                        raf_w.write(buffer, 0, len);
                        // 如果分块文件达到分块大小 (1MB)，就不再写入数据
                        if (chunkedFile.length() >= CHUNK_SIZE) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * (本地) 分块合并测试
     * @throws IOException
     */
    @Test
    @Order(2)
    void test_merging() throws IOException {
        File sourceFolder = new File(sourceFolderPath);
        File chunkFolder = new File(chunkFolderPath);
        File mergedFile = new File(sourceFolderPath + sourceFilenameWithoutExt + "-merge" + sourceFileExt);

        // 获取分块文件列表
        File[] files = chunkFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        Assertions.assertNotNull(fileList, "分块文件列表为空");

        // 对分块文件排序(分块文件名称示例：xxx-1.chunk)
        int start = sourceFilenameWithoutExt.length() + 1;
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                String name1 = f1.getName();
                String name2 = f2.getName();
                return Integer.parseInt(name1.substring(start, name1.lastIndexOf(".")))
                        - Integer.parseInt(name2.substring(start, name2.lastIndexOf(".")));
            }
        });

        try (RandomAccessFile raf_w = new RandomAccessFile(mergedFile, "rw")) {
            // 缓冲区
            byte[] buffer = new byte[1024];

            // 遍历分块文件，写入合并文件
            for (File file : fileList) {
                // 读取分块文件
                try (RandomAccessFile raf_r = new RandomAccessFile(file, "r")) {
                    int len = -1;
                    // 按照缓冲区大小，从源文件中读取数据
                    while ((len = raf_r.read(buffer)) != -1) {
                        raf_w.write(buffer, 0, len);
                    }
                }
            }
        }

        FileInputStream source_fis = new FileInputStream(sourceFolder + File.separator + sourceFilename);
        FileInputStream merge_fis = new FileInputStream(mergedFile);
        // 合并文件完成后，对合并的文件 MD5 校验
        String sourceMD5 = DigestUtils.md5Hex(source_fis);
        String mergeMD5 = DigestUtils.md5Hex(merge_fis);
        source_fis.close();
        merge_fis.close();
        Assertions.assertEquals(sourceMD5, mergeMD5, "MD5 校验失败");
    }

    /**
     * (Minio) 上传分块文件
     * @throws IOException
     */
    @Test
    void test_uploadChunksToMinio() throws IOException {
        Path dir = Paths.get(chunkFolderPath);
        Files.list(dir).filter(Files::isRegularFile).forEach(file -> {
            try {
                commonCode.uploadFileToMinio(bucketName, file.toAbsolutePath().toString(), objectFolderPath + file.toFile().getName());
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail(e.getMessage() + "\n上传文件失败 (" + file.toAbsolutePath().toString() + ")");
            }
        });
    }

    @Test
    void test_mergeFilesOnMinio() {

    }

    @AfterAll
    void tearDown() throws IOException {
        // 清除分块文件 (并删除文件夹)
        Path dir = Paths.get(chunkFolderPath);
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });

        // 删除合并文件
        Files.delete(Paths.get(sourceFolderPath + sourceFilenameWithoutExt + "-merge" + sourceFileExt));
    }

}
