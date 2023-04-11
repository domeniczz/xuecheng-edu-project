package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * 使用递归查询课程分类<br/>
     * 在数据库中，课程分类的数据是以树形结构组织起来的<br/>
     * 因此采用递归，查询时需提供根节点的 id 值
     * @param rootCourseID 根课程的 id 值
     * @return 未经处理的 CourseCategoryTreeDto List，没有将子节点封装进父节点中
     */
    public List<CourseCategoryTreeDto> selectTreeNodes(String rootCourseID);

}
