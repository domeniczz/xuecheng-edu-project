package com.xuecheng.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.system.mapper.DictionaryMapper;
import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.service.DictionaryService;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname DictionaryServiceImpl
 * @Description 数据字典 服务实现类
 * @Created by Domenic
 */
@Slf4j
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Override
    public List<Dictionary> queryAll() {

        return this.list();
    }

    @Override
    public Dictionary getByCode(String code) {

        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getCode, code);

        Dictionary dictionary = this.getOne(queryWrapper);

        if (dictionary == null) {
            log.error("根据code查询数据字典出错, code: {}", code);
            XueChengEduException.cast(String.format("code: %s 查询字典失败", code));
        }

        return dictionary;
    }
}
