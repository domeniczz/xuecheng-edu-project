package com.xuecheng.media.others;

import com.xuecheng.media.utils.FileUtils;
import com.xuecheng.media.utils.MinioUtils;

import org.apache.commons.io.IOUtils;
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
import java.io.FileOutputStream;
import java.io.FilterInputStream;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.SetBucketPolicyArgs;

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
    private MinioUtils minioUtils;

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
        bucketName = "testbucket" + (int) Math.floor(Math.random() * (100 + 1));

        // 文件在本地的路径
        localFilePath = localFolderPath + filename;

        // 文件下载后在本地的路径 (示例：xxx-test-download.xxx)
        testDownloadFilePath = localFolderPath + File.separator +
                filename.substring(0, filename.lastIndexOf(".")) +
                "-download" + filename.substring(filename.lastIndexOf("."));

        // 文件在 minio 中的保存路径 (路径 + 名称)
        objectName = "test/" + filename;
    }

    /**
    * 创建一个指定名称的 bucket
    */
    @Test
    @Order(1)
    void test_createBucket() {
        try {
            minioUtils.createBucket(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage() + "创建桶 (" + bucketName + ") 失败");
        }
    }

    /**
     * 设置 bucket 的访问策略为公共读
     */
    @Test
    @Order(2)
    void test_setBucketAccessPolicyPublic() throws Exception {
        String policy = "{" +
                "\"Version\":\"2012-10-17\"," +
                "   \"Statement\":[{" +
                "       \"Effect\":\"Allow\"," +
                "       \"Principal\":\"*\"," +
                "       \"Action\":\"s3:GetObject\"," +
                "       \"Resource\":\"arn:aws:s3:::" + bucketName + "/*\"" +
                "   }]" +
                "}";

        // 设置桶的访问策略，允许 API 执行增删改查操作
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
    }

    /**
     * 删除指定名称的 bucket
     */
    @Test
    @Order(6)
    void test_deleteBucket() throws Exception {
        minioUtils.deleteBucket(bucketName);
        Assertions.assertFalse(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()));
    }

    /**
     * 上传文件到 bucket
     */
    @Test
    @Order(3)
    void test_uploadFile() {
        try {
            ObjectWriteResponse resp = minioUtils.uploadFile(localFilePath,
                    FileUtils.getMimeTypeFromExt(filename.substring(filename.lastIndexOf("."))),
                    bucketName, objectName);
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
    void test_downloadFile() throws Exception {
        File src = new File(localFilePath);
        File dest = new File(testDownloadFilePath);

        try (FilterInputStream is = minioUtils.queryFile(bucketName, objectName);
                FileOutputStream os = new FileOutputStream(dest)) {
            // 将下载的数据流写入到目标文件
            IOUtils.copy(is, os);
        }

        // 通过 MD5 校验文件的完整性
        String srcMD5 = FileUtils.getFileMd5(src);
        String destMD5 = FileUtils.getFileMd5(dest);
        Assertions.assertEquals(srcMD5, destMD5, "MD5 校验失败");

        // 删除下载的文件
        boolean res = dest.delete();
        Assertions.assertTrue(res, "删除文件失败");
    }

    /**
     * 从 bucket 删除文件
     */
    @Test
    @Order(5)
    void test_deleteFile() throws Exception {
        minioUtils.deleteFile(bucketName, objectName);
    }

}
