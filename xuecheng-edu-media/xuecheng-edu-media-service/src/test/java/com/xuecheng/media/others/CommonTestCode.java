package com.xuecheng.media.others;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;

/**
 * @author Domenic
 * @Classname CommonTestCode
 * @Description MinioTest 和 FileChunkMergeTest 中的公共代码
 * @Created by Domenic
 */
@Component
public class CommonTestCode {

    @Autowired
    MinioClient minioClient;

    public ObjectWriteResponse uploadFileToMinio(String bucketName, String localFilePath, String objectName) throws Exception {
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
        return minioClient.uploadObject(args);
    }

    public void createBucket(String bucketName) throws Exception {
        BucketExistsArgs args = BucketExistsArgs.builder()
                .bucket(bucketName)
                .build();

        // 检查桶是否存在
        boolean exist = minioClient.bucketExists(args);
        if (!exist) {
            // 创建桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    public void deleteBucket(String bucketName) throws Exception {
        RemoveBucketArgs args = RemoveBucketArgs.builder()
                .bucket(bucketName)
                .build();

        // 删除桶
        minioClient.removeBucket(args);
    }

    public void deleteFileFromMinio(String bucketName, String objectName) throws Exception {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        // 删除对象
        minioClient.removeObject(args);
    }

}
