package com.xuecheng.content.service;

import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author Domenic
 * @Classname TeachplanService
 * @Description 课程计划服务接口
 * @Created by Domenic
 */
public interface TeachplanService {

    /**
     * 获取课程计划的树形结构
     * @param courseId 根节点课程 id
     * @return TeachplanDto List
     */
    public List<TeachplanDto> queryTreeNodes(long courseId);

    /**
     * 添加/修改课程计划
     * @param saveTeachplanDto 保存课程计划 dto
     * @return 添加结果
     */
    public ResponseResult saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * <p>
     * 删除课程计划和媒资关联信息
     * 1、删除大章节，大章节下有小章节时不允许删除
     * 2、删除大章节，大章节下没有小章节时可以正常删除
     * 3、删除小章节，同时将关联的信息进行删除
     * </p>
     * @param id id
     * @return 删除结果，若有错误则抛出 XueChengException
     */
    ResponseResult deleteTeachplan(long id);

    /**
     * <p>
     * 删除指定课程下的所有章节(包括大章节和小章节)和媒资关联信息
     * 谨慎使用，一般在删除课程时调用
     * 因为课程关联的章节可以为空，所有方法不会因为 delete 语句影响行数为 0 而抛出异常
     * </p>
     * @param courseId 课程 id
     * @return 删除结果
     */
    ResponseResult deleteAll(long courseId);

    /**
     * 课程计划上移
     * @param id 课程计划 id
     * @return 上移结果
     */
    public ResponseResult moveUp(long id);

    /**
     * 课程计划下移
     * @param id 课程计划 id
     * @return 下移结果
     */
    public ResponseResult moveDown(long id);

}
