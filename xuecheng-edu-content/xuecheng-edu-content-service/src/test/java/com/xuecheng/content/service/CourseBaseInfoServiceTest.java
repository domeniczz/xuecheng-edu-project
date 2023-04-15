package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Domenic
 * @Classname CourseBaseMapperTest
 * @Description 课程信息服务测试类
 * @Created by Domenic
 */
@SpringBootTest
public class CourseBaseInfoServiceTest {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Test
    void testCourseBaseInfoService() {

        // 分页参数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L); // 当前页码
        pageParams.setPageSize(3L); // 每页最多记录数

        // 查询条件
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setAuditStatus("202004");
        queryCourseParamsDto.setPublishStatus("203001");

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams,
                queryCourseParamsDto);
        Assertions.assertNotNull(courseBasePageResult);

        System.out.println(
                "\n===================================================\n"
                        + courseBasePageResult
                        + "\n===================================================\n");
    }

}
