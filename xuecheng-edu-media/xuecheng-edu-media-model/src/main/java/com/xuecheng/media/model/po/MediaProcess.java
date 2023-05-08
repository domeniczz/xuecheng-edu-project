package com.xuecheng.media.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Domenic
 * @Classname MediaProcess
 * @Description MediaProcess 对象
 * @Created by Domenic
 */
@Data
@ToString
@TableName("media_process")
public class MediaProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <!-- 不要改为基本类型，维持包装类 Long -->
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件标识
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 存储源
     */
    private String bucket;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 状态：1.未处理，2.视频处理完成更新
     */
    private String status;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 完成时间
     */
    private LocalDateTime finishDate;

    /**
     * 媒资文件访问地址
     */
    private String url;

    /**
     * 失败原因
     */
    private String errormsg;

    /**
     * <p>
     * 失败次数
     * </p>
     * <!-- 不要改为基本类型，维持包装类 Integer -->
     */
    private Integer failCount;

}
