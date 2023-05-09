package com.xuecheng.media.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.FileParamsDto;

/**
 * @author Domenic
 * @Classname BigFilesService
 * @Description 大文件处理接口 (大文件，分片上传)
 * @Created by Domenic
 */
public interface BigFilesService {

    /**
     * <p>
     * 检查文件是否已经存在 (数据库和 minio 中都存在)<br/>
     * 若在数据库中存在文件信息，而在 minio 中不存在，则删除数据库中的异常文件信息，返回文件不存在
     * </p>
     * @param fileMd5 文件的 MD5
     * @return {@link RestResponse}&lt;{@link Boolean}&gt;，{@code false} 不存在，{@code true} 存在
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 检查 minio 中是否存在分块文件
     * @param fileMd5 源文件的 MD5
     * @param chunkIndex 分块文件的序号
     * @return {@link RestResponse}&lt;{@link Boolean}&gt;，{@code false} 不存在，{@code true} 存在
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传分块
     * @param fileMd5 文件 MD5
     * @param chunkIndex 分块文件的序号
     * @param localChunkFilePath 分块文件本地路径
     * @return {@link RestResponse}&lt;{@link Boolean}&gt;，{@code false} 上传失败，{@code true} 上传成功
     */
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chunkIndex, String localChunkFilePath);

    /**
     * <p>
     * 合并分块<br/>
     * 并将文件信息保存到数据库中<br/>
     * 并删除 minio 中的分块文件<br/>
     * 若合并后校验出错，则删除校验失败的合并后文件
     * </p>
     * @param companyId 机构 ID
     * @param fileMd5 文件 MD5
     * @param chunkTotalNum 分块文件数量
     * @param fileParamsDto 文件请求信息
     * @return {@link RestResponse}&lt;{@link Boolean}&gt;，{@code false} 合并失败，{@code true} 合并成功
     */
    public RestResponse<Boolean> mergeChunksAndSave(Long companyId, String fileMd5, int chunkTotalNum, FileParamsDto fileParamsDto);

    /**
     * 删除 minio 中的文件
     * @param objectName 对象名 (文件的路径)
     * @return {@link RestResponse}&lt;{@link Boolean}&gt; {@code false} 删除失败，{@code true} 删除成功
     */
    public RestResponse<Boolean> deleteFile(String objectName);

}
