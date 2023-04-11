package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Domenic
 * @Classname CourseBaseMapperTest
 * @Description TODO
 * @Date 4/7/2023 10:00 PM
 * @Created by Domenic
 */
@SpringBootTest
public class CourseBaseInfoServiceTest {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

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

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        Assertions.assertNotNull(courseBasePageResult);

        System.out.println(
                "\n===================================================\n"
                + courseBasePageResult
                + "\n===================================================\n");
    }

}