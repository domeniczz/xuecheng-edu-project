package com.xuecheng.media.others;

import com.xuecheng.media.utils.FileUtils;
import com.xuecheng.media.utils.MinioUtils;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import io.minio.ComposeSource;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.Result;
import io.minio.messages.Item;

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
    private MinioClient minioClient;

    @Autowired
    private MinioUtils minioUtils;

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

    private String sourceFilenameWithoutExt;
    private String chunkFolderPath;
    private int chunkTotalNum;

    private String mergedFilename;
    private String mergedFilenameMinio;

    private String bucketName;
    private String objectFolderPathMinio;

    @BeforeAll
    void setUp() throws Exception {

        // 分块文件在本地的保存路径
        chunkFolderPath = sourceFolderPath + "chunk" + File.separator;
        // 分块文件在 minio 中的保存路径
        objectFolderPathMinio = "chunk/";

        sourceFilenameWithoutExt = sourceFilename.substring(0, sourceFilename.lastIndexOf("."));
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
        if (!FileUtils.isFolderEmpty(chunkFolderPath)) {
            FileUtils.clearFolderRecursively(chunkFolderPath);
        }

        // minio 中的桶名，示例：testbucket56
        bucketName = "testbucket" + (int) Math.floor(Math.random() * (100 + 1));
        minioUtils.createBucket(bucketName);
    }

    /**
     * 本地文件分块测试
     * @throws IOException
     */
    @Test
    @Order(1)
    void test_chunking() throws IOException {

        File sourceFile = new File(sourceFolderPath + sourceFilename);

        // 文件分块的数量
        chunkTotalNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        // 从源文件读数据，向分块文件中写数据
        try (RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");) {
            // 缓冲区
            byte[] buffer = new byte[1024];

            // 逐一向各个分块中写入数据
            for (int i = 0; i < chunkTotalNum; ++i) {
                // 分块文件名称示例：test-1.chunk
                File chunkedFile = new File(chunkFolderPath + sourceFilenameWithoutExt + "-" + i + ".chunk");

                // TODO: 更改分块，可能是这里有 bug
                // 向分块文件中写入数据
                try (RandomAccessFile raf_w = new RandomAccessFile(chunkedFile, "rw")) {
                    int len = -1;
                    // 按照缓冲区大小，从源文件中读取数据
                    while ((len = raf_r.read(buffer)) != -1) {
                        long remaining = chunkSize - raf_w.length();
                        int writeLength = (int) Math.min(len, remaining);

                        raf_w.write(buffer, 0, writeLength);

                        // 如果分块文件达到分块大小，就不再写入数据
                        if (raf_w.length() == chunkSize) {
                            break;
                        }
                        // 若分块文件超过分块大小，就抛出异常
                        else if (raf_w.length() > chunkSize) {
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
    void test_merging() throws IOException {
        File sourceFolder = new File(sourceFolderPath);
        File chunkFolder = new File(chunkFolderPath);
        File mergedFile = new File(sourceFolderPath + mergedFilename);

        // 获取分块文件列表
        File[] files = chunkFolder.listFiles();
        Assertions.assertNotNull(files, "分块文件列表为空");
        List<File> fileList = Arrays.asList(files);

        // 对分块文件夹下的文件，进行排序 (分块文件名称示例：test-1.chunk)
        int start = sourceFilenameWithoutExt.length() + 1;
        fileList.sort(new Comparator<File>() {
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

        Assertions.assertTrue(mergedFile.exists(), "合并文件不存在, 合并失败");
        Assertions.assertTrue(mergedFile.length() > 0, "合并文件长度为 0, 合并失败");
        Assertions.assertEquals(mergedFile.getName(), mergedFilename);

        // 合并文件完成后，对合并的文件 MD5 校验
        try (FileInputStream src_fis = new FileInputStream(sourceFolder + File.separator + sourceFilename);
                FileInputStream merge_fis = new FileInputStream(mergedFile)) {
            String sourceMD5 = DigestUtils.md5Hex(src_fis);
            String mergeMD5 = DigestUtils.md5Hex(merge_fis);
            Assertions.assertEquals(sourceMD5, mergeMD5, "MD5 校验失败");
        }
    }

    /**
     * 测试上传本地分块文件到 minio
     */
    @Test
    @Order(3)
    void test_uploadChunksToMinio() throws Exception {
        // 将分块文件上传到 minio
        try (Stream<Path> stream = Files.list(Paths.get(chunkFolderPath))) {
            stream.filter(Files::isRegularFile).forEach(file -> {
                try {
                    String objectName = objectFolderPathMinio + file.getFileName().toString();
                    ObjectWriteResponse resp = minioUtils.uploadFile(file.toAbsolutePath().toString(),
                            FileUtils.getMimeTypeFromExt(sourceFilename.substring(sourceFilename.lastIndexOf("."))),
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
    void test_mergeChunksOnMinio() throws Exception {
        // 初始化用来保存 ComposeSource 的 List
        List<ComposeSource> sources = new ArrayList<>();

        // 列出 minio 中的分块文件
        Iterable<Result<Item>> minioFiles = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(objectFolderPathMinio)
                .recursive(false)
                .build());

        // 遍历所有分块文件，为每个分块文件创建 ComposeSource 对象，并保存到 List 中
        for (Result<Item> object : minioFiles) {
            Item item = object.get();
            // 获取分块文件的 minio 路径
            String chunkFileObjectName = item.objectName();

            // 根据分块文件，创建出 ComposeSource 对象，保存到 List 中
            sources.add(ComposeSource.builder()
                    .bucket(bucketName)
                    .object(chunkFileObjectName)
                    .build());
        }

        // 合并文件
        ObjectWriteResponse resp = minioUtils.mergeChunks(bucketName, mergedFilenameMinio, sources);
        Assertions.assertNotNull(resp, "合并文件失败");
        Assertions.assertEquals(resp.object(), mergedFilenameMinio);
    }

    @AfterAll
    void tearDown() throws Exception {

        /* 删除本地测试产生的文件 */

        // 删除分块文件
        FileUtils.deleteFolder(chunkFolderPath);

        // 删除合并文件
        FileUtils.deleteLocalFile(sourceFolderPath + mergedFilename);

        /* 删除 Minio 中测试产生的文件 */

        // 删除分块文件夹
        minioUtils.deleteFolderRecursively(bucketName, objectFolderPathMinio);

        // 删除和合并后的文件
        minioUtils.deleteFile(bucketName, mergedFilenameMinio);

        // 删除测试桶
        minioUtils.deleteBucket(bucketName);
    }

}
