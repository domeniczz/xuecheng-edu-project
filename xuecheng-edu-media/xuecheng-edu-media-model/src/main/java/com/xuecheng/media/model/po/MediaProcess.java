package com.xuecheng.media.model.po;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname MediaProcess
 * @Description MediaProcess 对象
 * @Created by Domenic
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("media_process")
public class MediaProcess extends MediaProcessHistory {

    private static final long serialVersionUID = 1L;

}