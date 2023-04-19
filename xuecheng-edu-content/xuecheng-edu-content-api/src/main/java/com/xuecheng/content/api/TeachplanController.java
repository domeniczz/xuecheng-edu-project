package com.xuecheng.content.api;

import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Domenic
 * @Classname TeachplanController
 * @Description 课程计划接口
 * @Created by Domenic
 */
@RestController
@RequestMapping("/teachplan")
@Api(value = "课程计划接口", tags = "课程计划接口")
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    @GetMapping("/{courseId}/tree-nodes")
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程 id", required = true, dataType = "Long", paramType = "path")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.queryTreeNodes(courseId);
    }

    @PostMapping("")
    @ApiOperation("课程计划创建或修改")
    public Teachplan saveTeachplan(@RequestBody SaveTeachplanDto teachplan) {
        return teachplanService.saveTeachplan(teachplan);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除课程计划")
    @ApiImplicitParam(value = "id", name = "课程计划 id", required = true, dataType = "Long", paramType = "path")
    public ResponseResult deleteTeachplan(@PathVariable Long id) {
        return teachplanService.deleteTeachplan(id);
    }

    @PostMapping("/moveup/{id}")
    @ApiOperation("课程计划上移")
    @ApiImplicitParam(value = "id", name = "课程计划 id", required = true, dataType = "Long", paramType = "path")
    public ResponseResult moveUp(@PathVariable Long id) {
        return teachplanService.moveUp(id);
    }

    @PostMapping("/movedown/{id}")
    @ApiOperation("课程计划下移")
    @ApiImplicitParam(value = "id", name = "课程计划 id", required = true, dataType = "Long", paramType = "path")
    public ResponseResult moveDown(@PathVariable Long id) {
        return teachplanService.moveDown(id);
    }

}
