package com.xuecheng.system.controller;

import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 * @author itcast
 */
@Slf4j
@RestController
@RequestMapping("/dictionary")
@Api(value = "数据字典请求接口", tags = "数据字典请求接口")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/all")
    @ApiOperation("查询所有数据字典")
    public List<Dictionary> queryAll() {
        return dictionaryService.queryAll();
    }

    @GetMapping("/code/{code}")
    @ApiOperation("根据码查对应的映射名称")
    public Dictionary getByCode(@PathVariable String code) {
        return dictionaryService.getByCode(code);
    }

}
