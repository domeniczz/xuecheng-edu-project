package com.xuecheng.media.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.utils.FileUtils;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BigFilesServiceTest {

    @Autowired
    private BigFilesService bigFilesService;

    private long companyId;
    /**
     * 文件分块大小，minio 规定分块大小需要大于等于 5MB
     */
    @Value("${bigfile.chunk.size}")
    private int chunkSize;
    /**
     * 测试文件名
     */
    @Value("${bigfile.file.name}")
    private String filename;
    /**
     * 测试文件路径
     */
    @Value("${bigfile.folder.path}")
    private String folderPath;
    private String fileType;
    private FileParamsDto dto;
    private String fileMd5;

    private String chunkFolderPath;
    private int chunkTotalNum;

    @BeforeAll
    void setUp() throws IOException {

        // 机构 ID
        companyId = 100011L;
        // 文件类型：视频 (图片 001001; 视频 001002; 其他 001003)
        fileType = "001002";
        // 文件大小 (乱写的)
        long fileSize = 10000L;

        // 设置媒资文件操作请求参数 DTO
        dto = new FileParamsDto();
        dto.setFilename(filename);
        dto.setFileType(fileType);
        dto.setFileSize(fileSize);
        dto.setTags("测试文件 Unit Test");
        dto.setUsername("测试测试");
        dto.setRemark("测试测试");

        // 文件 MD5
        File file = new File(folderPath + filename);
        fileMd5 = FileUtils.getFileMd5(file);

        // 分块文件夹
        chunkFolderPath = folderPath + "chunk" + File.separator;

        // 创建分块文件夹 (若不存在)
        Path path = Paths.get(chunkFolderPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // 清空分块文件夹
        if (!FileUtils.isFolderEmpty(chunkFolderPath)) {
            FileUtils.clearFolderRecursively(chunkFolderPath);
        }

        // 准备测试文件的本地分块文件
        prepareChunkFiles();
    }

    @Test
    @Order(1)
    void test_checkFile_pre() {
        RestResponse<Boolean> res = bigFilesService.checkFile(fileMd5);
        Assertions.assertFalse(res.getResult(), "文件不应该存在");
    }

    @Test
    @Order(2)
    void test_uploadChunk() {
        // 将分块文件上传到 minio
        for (int i = 0; i < chunkTotalNum; ++i) {
            RestResponse<Boolean> res = bigFilesService.uploadChunk(fileMd5, i, chunkFolderPath + i);
            Assertions.assertTrue(res.getResult(), "分块文件" + i + "上传失败");
        }
    }

    @Test
    @Order(3)
    void test_checkChunk_pre() {
        // 检查分块文件是否存在
        for (int i = 0; i < chunkTotalNum; ++i) {
            RestResponse<Boolean> res = bigFilesService.checkChunk(fileMd5, i);
            Assertions.assertTrue(res.getResult(), "分块文件 " + i + " 不存在");
        }
    }

    @Test
    @Order(4)
    void test_mergeChunks() {
        RestResponse<Boolean> res = bigFilesService.mergeChunksAndSave(companyId, fileMd5, chunkTotalNum, dto);
        Assertions.assertTrue(res.getResult(), "分块文件合并失败");
    }

    @Test
    @Order(5)
    void test_checkChunk_post() {
        for (int i = 0; i < chunkTotalNum; ++i) {
            RestResponse<Boolean> res = bigFilesService.checkChunk(fileMd5, i);
            Assertions.assertFalse(res.getResult(), "分块文件 " + i + " 未被删除");
        }
    }

    @Test
    @Order(6)
    void test_checkFile_post() {
        RestResponse<Boolean> res = bigFilesService.checkFile(fileMd5);
        Assertions.assertTrue(res.getResult(), "文件不存在");
    }

    @Test
    @Order(7)
    void test_deleteFile() {
        RestResponse<Boolean> res = bigFilesService.deleteFile(getMergedFileObjectName());
        Assertions.assertTrue(res.getResult(), "文件删除失败");
    }

    /**
     * 准备测试文件的本地分块文件
     * @throws IOException IO 异常
     */
    private void prepareChunkFiles() throws IOException {
        // 创建分块文件夹 (若不存在)
        Path path = Paths.get(chunkFolderPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // 准备测试文件的本地分块
        File sourceFile = new File(folderPath + filename);
        // 计算分块总数
        chunkTotalNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        // 从源文件读数据，向分块文件中写数据
        try (RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");) {
            // 缓冲区
            byte[] buffer = new byte[1024];
            // 逐一向各个分块中写入数据
            for (int i = 0; i < chunkTotalNum; ++i) {
                // 分块文件名称就是该分块的编号
                File chunkedFile = new File(chunkFolderPath + i);
                // 向分块文件中写入数据
                try (RandomAccessFile raf_w = new RandomAccessFile(chunkedFile, "rw")) {
                    int len = -1;
                    // 按照缓冲区大小，从源文件中读取数据
                    while ((len = raf_r.read(buffer)) != -1) {
                        // 写入数据之前，检查是否会超过分块大小
                        long remaining = chunkSize - raf_w.length();
                        int writeLength = (int) Math.min(len, remaining);

                        raf_w.write(buffer, 0, writeLength);

                        // 若分块文件达到分块大小，就不再写入数据
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
     * 获取合并后的文件的路径 (objectName)
     * @return 文件路径
     */
    private String getMergedFileObjectName() {
        // 源文件名称
        String filename = dto.getFilename();
        // 文件扩展名
        String fileExt = filename.substring(filename.lastIndexOf("."));
        // 指定合并后文件的 objectname
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" +
                filename.substring(0, filename.lastIndexOf(".")) + "-" + fileMd5 + fileExt;
    }

}
