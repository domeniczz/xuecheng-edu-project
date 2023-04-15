package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Domenic
 * @Classname CourseCategoryController
 * @Description 课程分类信息接口
 * @Created by Domenic
 */
@RestController
@RequestMapping("/course-category")
@Api(value = "课程分类信息接口", tags = "课程分类信息接口")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @GetMapping("/tree-nodes")
    @ApiOperation("查询所有分类信息")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        String RootNodeId = "1";
        return courseCategoryService.queryTreeNodes(RootNodeId);
    }

}
