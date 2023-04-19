package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.UpdateCourseDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseCategoryService;
import com.xuecheng.content.service.CourseMarketService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Domenic
 * @Classname CourseBaseServiceImpl
 * @Description 课程信息服务实现类
 * @Created by Domenic
 */
@Slf4j
@Service
@Transactional
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Autowired
    private CourseMarketService courseMarketService;

    @Autowired
    private TeachplanService teachplanService;

    @Autowired
    private CourseTeacherService courseTeacherService;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto dto) {
        // 创建查询接口
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        // 拼接查询条件
        // 根据课程名称 模糊查询 name like '%名称%'
        queryWrapper.like(
                StringUtils.isNotEmpty(dto.getCourseName()),
                CourseBase::getName,
                dto.getCourseName());
        // 根据课程审核状态 精确查询
        queryWrapper.eq(
                StringUtils.isNotEmpty(dto.getAuditStatus()),
                CourseBase::getAuditStatus,
                dto.getAuditStatus());
        // 根据课程发布状态 精确查询
        queryWrapper.eq(
                StringUtils.isNotEmpty(dto.getPublishStatus()),
                CourseBase::getStatus,
                dto.getPublishStatus());

        // 分页参数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        // 分页查询E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        // 数据
        List<CourseBase> items = pageResult.getRecords();
        // 总记录数
        long total = pageResult.getTotal();

        // 准备返回数据 List<T> items, long counts, long page, long pageSize
        return new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    /**
     * 根据课程 id 查询课程基本信息和课程营销信息
     * @param courseId 课程 id
     * @return 课程基本信息和课程营销信息 DTO
     */
    @Override
    public CourseBaseInfoDto queryCourseBaseAndMarketInfoById(long courseId) {
        // 查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengEduException.cast("课程不存在");
            return null;
        }

        // 查询课程营销信息
        CourseMarket courseMarket = courseMarketService.query(courseId);
        if (courseMarket == null) {
            XueChengEduException.cast("课程营销信息不存在");
            return null;
        }

        // 封装返回数据
        CourseBaseInfoDto dto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, dto);
        BeanUtils.copyProperties(courseMarket, dto);

        // 通过 courseCategoryMapper 查询分类信息，将分类名称放在 courseBaseInfoDto 对象
        dto.setMtName(courseCategoryService.query(courseBase.getMt()).getName());
        dto.setStName(courseCategoryService.query(courseBase.getSt()).getName());

        return dto;
    }

    @Override
    public CourseBaseInfoDto create(long companyId, AddCourseDto dto) {
        // 参数合法性校验通过 JSR Validation 进行
        // Spring 支持 Hibernate Validator 校验框架

        /* ---- 写入课程基本信息 ---- */
        CourseBase courseBase = new CourseBase();

        BeanUtils.copyProperties(dto, courseBase);

        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        // 审核状态默认为未提交
        courseBase.setAuditStatus("202002");
        // 发布状态为未发布
        courseBase.setStatus("203001");

        int baseRes = courseBaseMapper.insert(courseBase);
        if (baseRes <= 0) {
            log.error(String.format("创建课程 (name: %s) 失败", courseBase.getName()));
            XueChengEduException.cast("创建课程失败");
            return null;
        }

        /* ---- 写入课程营销信息 ---- */
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);

        Long courseId = courseBase.getId();
        // 设置课程 id（主键）
        courseMarket.setId(courseId);
        // 创建营销信息
        CourseMarket marketRes = courseMarketService.saveCourseMarket(courseMarket);
        if (marketRes == null) {
            log.error(String.format("保存课程 (name: %s) 营销信息失败", courseBase.getName()));
            XueChengEduException.cast("保存课程营销信息失败");
            return null;
        }

        CourseBaseInfoDto resDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, resDto);
        BeanUtils.copyProperties(courseMarket, resDto);
        return resDto;
    }

    @Override
    public CourseBaseInfoDto update(long companyId, UpdateCourseDto dto) {
        // 参数合法性校验通过 JSR Validation 进行
        // Spring 支持 Hibernate Validator 校验框架

        long courseId = dto.getId();

        // 检查课程是否存在
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengEduException.cast("课程不存在，请先创建课程");
            return null;
        }

        // 操作权限合法性校验
        if (companyId != courseBase.getCompanyId()) {
            XueChengEduException.cast("只能修改本机构的课程");
            return null;
        }

        /* ---- 写入课程基本信息 ---- */
        BeanUtils.copyProperties(dto, courseBase);

        courseBase.setChangeDate(LocalDateTime.now());

        int baseRes = courseBaseMapper.updateById(courseBase);
        if (baseRes <= 0) {
            log.error(String.format("修改课程 (name: %s) 失败", courseBase.getName()));
            XueChengEduException.cast("修改课程失败");
            return null;
        }

        /* ---- 写入课程营销信息 ---- */
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);

        // 设置课程 id（主键）
        courseMarket.setId(courseId);
        // 更新营销信息
        CourseMarket marketRes = courseMarketService.saveCourseMarket(courseMarket);
        if (marketRes == null) {
            log.error(String.format("更新课程 (name: %s) 营销信息失败", courseBase.getName()));
            XueChengEduException.cast("更新课程营销信息失败");
            return null;
        }

        CourseBaseInfoDto resDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, resDto);
        BeanUtils.copyProperties(courseMarket, resDto);
        return resDto;
    }

    @Override
    public ResponseResult delete(long courseId) {
        boolean ifSubmitForAudit = checkAuditState(courseId);
        if (!ifSubmitForAudit) {
            // 删除课程基本信息
            courseBaseMapper.deleteById(courseId);
            // 删除课程营销信息
            courseMarketService.delete(courseId);
            // 删除课程计划（章节）信息
            teachplanService.deleteAll(courseId);
            // 删除课程教师信息
            courseTeacherService.deleteAll(courseId);

            return new ResponseResult(200, "删除课程成功");
        } else {
            XueChengEduException.cast("删除课程失败");
            return null;
        }
    }

    /**
     * 根据课程 id 确认课程审核状态
     * @param courseId 课程 id
     * @return 课程是否已经提交审核，false 未提交审核，true 已经提交审核或课程不存在
     */
    private boolean checkAuditState(Long courseId) {
        // 查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengEduException.cast("课程不存在");
            return true;
        }

        // 检查课程审核状态
        String auditStatus = courseBase.getAuditStatus();

        // TODO: 2020/3/16 课程审核状态码，应该通过 System 模块提供的接口获取，获取到后用 CodeValueParser 解析出 Code-Value Map (之后若使用了 Spring Cloud，可以通过 Feign 调用 System 模块的接口)
        if ("202002".equals(auditStatus))
            return false;
        else
            return true;
    }

}
