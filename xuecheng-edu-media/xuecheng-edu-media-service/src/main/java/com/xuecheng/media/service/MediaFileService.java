package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.FileParamsDto;
import com.xuecheng.media.model.dto.FileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFile;

/**
 * @author Domenic
 * @Classname MediaFileService
 * @Description 媒资文件管理业务类 (小文件，不分片上传)
 * @Created by Domenic
 */
public interface MediaFileService {

    /**
     * 媒资文件查询方法
     * @param companyId 机构 ID
     * @param pageParams 分页参数
     * @param queryMediaParamsDto 查询条件
     * @return {@link PageResult}&lt;{@link MediaFile}&gt;
    */
    public PageResult<MediaFile> queryMediaFileList(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传媒体文件
     * @param companyId 机构 ID
     * @param fileParamsDto 文件操作 (上传) 参数 DTO
     * @param tempFilePath 本地临时文件的路径 (上传的文件先临时存储到本地，再上传到文件服务器)
     * @return {@link FileResultDto} 文件上传结果 DTO
     */
    public FileResultDto uploadMediaFile(Long companyId, FileParamsDto fileParamsDto, String tempFilePath);

    /**
     * <p>
     * 删除媒体文件<br/>
     * 注意：删除数据库中对应的文件信息时，不是根据文件 ID (MD5 值) 删除，而是根据文件其他元信息删除
     * </p>
     * @param companyId 机构 ID
     * @param fileParamsDto 文件操作 (删除) 参数 DTO
     * @return {@link FileResultDto} 文件删除结果 DTO
     */
    public FileResultDto deleteMediaFile(long companyId, FileParamsDto fileParamsDto);

}