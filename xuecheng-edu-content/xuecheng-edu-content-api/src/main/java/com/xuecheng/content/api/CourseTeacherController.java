package com.xuecheng.content.api;

import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Domenic
 * @Classname CourseTeacherController
 * @Description 课程师资管理接口
 * @Created by Domenic
 */
@RestController
@RequestMapping("/courseTeacher")
@Api(value = "师资管理接口", tags = "师资管理接口")
public class CourseTeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @GetMapping("list/{courseId}")
    @ApiOperation("查询课程师资列表")
    @ApiImplicitParam(value = "courseId", name = "课程 id", required = true, dataType = "Long", paramType = "path")
    public List<CourseTeacher> list(@PathVariable Long courseId) {
        return courseTeacherService.queryTeacherList(courseId);
    }

    @PostMapping("")
    @ApiOperation("保存课程师资")
    public CourseTeacher creatCourseTeacher(@RequestBody @Validated CourseTeacher teacher) {
        return courseTeacherService.save(teacher);
    }

    @DeleteMapping("/course/{courseId}/{teacherId}")
    @ApiOperation("删除课程师资")
    @ApiImplicitParam(value = "courseId", name = "课程 id", required = true, dataType = "Long", paramType = "path")
    public ResponseResult deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        return courseTeacherService.delete(courseId, teacherId);
    }

}
