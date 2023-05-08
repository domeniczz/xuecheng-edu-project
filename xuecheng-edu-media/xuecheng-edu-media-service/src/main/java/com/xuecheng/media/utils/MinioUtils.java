package com.xuecheng.media.utils;

import com.xuecheng.base.exception.XueChengEduException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.minio.BucketExistsArgs;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname MinioUtils
 * @Description MinioTest 和 FileChunkMergeTest 中的公共代码
 * @Created by Domenic
 */
@Component
@Slf4j
public class MinioUtils {

    @Autowired
    private MinioClient minioClient;

    /**
     * 创建桶
     * @param bucketName 桶名
     * @throws Exception 异常
     */
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

    /**
     * 删除桶
     * @param bucketName 桶名
     * @throws Exception 异常
     */
    public void deleteBucket(String bucketName) throws Exception {
        RemoveBucketArgs args = RemoveBucketArgs.builder()
                .bucket(bucketName)
                .build();

        // 删除桶
        minioClient.removeBucket(args);
    }

    /**
     * 向 minio 查询文件
     * @param bucket 桶名
     * @param filePath 文件在 minio 中的路径
     * @return 输入流 {@link FilterInputStream}
     */
    public FilterInputStream queryFile(String bucket, String filePath) {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucket)
                .object(filePath)
                .build();

        // 查询远程服务获取到一个流对象
        try {
            return minioClient.getObject(args);
        } catch (Exception e) {
            log.error("向 minio 查询文件出错 (" + filePath + ")");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将 媒体/视频 文件上传到 minio
     * @param localFilePath 文件本地路径
     * @param mimeType 媒体类型
     * @param bucket 桶名
     * @param objectName 对象名 (文件在 minio 中的路径)
     * @return {@link ObjectWriteResponse}
     */
    public ObjectWriteResponse uploadFile(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs args = UploadObjectArgs.builder()
                    // 桶
                    .bucket(bucket)
                    // 指定本地文件路径
                    .filename(localFilePath)
                    // 对象名 放在子目录下
                    .object(objectName)
                    // 设置媒体文件类型
                    .contentType(mimeType)
                    .build();

            // 上传文件
            ObjectWriteResponse res = minioClient.uploadObject(args);
            log.debug("上传文件到 minio 成功, bucket={}, objectName={}", bucket, objectName);
            return res;
        } catch (Exception e) {
            log.error("上传文件到 minio 出错, bucket={}, objectName={}", bucket, objectName);
            e.printStackTrace();
            XueChengEduException.cast("上传文件到 minio 出错，未知错误!");
            return null;
        }
    }

