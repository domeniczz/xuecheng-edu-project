package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;

/**
 * @author Domenic
 * @Classname MinioTest
 * @Description 测试 minio 的 SDK
 * @Created by Domenic
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MinioTest {

    @Autowired
    MinioClient minioClient;

    private static String bucketName;
    private static String localFileName;
    private static String testDownloadFileName;
    private static String objectName;

    @BeforeAll
    static void init() {
        // minio 中的桶名称
        bucketName = "testbucket" + (int) Math.floor(Math.random() * (100 + 1));
        // 文件在本地的路径
        localFileName = "D:\\Download\\Laplace Transforms.pdf";
        // 文件下载后在本地的保存路径
        testDownloadFileName = "D:\\Download\\Laplace Transforms TestDownload.pdf";
        // 文件在 minio 中的保存路径
        objectName = "test/Laplace Transforms.pdf";
    }

    /**
    * 创建一个指定名称的 bucket
    */
    @Test
    @Order(1)
    public void test_createBucket() throws Exception {
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
    public void test_setBucketAccessPolicyPublic() throws Exception {
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
    public void test_deleteBucket() throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        System.out.println(" ========== Bucket \"" + bucketName + "\" Deleted ========== ");
    }

    /**
     * 上传文件到 bucket
     */
    @Test
    @Order(3)
    public void test_uploadFile() throws Exception {
        // 通过扩展名得到媒体资源类型 mimeType
        // 根据扩展名取出 mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        // 通用 mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }

        // 上传文件的参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                // 桶
                .bucket(bucketName)
                // 指定本地文件 (路径)
                .filename(localFileName)
                // 对象名 (路径)
                .object(objectName)
                // 设置媒体文件类型
                .contentType(mimeType)
                .build();

        // 上传文件
        ObjectWriteResponse resp = minioClient.uploadObject(uploadObjectArgs);
        System.out.println(resp);
    }

    /**
     * 向 bucket 查询文件，并下载
     */
    @Test
    @Order(4)
    public void test_downloadFile() throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        File src = new File(localFileName);
        File dest = new File(testDownloadFileName);

        // 查询远程服务获取到一个流对象
        FilterInputStream is = minioClient.getObject(getObjectArgs);

        // 指定输出流
        FileOutputStream os = new FileOutputStream(dest);
        IOUtils.copy(is, os);

        FileInputStream srcIs = new FileInputStream(src);
        FileInputStream destIs = new FileInputStream(dest);

        // 通过 MD5 校验文件的完整性
        String srcMD5 = DigestUtils.md5Hex(srcIs);
        String destMD5 = DigestUtils.md5Hex(destIs);
        if (srcMD5.equals(destMD5)) {
            System.out.println(" ========== File Downloaded! ========== ");
        } else {
            System.out.println(" ========== MD5 Check Failed! ========== ");
        }

        boolean res = dest.delete();
        if (res) {
            System.out.println(" ========== Download File Deleted ==========");
        }
    }

    /**
     * 从 bucket 删除文件
     */
    @Test
    @Order(5)
    public void test_deleteFile() throws Exception {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        // 删除文件
        minioClient.removeObject(removeObjectArgs);
    }

}
