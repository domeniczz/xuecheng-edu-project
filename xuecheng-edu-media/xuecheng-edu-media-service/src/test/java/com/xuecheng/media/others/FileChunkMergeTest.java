package com.xuecheng.media.others;

import com.xuecheng.base.utils.FileUtil;
import com.xuecheng.media.operations.MinioOperation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import io.minio.ObjectWriteResponse;
import io.minio.errors.MinioException;

/**
 * @author Domenic
 * @Classname FileChunkingTest
 * @Description 本地和 Minio 中的 文件分块/分块合并 测试
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileChunkMergeTest {

    @Autowired
    private MinioOperation minioOperation;

    /**
     * 文件分块大小，minio 规定分块大小需要大于等于 5MB
     */
    @Value("${bigfile.chunk.size}")
    private int chunkSize;
    /**
     * 测试文件名
     */
    @Value("${bigfile.file.name}")
    private String sourceFilename;
    /**
     * 测试文件路径
     */
    @Value("${bigfile.folder.path}")
    private String sourceFolderPath;

    private String chunkFolderPath;

    private String mergedFilename;
    private String mergedFilenameMinio;

    private String bucketName;
    private String objectFolderPathMinio;

    @BeforeAll
    void setUp() throws IOException, MinioException, GeneralSecurityException {

        // 分块文件在本地的保存路径
        chunkFolderPath = sourceFolderPath + "chunk" + FileSystems.getDefault().getSeparator();
        // 分块文件在 minio 中的保存路径
        objectFolderPathMinio = "chunk/";

        String sourceFilenameWithoutExt = sourceFilename.substring(0, sourceFilename.lastIndexOf("."));
        String sourceFileExt = sourceFilename.substring(sourceFilename.lastIndexOf("."));

        // 分块合并后的文件的本地保存路径 (路径 + 名称)，示例：test-merge.mp4
        mergedFilename = sourceFilenameWithoutExt + "-merge" + sourceFileExt;
        mergedFilenameMinio = mergedFilename;

        // 创建分块文件夹 (若不存在)
        Path path = Paths.get(chunkFolderPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // 清空分块文件夹
        if (!FileUtil.isFolderEmpty(chunkFolderPath)) {
            FileUtil.clearFolderRecursively(chunkFolderPath);
        }

        // minio 中的桶名，示例：testbucket56
        bucketName = "testbucket" + new SecureRandom().nextInt(100);
        minioOperation.createBucket(bucketName);
    }

    /**
     * 本地文件分块测试
     * @throws IOException
     */
    @Test
    @Order(1)
    void testChunking() throws IOException {

        File sourceFile = new File(sourceFolderPath + sourceFilename);

        // 文件分块的数量
        int chunkTotalNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        Assertions.assertTrue(chunkTotalNum > 0, "文件分块数量不能小于 1");

        // 从源文件读数据，向分块文件中写数据
        try (RandomAccessFile rafRead = new RandomAccessFile(sourceFile, "r");) {
            // 缓冲区
            byte[] buffer = new byte[1024];

            // 逐一向各个分块中写入数据
            for (int i = 0; i < chunkTotalNum; ++i) {
                // 分块文件名称为分块序号
                File chunkedFile = new File(chunkFolderPath + i);

                // 向分块文件中写入数据
                try (RandomAccessFile rafWrite = new RandomAccessFile(chunkedFile, "rw")) {
                    int len = -1;
                    // 按照缓冲区大小，从源文件中读取数据
                    while ((len = rafRead.read(buffer)) != -1) {
                        long remaining = chunkSize - rafWrite.length();
                        int writeLength = (int) Math.min(len, remaining);

                        rafWrite.write(buffer, 0, writeLength);

                        // 如果分块文件达到分块大小，就不再写入数据
                        if (rafWrite.length() == chunkSize) {
                            break;
                        }
                        // 若分块文件超过分块大小，就抛出异常
                        else if (rafWrite.length() > chunkSize) {
                            throw new IllegalStateException("The chunk file \"" + i + "\" exceeds the predefined chunk size of \"" + chunkSize + "\"");
                        }
                    }
                }
            }
        }
    }

    /**
     * 本地分块文件合并测试
     * @throws IOException
     */
    @Test
    @Order(2)
    void testMerging() throws IOException {
        File sourceFolder = new File(sourceFolderPath);
        File chunkFolder = new File(chunkFolderPath);
        File mergedFile = new File(sourceFolderPath + mergedFilename);

        // 获取分块文件列表
        File[] files = chunkFolder.listFiles();
        Assertions.assertNotNull(files, "分块文件列表为空");
        List<File> fileList = Arrays.asList(files);

        // 对分块文件夹下的文件，进行排序 (分块文件名称为分块序号)
        fileList.sort((f1, f2) -> Integer.parseInt(f1.getName()) - Integer.parseInt(f2.getName()));

        try (RandomAccessFile rafWrite = new RandomAccessFile(mergedFile, "rw")) {
            // 缓冲区
            byte[] buffer = new byte[1024];

            // 遍历分块文件，写入合并文件
            for (File file : fileList) {
                // 读取分块文件
                try (RandomAccessFile rafRead = new RandomAccessFile(file, "r")) {
                    int len = -1;
                    // 按照缓冲区大小，从源文件中读取数据
                    while ((len = rafRead.read(buffer)) != -1) {
                        rafWrite.write(buffer, 0, len);
                    }
                }
            }
        }

        Assertions.assertTrue(mergedFile.exists(), "合并文件不存在, 合并失败");
        Assertions.assertTrue(mergedFile.length() > 0, "合并文件长度为 0, 合并失败");
        Assertions.assertEquals(mergedFile.getName(), mergedFilename);

        // 合并文件完成后，对合并的文件 MD5 校验
        String sourceMD5 = FileUtil.getFileMd5(Paths.get(sourceFolder + FileSystems.getDefault().getSeparator() + sourceFilename));
        String mergeMD5 = FileUtil.getFileMd5(mergedFile.toPath());
        Assertions.assertEquals(sourceMD5, mergeMD5, "MD5 校验失败");
    }

    /**
     * 测试上传本地分块文件到 minio
     */
    @Test
    @Order(3)
    void testUploadChunksToMinio() throws IOException {
        // 将分块文件上传到 minio
        try (Stream<Path> stream = Files.list(Paths.get(chunkFolderPath))) {
            stream.filter(Files::isRegularFile).forEach(file -> {
                try {
                    String objectName = objectFolderPathMinio + file.getFileName().toString();
                    ObjectWriteResponse resp = minioOperation.uploadFile(file.toAbsolutePath().toString(),
                            FileUtil.getMimeTypeFromExt(""),
                            bucketName, objectName);
                    Assertions.assertNotNull(resp, "上传文件失败");
                    Assertions.assertEquals(resp.object(), objectName, "上传文件失败, 文件名称不一致");
                } catch (Exception e) {
                    Assertions.fail(e.getMessage() + " 文件上传失败 (" + file.toAbsolutePath() + ")");
                }
            });
        }
    }

    /**
     * 测试分块文件在 minio 上进行合并
     */
    @Test
    @Order(4)
    void testMergeChunksOnMinio() throws IOException {
        // 合并文件
        ObjectWriteResponse resp = minioOperation.mergeChunks(bucketName, mergedFilenameMinio, objectFolderPathMinio);
        Assertions.assertNotNull(resp, "合并文件失败");
        Assertions.assertEquals(resp.object(), mergedFilenameMinio);
    }

    @AfterAll
    void tearDown() throws IOException, MinioException, GeneralSecurityException {

        /* 删除本地测试产生的文件 */

        // 删除分块文件
        FileUtil.deleteFolder(chunkFolderPath);

        // 删除合并文件
        FileUtil.deleteFile(sourceFolderPath + mergedFilename);

        /* 删除 Minio 中测试产生的文件 */

        // 删除分块文件夹
        minioOperation.clearFolderNonRecursively(bucketName, chunkFolderPath);

        // 删除和合并后的文件
        minioOperation.deleteFile(bucketName, mergedFilenameMinio);

        // 删除测试桶
        minioOperation.deleteBucket(bucketName);
    }

}
