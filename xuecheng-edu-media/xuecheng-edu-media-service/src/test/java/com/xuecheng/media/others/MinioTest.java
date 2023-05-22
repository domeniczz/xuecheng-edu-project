package com.xuecheng.media.others;

import com.xuecheng.base.utils.FileUtil;
import com.xuecheng.media.operations.MinioOperation;

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

import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import io.minio.BucketExistsArgs;
import io.minio.GetBucketPolicyArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.MinioException;

/**
 * @author Domenic
 * @Classname MinioTest
 * @Description 测试 minio SDK 的基础功能
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MinioTest {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioOperation minioOperation;

    /**
     * 测试文件名
     */
    @Value("${mediafile.file.name}")
    private String filename;
    /**
     * 测试文件路径
     */
    @Value("${mediafile.folder.path}")
    private String localFolderPath;

    private String bucketName;
    private String localFilePath;
    private String testDownloadFilePath;
    private String objectName;

    @BeforeAll
    void setUp() {

        // minio 中的桶名
        bucketName = "testbucket" + new SecureRandom().nextInt(100);

        // 文件在本地的路径
        localFilePath = localFolderPath + filename;

        // 文件下载后在本地的路径 (示例：xxx-test-download.xxx)
        testDownloadFilePath = localFolderPath + FileSystems.getDefault().getSeparator() +
                FileUtil.dropFileExtension(filename) +
                "-download" + FileUtil.getFileExtension(filename);

        // 文件在 minio 中的保存路径 (路径 + 名称)
        objectName = "test/" + filename;
    }

    /**
    * 创建一个指定名称的 bucket
    */
    @Test
    @Order(1)
    void testCreateBucket() {
        try {
            minioOperation.createBucket(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage() + "创建桶 (" + bucketName + ") 失败");
        }
    }

    /**
     * 设置 bucket 的访问策略为公共读
     */
    @Test
    @Order(2)
    void testSetBucketAccessPolicyPublic() throws IOException, MinioException, GeneralSecurityException {
        String policy = "{\n" +
                "    \"Version\": \"2012-10-17\"," +
                "    \"Statement\": [" +
                "        {" +
                "            \"Effect\": \"Allow\"," +
                "            \"Principal\": {" +
                "                \"AWS\": [" +
                "                    \"*\"" +
                "                ]" +
                "            }," +
                "            \"Action\": [" +
                "                \"s3:GetObject\"" +
                "            ]," +
                "            \"Resource\": [\n" +
                "                \"arn:aws:s3:::" + bucketName + "/*\"" +
                "            ]" +
                "        }" +
                "    ]" +
                "}";

        // 设置桶的访问策略，允许 API 执行增删改查操作
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());

        String resPolicy = minioClient.getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucketName).build());
        Assertions.assertEquals(policy.replace(" ", "").replace("\n", ""),
                resPolicy.replace(" ", "").replace("\n", ""));
    }

    /**
     * 删除指定名称的 bucket
     */
    @Test
    @Order(6)
    void testDeleteBucket() throws IOException, MinioException, GeneralSecurityException {
        minioOperation.deleteBucket(bucketName);
        Assertions.assertFalse(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()));
    }

    /**
     * 上传文件到 bucket
     */
    @Test
    @Order(3)
    void testUploadFile() {
        try {
            ObjectWriteResponse resp = minioOperation.uploadFile(localFilePath,
                    FileUtil.getMimeType(filename), bucketName, objectName);
            Assertions.assertNotNull(resp, "上传文件失败");
            Assertions.assertEquals(resp.object(), objectName, "上传文件失败, 文件名称不一致");
        } catch (Exception e) {
            Assertions.fail(e.getMessage() + "上传文件 \"" + localFilePath + "\" 到 \"" + bucketName + "/" + objectName + "\" 失败");
        }
    }

    /**
     * 向 bucket 查询文件，并下载
     */
    @Test
    @Order(4)
    void testDownloadFile() throws IOException {
        Path src = Paths.get(localFilePath);
        Path dest = Paths.get(testDownloadFilePath);

        try (FilterInputStream in = minioOperation.queryFile(bucketName, objectName)) {
            // 将下载的数据流写入到目标文件
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }

        // 通过 MD5 校验文件的完整性
        String srcMD5 = FileUtil.getFileMd5(src);
        String destMD5 = FileUtil.getFileMd5(dest);
        Assertions.assertEquals(srcMD5, destMD5, "MD5 校验失败");

        // 删除下载的文件
        boolean res = Files.deleteIfExists(dest);
        Assertions.assertTrue(res, "删除文件失败");
    }

    /**
     * 从 bucket 删除文件
     */
    @Test
    @Order(5)
    void testDeleteFile() throws IOException {
        boolean res = minioOperation.deleteFile(bucketName, objectName);
        Assertions.assertTrue(res, "删除文件失败");
    }

}
