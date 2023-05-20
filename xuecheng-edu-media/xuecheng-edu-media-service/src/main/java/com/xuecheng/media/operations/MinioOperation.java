package com.xuecheng.media.operations;

import com.xuecheng.base.exception.XueChengEduException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname MinioOperation
 * @Description Minio 操作的工具类
 * @Created by Domenic
 */
@Component
@Slf4j
public class MinioOperation {

    @Autowired
    private MinioClient minioClient;

    /**
     * 创建桶
     * @param bucketName 桶名
     * @throws IOException IO 异常
     * @throws MinioException Minio 异常
     * @throws GeneralSecurityException 加解密异常
     */
    public void createBucket(String bucketName) throws IOException, MinioException, GeneralSecurityException {
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
     * @throws IOException IO 异常
     * @throws MinioException Minio 异常
     * @throws GeneralSecurityException 加解密异常
     */
    public void deleteBucket(String bucketName) throws IOException, MinioException, GeneralSecurityException {
        RemoveBucketArgs args = RemoveBucketArgs.builder()
                .bucket(bucketName)
                .build();

        // 删除桶
        minioClient.removeBucket(args);
    }

    /**
     * 查询文件
     * @param bucket 桶名
     * @param filePath 文件的路径
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
            return null;
        }
    }

    /**
     * 上传 媒体/视频 文件
     * @param localFilePath 文件本地路径
     * @param mimeType 媒体类型
     * @param bucket 桶名
     * @param objectName 对象名 (文件的路径)
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
            XueChengEduException.cast("上传文件到 minio 出错，未知错误!");
            return null;
        }
    }

    /**
     * 下载文件，保存为临时文件
     * @param bucket 桶名
     * @param objectName 对象名 (文件的路径)
     * @return 下载后的文件 {@link File}
     */
    public File downloadFile(String bucket, String objectName) {

        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();

        try (InputStream downloadstream = minioClient.getObject(args)) {
            // 创建临时文件，示例命名：down-minio-6c2293.mp4
            File file = File.createTempFile("down-minio-" + FileOperation.getUuid(), objectName.substring(objectName.lastIndexOf(".")));

            try (FileOutputStream out = new FileOutputStream(file)) {
                IOUtils.copy(downloadstream, out);
            }

            log.debug("从 minio 下载文件成功, bucket={}, objectName={}, saveDir={}", bucket, objectName, System.getProperty("java.io.tmpdir"));

            return file;
        } catch (Exception e) {
            log.error("从 minio 下载文件出错, bucket={}, objectName={}", bucket, objectName);
        }
        return null;
    }

    /**
     * 下载文件的部分内容
     * @param bucket 桶名
     * @param objectName 对象名 (文件的路径)
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
            File file = File.createTempFile("down-part-minio-" + FileOperation.getUuid(), objectName.substring(objectName.lastIndexOf(".")));

            try (FileOutputStream out = new FileOutputStream(file)) {
                IOUtils.copy(downloadstream, out);
            }

            log.debug("从 minio 下载部分的文件成功, downloadSize={}, bucket={}, objectName={}, saveDir={}",
                    file.length(), bucket, objectName, System.getProperty("java.io.tmpdir"));

            return file;
        } catch (Exception e) {
            log.error("从 minio 下载部分发文件出错, bucket={}, objectName={}", bucket, objectName);
        }
        return null;
    }

    /**
     * 删除 媒体/视频 文件
     * @param bucket 桶名
     * @param objectName 对象名 (文件的路径)
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
            XueChengEduException.cast("删除文件失败");
            return false;
        }
    }

    /**
     * <p>
     * 合并分块<br/>
     * 注意：minio 默认的分块文件大小为 5MB，且分块文件大小不能小于 5MB
     * </p>
     * @param bucketName 桶名
     * @param objectName 对象名 (合并后的文件的路径)
     * @param chunkFolderPath 分块文件的路径
     * @return {@link ObjectWriteResponse} 合并操作的响应
     */
    public ObjectWriteResponse mergeChunks(String bucketName, String objectName, String chunkFolderPath) {
        try {
            List<ComposeSource> sources = new ArrayList<>();

            Iterable<Result<Item>> listChunks = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(chunkFolderPath)
                    // 非递归
                    .recursive(false)
                    .build());

            for (Result<Item> result : listChunks) {
                sources.add(ComposeSource.builder()
                        .bucket(bucketName)
                        .object(result.get().objectName())
                        .build());
            }

            ComposeObjectArgs args = ComposeObjectArgs.builder()
                    .bucket(bucketName)
                    // 合并后的文件的 objectname
                    .object(objectName)
                    // 指定源文件
                    .sources(sources)
                    .build();

            ObjectWriteResponse resp = minioClient.composeObject(args);
            log.debug("合并文件成功, bucket={}, objectName={}", bucketName, objectName);

            return resp;
        } catch (Exception e) {
            log.error("合并文件出错, bucket={}, objectName={}, errorMsg={}", bucketName, objectName, e.getMessage());
            return null;
        }
    }

