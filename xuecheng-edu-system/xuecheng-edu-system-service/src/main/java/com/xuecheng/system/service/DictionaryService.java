package com.xuecheng.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.system.model.po.Dictionary;

import java.util.List;

/**
 * @author Domenic
 * @Classname DictionaryService
 * @Description 数据字典 服务类
 * @Created by Domenic
 */
public interface DictionaryService extends IService<Dictionary> {

    /**
     * 查询所有数据字典内容
     * @return {@link List}&lt{@link Dictionary}&gt;
     */
    List<Dictionary> queryAll();

    /**
     * 根据code查询数据字典
     * @param code -- String 数据字典Code
     * @return {@link List}&lt{@link Dictionary}&gt;
     */
    Dictionary getByCode(String code);

}
