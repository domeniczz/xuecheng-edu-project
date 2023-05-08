package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.base.validation.ValidationGroups;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.UpdateCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Domenic
 * @Classname CourseBaseInfoController
 * @Description 课程信息接口
 * @Created by Domenic
 */
@RestController
@RequestMapping("/course")
@Api(value = "课程信息接口", tags = "课程信息接口")
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @PostMapping("/list")
    @ApiOperation("课程列表查询接口")
    public PageResult<CourseBase> list(PageParams pageParams,
            @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        return courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
    }

    @GetMapping("/{courseId}")
    @ApiOperation("根据课程 ID 查询课程信息")
    @ApiImplicitParam(value = "courseId", name = "课程 id", required = true, dataType = "Long", paramType = "path")
    public CourseBaseInfoDto getCourseBaseInfoById(@PathVariable Long courseId) {
        return courseBaseInfoService.queryCourseBaseAndMarketInfoById(courseId);
    }

    @PostMapping("")
    @ApiOperation("新增课程")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto) {
        // 通过单点登录系统，获取到用户所属机构的 ID
        // 为了方便测试，这里先写死
        // TODO: 4/7/2023 5:09 PM 通过单点登录系统，获取到用户所属机构的 ID
        long companyId = 1232141425L;
        return courseBaseInfoService.create(companyId, addCourseDto);
    }

    @PutMapping("")
    @ApiOperation("修改课程")
    public CourseBaseInfoDto updateCourseBaseById(@RequestBody @Validated(ValidationGroups.Update.class) UpdateCourseDto updateCourseDto) {
        // 通过单点登录系统，获取到用户所属机构的 ID
        // 为了方便测试，这里先写死
        // TODO: 4/7/2023 5:09 PM 通过单点登录系统，获取到用户所属机构的 ID
        long companyId = 1232141425L;
        return courseBaseInfoService.update(companyId, updateCourseDto);
    }

    @DeleteMapping("/{courseId}")
    @ApiOperation("删除课程")
    @ApiImplicitParam(value = "courseId", name = "课程 id", required = true, dataType = "Long", paramType = "path")
    public RestResponse<?> deleteCourseBaseById(@PathVariable Long courseId) {
        return courseBaseInfoService.delete(courseId);
    }

}