    /**
     * (非递归地) 清除指定文件夹下的所有文件
     * @param bucketName 桶名
     * @param folderPath 文件夹路径
     * @return {@link Boolean} {@code true} 成功, {@code false} 失败
     */
    public boolean clearFolderNonRecursively(String bucketName, String folderPath) {
        try {
            // 获取文件夹下的所有文件
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(folderPath)
                    // 非递归
                    .recursive(false)
                    .build());

            // 遍历所有文件
            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(item.objectName())
                        .build());
            }

            log.debug("非递归地清除文件夹 ({}) 成功, bucket={}", folderPath, bucketName);
            return true;
        } catch (Exception e) {
            log.error("非递归地清除文件夹 ({}) 出错, bucket={}, errorMsg={}", folderPath, bucketName, e.getMessage());
        }

        return false;
    }

    /**
    * (递归地) 清除文件夹下的所有文件
    * @param bucketName 桶名
    * @param folderPath 文件夹路径
    * @return {@link Boolean} {@code true} 成功, {@code false} 失败
    */
    public boolean clearFolderRecursively(String bucketName, String folderPath) {
        try {
            // 获取文件夹下的所有文件
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(folderPath)
                    // 递归
                    .recursive(true)
                    .build());

            // 封装删除文件的参数对象 RemoveObjectArgs 到 List 集合中
            List<RemoveObjectArgs> objectsToDelete = new LinkedList<>();
            // 遍历所有文件
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
                }
                log.debug("递归地清除文件夹 ({}) 成功, bucket={}", folderPath, bucketName);
                return true;
            }
        } catch (Exception e) {
            log.error("递归地清除文件夹 ({}) 出错, bucket={}, errorMsg={}", folderPath, bucketName, e.getMessage());
        }
        return false;
    }

    /**
     * <p>
     * 清理桶中残留的分块文件 (用于定时清理任务)<br/>
     * 只清理存在时间超过 24 小时的分块文件
     * </p>
     * @param bucketName 桶名称
     */
    public void clearResidualChunkFiles(String bucketName) {
        try {
            // 获取所有文件
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    // 递归
                    .recursive(true)
                    .build());

            LocalDateTime now = LocalDateTime.now();
            String lastMd5Path = "";
            // 记录被清理的，分块文件数量
            int leftoverChunkFileCount = 0;
            // 记录被清理的，分块文件夹数量
            int leftoverChunkFolderCount = 0;
            // 记录同一个分块文件夹中的文件，最近的一次修改时间 (初始化最早的时间: -999999999-01-01T00:00:00)
            LocalDateTime latestLastModified = LocalDateTime.MIN;

            // 遍历所有文件，查找残留的分块文件
            for (Result<Item> result : results) {
                Item item = result.get();
                String itemObjectName = item.objectName();
                String[] pathParts = itemObjectName.split("/");

                // 因为循环的 item 是文件，因此 length - 2 获取的就是该文件所在的文件夹名
                // 若文件夹名为 "chunk"，则表示该文件是分块文件
                String folderName = pathParts[pathParts.length - 2];
                // 从文件路径中获取分块文件的源文件的 MD5 值
                String currentMd5Path = pathParts[pathParts.length - 3];
                if (pathParts.length > 1 && "chunk".equals(folderName)) {
                    // 判断是否切换到了另一个源文件的残留分块文件夹
                    if (!Objects.equals(currentMd5Path, lastMd5Path)) {
                        lastMd5Path = currentMd5Path;
                        leftoverChunkFolderCount++;
                        // 重置为最早的时间
                        latestLastModified = LocalDateTime.MIN;
                    }

                    LocalDateTime lastModified = item.lastModified().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    latestLastModified = lastModified.isAfter(latestLastModified) ? lastModified : latestLastModified;

                    // 若最后修改日期比当前时间早 24 小时，则对分块文件进行删除
                    if (latestLastModified.isBefore(now.minusHours(24))) {
                        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(itemObjectName).build());
                        leftoverChunkFileCount++;
                        log.debug("删除残留的分块文件: " + itemObjectName);
                    }
                }
            }

            log.debug("总共清理了 {} 个残留的分块文件夹, 删除了 {} 个残留的分块文件", leftoverChunkFolderCount, leftoverChunkFileCount);
        } catch (MinioException e) {
            log.error("删除残留的 chunk 文件出错, bucket={}, errorMsg={}\nhttpTrace={}", bucketName, e.getMessage(), e.httpTrace());
        } catch (Exception e) {
            log.error("删除残留的 chunk 文件出错, bucket={}, errorMsg={}", bucketName, e.getMessage());
        }
    }

    /**
     * 获取文件信息
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
                log.error("文件不存在, bucket={}, objectName={}, errorMsg={}", bucketName, objectName, e.getMessage());
                return null;
            }
            // 访问被拒绝
            else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                log.error("访问文件被拒绝, bucket={}, objectName={}, errorMsg={}", bucketName, objectName, e.getMessage());
                throw e;
            } else {
                throw e;
            }
        }
    }

    /**
     * <p>
     * 获取文件 ETag 值<br/>
     * 文件的 etag 值和本地计算的文件 MD5 值可能不一样
     * </p>
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_Object.html">AWS S3 Object Doc</a>
     * @param bucketName 桶名
     * @param objectName 对象名 (文件的路径)
     * @return MD5 字符串
     * @throws Exception 异常 (除了 {@link MinioException} 异常)
     */
    public String getFileEtag(String bucketName, String objectName) throws Exception {
        try {
            // 返回 MD5 (minio 中的 etag 属性就是 MD5)
            return getFileInfo(bucketName, objectName).etag();
        } catch (MinioException e) {
            log.error("获取文件 MD5 出错, bucket={}, objectName={}, errorMsg={}\nhttpTrace={}", bucketName, objectName, e.getMessage(), e.httpTrace());
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
    public Optional<Long> getFileSize(String bucketName, String objectName) {
        try {
            long size = getFileInfo(bucketName, objectName).size();
            return Optional.of(size);
        } catch (MinioException e) {
            log.error("获取文件大小出错, bucket={}, objectName={}, errorMsg={}\nhttpTrace={}", bucketName, objectName, e.getMessage(), e.httpTrace());
        } catch (Exception e) {
            log.error("获取文件大小出错, bucket={}, objectName={}, errorMsg={}", bucketName, objectName, e.getMessage());
        }
        return Optional.empty();
    }

}
