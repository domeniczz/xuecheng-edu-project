package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Domenic
 * @Classname MediaProcessMapper
 * @Description MediaProcess Mapper 接口
 * @Created by Domenic
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
     * 获取待处理任务列表
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count 获取的任务数
     * @return 待处理任务列表 {@link List}&lt;{@link MediaProcess}&gt;
     */
    List<MediaProcess> selectMediaProcessList(@Param("shardIndex") int shardIndex, @Param("shardTotal") int shardTotal, @Param("count") int count);

    /**
     * 执行任务一次，更新任务数据
     * @param processId 任务 ID
     * @return 更新记录数
     */
    int startProcessOnce(@Param("processId") long processId);

}