    /**
     * <p>
     * 从 minio 下载文件，保存为临时文件
     * </p>
     * @param bucket 桶名
     * @param objectName 对象名 (文件在 minio 中的路径)
     * @return 下载后的文件 {@link File}
     */
    public File downloadFile(String bucket, String objectName) {

        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();

        try (InputStream downloadstream = minioClient.getObject(args)) {
            // 创建临时文件，示例命名：down-minio-6c2293.mp4
            File file = File.createTempFile("down-minio-" + FileUtils.getUuid(), objectName.substring(objectName.lastIndexOf(".")));
            FileOutputStream outputStream = new FileOutputStream(file);

            IOUtils.copy(downloadstream, outputStream);
            log.debug("从 minio 下载文件成功, bucket={}, objectName={}, saveDir={}", bucket, objectName, System.getProperty("java.io.tmpdir"));

            return file;
        } catch (Exception e) {
            log.error("从 minio 下载文件出错, bucket={}, objectName={}", bucket, objectName);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件的部分内容
     * @param bucket 桶名
     * @param objectName 对象名 (文件在 minio 中的路径)
     * @param offset 偏移量 (下载文件的多少量)
     * @param length 下载的长度
     * @return 下载的文件内容 {@link File}
     */
    public File downloadFileParts(String bucket, String objectName, long offset, long length) {

        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .offset(offset)
                .length(length)
                .build();

        try (InputStream downloadstream = minioClient.getObject(args)) {
            // 创建临时文件，示例命名：down-part-minio-6c2293.mp4
            File file = File.createTempFile("down-part-minio-" + FileUtils.getUuid(), objectName.substring(objectName.lastIndexOf(".")));
            FileOutputStream outputStream = new FileOutputStream(file);

            IOUtils.copy(downloadstream, outputStream);
            log.debug("从 minio 下载部分的文件成功, downloadSize={}, bucket={}, objectName={}, saveDir={}",
                    file.length(), bucket, objectName, System.getProperty("java.io.tmpdir"));

            return file;
        } catch (Exception e) {
            log.error("从 minio 下载部分发文件出错, bucket={}, objectName={}", bucket, objectName);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将 媒体/视频 文件从 minio 中删除
     * @param bucket 桶名
     * @param objectName 对象名 (文件在 minio 中的路径)
     */
    public boolean deleteFile(String bucket, String objectName) {

        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();

        // 删除文件
        try {
            minioClient.removeObject(args);
            log.debug("从 minio 删除文件成功, bucket={}, objectName={}", bucket, objectName);
            return true;
        } catch (Exception e) {
            log.error("从 minio 删除文件出错, bucket={}, objectName={}, errorMsg={}", bucket, objectName, e.getMessage());
            e.printStackTrace();
            XueChengEduException.cast("删除文件失败");
            return false;
        }
    }

    /**
     * <p>
     * 在 Minio 中合并分块<br/>
     * 注意：minio 默认的分块文件大小为 5MB，且分块文件大小不能小于 5MB
     * </p>
     * @param bucketName 桶名
     * @param objectName 对象名 (合并后的文件的路径)
     * @param sources {@link List}&lt;{@link ComposeSource}&gt; 源文件列表 (分块文件)
     * @return {@link ObjectWriteResponse}
     */
    public ObjectWriteResponse mergeChunks(String bucketName, String objectName, List<ComposeSource> sources) {

        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket(bucketName)
                // 合并后的文件的 objectname
                .object(objectName)
                // 指定源文件
                .sources(sources)
                .build();

        // 报错 size 1048576 must be greater than 5242880，minio 默认的分块文件大小为 5M
        try {
            return minioClient.composeObject(composeObjectArgs);
        } catch (Exception e) {
            log.error("合并文件出错, bucket={}, objectName={}, errorMsg={}", bucketName, objectName, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 清除分块文件 (要求分块文件的命名是序号 0,1,2...)
     * @param chunkFileFolderPath 分块文件夹路径
     * @param chunkTotalNum 分块文件总数
     */
    public boolean clearChunkFiles(String bucketName, String chunkFileFolderPath, int chunkTotalNum) {
        Iterable<DeleteObject> objectsToDelete = Stream.iterate(0, i -> ++i).limit(chunkTotalNum)
                .map(i -> new DeleteObject(chunkFileFolderPath + i))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                .bucket(bucketName)
                .objects(objectsToDelete)
                .build();

        Iterable<Result<DeleteError>> results = null;
        try {
            results = minioClient.removeObjects(removeObjectsArgs);
        } catch (Exception e) {
            log.error("清除分块文件出错, bucket={}, objectName={}, errorMsg={}", bucketName, chunkFileFolderPath, e.getMessage());
            e.printStackTrace();
            return false;
        }

        // 遍历文件删除的结果，并检查在此过程中是否出现任何错误
        for (Result<DeleteError> res : results) {
            try {
                res.get();
            } catch (Exception e) {
                String failedObjectName = "";
                if (e instanceof ErrorResponseException) {
                    failedObjectName = ((ErrorResponseException) e).errorResponse().objectName();
                }
                log.error("清除分块文件出错, bucket={}, objectName={}, errorMsg={}", bucketName, failedObjectName, e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * 递归地删除文件夹
     * @param bucketName 桶名
     * @param folderName 文件夹路径
     * @return {@link Boolean} {@code true} 成功, {@code false} 失败
     */
    public boolean deleteFolderRecursively(String bucketName, String folderName) {
        try {
            List<RemoveObjectArgs> objectsToDelete = new LinkedList<>();

            // 获取文件夹下的所有文件
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(folderName)
                    .recursive(true)
                    .build());

            // 封装删除文件的参数对象 RemoveObjectArgs
            for (Result<Item> result : results) {
                Item item = result.get();
                objectsToDelete.add(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(item.objectName())
                        .build());
            }

            if (!objectsToDelete.isEmpty()) {
                // 遍历所有文件，逐一删除
                for (RemoveObjectArgs o : objectsToDelete) {
                    minioClient.removeObject(o);
                    try {
                        minioClient.removeObject(o);
                    } catch (Exception e) {
                        log.error("从 Minio 删除文件 ({}) 失败, bucket={}, folder={}, errorMsg={}", o.object(), bucketName, folderName, e.getMessage());
                        e.printStackTrace();
                    }
                }
                log.debug("从 minio 删除文件夹成功, bucket={}, folder={}", bucketName, folderName);
                return true;
            }
        } catch (Exception e) {
            log.error("从 minio 删除文件夹出错, bucket={}, folder={}, errorMsg={}", bucketName, folderName, e.getMessage());
        }
        return false;
    }

    /**
     * 获取文件 MD5 (其实就是 minio 中文件的 etag 值)
     * @param bucketName 桶名
     * @param objectName 对象名 (文件的路径)
     * @return MD5 字符串
     * @throws Exception 异常 (除了 {@link MinioException} 异常)
     */
    public String getFileMd5(String bucketName, String objectName) throws Exception {
        try {
            // 返回 MD5 (minio 中的 etag 属性就是 MD5)
            return getFileInfo(bucketName, objectName).etag();
        } catch (MinioException e) {
            log.error("获取文件 MD5 出错, bucket={}, objectName={}, errorMsg={}\nhttpTrace={}", bucketName, objectName, e.getMessage(), e.httpTrace());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件大小
     * @param bucketName 桶名
     * @param objectName 对象名 (文件的路径)
     * @return 文件大小
     * @throws Exception 异常 (除了 {@link MinioException} 异常)
     */
    public long getFileSize(String bucketName, String objectName) throws Exception {
        try {
            return getFileInfo(bucketName, objectName).size();
        } catch (MinioException e) {
            log.error("获取文件大小出错, bucket={}, objectName={}, errorMsg={}\nhttpTrace={}", bucketName, objectName, e.getMessage(), e.httpTrace());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 从 minio 获取文件的信息
     * @param bucketName 桶名
     * @param objectName 对象名 (文件的路径)
     * @return {@link StatObjectResponse}
     * @throws Exception 异常
     */
    public StatObjectResponse getFileInfo(String bucketName, String objectName) throws Exception {
        try {
            StatObjectArgs args = StatObjectArgs.builder().bucket(bucketName).object(objectName).build();
            // 从 minio 中获取文件信息
            return minioClient.statObject(args);
        } catch (MinioException e) {
            int statusCode = -1;
            if (e instanceof ErrorResponseException) {
                // 获取响应状态码
                statusCode = ((ErrorResponseException) e).response().code();
            }
            // 文件不存在
            if (statusCode == HttpStatus.SC_NOT_FOUND) {
                log.error("获取文件信息出错, 文件不存在, bucket={}, objectName={}, errorMsg={}", bucketName, objectName, e.getMessage());
                return null;
            }
            // 访问被拒绝
            else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                log.error("获取文件信息出错, 访问被拒绝, bucket={}, objectName={}, errorMsg={}", bucketName, objectName, e.getMessage());
                throw e;
            } else {
                throw e;
            }
        }
    }

}
