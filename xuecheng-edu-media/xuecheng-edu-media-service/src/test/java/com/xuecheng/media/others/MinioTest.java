package com.xuecheng.media.others;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.util.Objects;

/**
 * @author Domenic
 * @Classname MinioTest
 * @Description 测试 minio SDK 的基础功能
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MinioTest {

    @Autowired
    MinioClient minioClient;

    @Autowired
    CommonCode commonCode;

    private static String filename;
    private static String bucketName;
    private static String localFilePath;
    private static String testDownloadFilePath;
    private static String objectName;

    @BeforeAll
    static void setUp() {
        // 测试文件名称
        filename = "bootstrap.yml";

        // 测试文件路径：target/test-classes，substring 是为了去除 "file:/" 前缀
        String basePath = Objects.requireNonNull(MinioTest.class.getResource("/")).toString().substring(6);

        // minio 中的桶名称
        bucketName = "testbucket" + (int) Math.floor(Math.random() * (100 + 1));

        // // 路径：target/test-classes，substring 是为了去除 "file:/" 前缀
        String basePath = Objects.requireNonNull(MinioTest.class.getResource("/")).toString().substring(6);
        // 文件在本地的路径
        localFilePath = basePath + "bootstrap.yml";
        // 文件下载后在本地的保存路径
        testDownloadFilePath = basePath + "bootstrap-test-download.yml";
        // 文件在 minio 中的保存路径
        objectName = "test/bootstrap.yml";
    }

    /**
    * 创建一个指定名称的 bucket
    */
    @Test
    @Order(1)
    void test_createBucket() throws Exception {
        // 检查桶是否存在
        boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exist) {
            // 创建桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            System.out.println(" ========== Bucket \"" + bucketName + "\" Created ========== ");
        } else {
            System.out.println(" ========== Bucket \"" + bucketName + "\" Already Exists ========== ");
        }
    }

    /**
     * 设置 bucket 的访问策略为公共读
     */
    @Test
    @Order(2)
    void test_setBucketAccessPolicyPublic() throws Exception {
        // 设置桶的访问策略，允许 API 执行增删改查操作
        String policy = "{" +
                "\"Version\":\"2012-10-17\"," +
                "   \"Statement\":[{" +
                "   \"Effect\":\"Allow\"," +
                "   \"Principal\":\"*\"," +
                "   \"Action\":\"s3:GetObject\"," +
                "   \"Resource\":\"arn:aws:s3:::" + bucketName + "/*\"" +
                "}]}";
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
        System.out.println(" ========== Bucket \"" + bucketName + "\" Access Policy Set ========== ");
    }

    /**
     * 删除指定名称的 bucket
     */
    @Test
    @Order(6)
    void test_deleteBucket() throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        System.out.println(" ========== Bucket \"" + bucketName + "\" Deleted ========== ");
    }

    /**
     * 上传文件到 bucket
     */
    @Test
    @Order(3)
    void test_uploadFile() throws Exception {
        // 通过扩展名得到媒体资源类型 mimeType
        // 根据扩展名取出 mimeType
        ContentInfo extMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        // 通用 mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extMatch != null) {
            mimeType = extMatch.getMimeType();
        }

        // 上传文件的参数信息
        UploadObjectArgs args = UploadObjectArgs.builder()
                // 桶
                .bucket(bucketName)
                // 指定本地文件 (路径)
                .filename(localFilePath)
                // 对象名 (路径)
                .object(objectName)
                // 设置媒体文件类型
                .contentType(mimeType)
                .build();

        // 上传文件
        ObjectWriteResponse resp = minioClient.uploadObject(args);
        System.out.println(resp);
    }

    /**
     * 向 bucket 查询文件，并下载
     */
    @Test
    @Order(4)
    void test_downloadFile() throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        File src = new File(localFilePath);
        File dest = new File(testDownloadFilePath);

        // 查询远程服务获取到一个流对象
        FilterInputStream is = minioClient.getObject(args);

        // 指定输出流
        FileOutputStream os = new FileOutputStream(dest);
        IOUtils.copy(is, os);

        FileInputStream srcIs = new FileInputStream(src);
        FileInputStream destIs = new FileInputStream(dest);

        // 通过 MD5 校验文件的完整性
        String srcMD5 = DigestUtils.md5Hex(srcIs);
        String destMD5 = DigestUtils.md5Hex(destIs);
        Assertions.assertEquals(srcMD5, destMD5, "MD5 校验失败");

        is.close();
        os.close();
        srcIs.close();
        destIs.close();

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
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        // 删除文件
        minioClient.removeObject(args);
    }

}
