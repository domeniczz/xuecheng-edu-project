package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Domenic
 * @Classname CourseCategoryServiceImpl
 * @Description TODO
 * @Date 4/11/2023 8:40 PM
 * @Created by Domenic
 */
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String rootCourseID) {
        /* 获取到课程分类信息 */
        List<CourseCategoryTreeDto> list = courseCategoryMapper.selectTreeNodes(rootCourseID);

        /* 将结果封装成进 List<CourseCategoryTreeDto> */

        // 将 List 转换为 Map：Key 是课程分类的 id；Value 是 CourseCategory
        // 因为通过 ID 查 CourseCategory，Map 更方便
        Map<String, CourseCategoryTreeDto> map = list.stream()
                .filter(item -> !rootCourseID.equals(item.getId()))
                .collect(Collectors.toMap(CourseCategory::getId, val -> val, (val1, val2) -> val2));

        List<CourseCategoryTreeDto> result = new ArrayList<>();

        // 遍历 List，一边遍历一边找子节点放入父节点的 childrenTreeNodes 中
        list.stream()
                .filter(item -> !rootCourseID.equals(item.getId()))
                .forEach(item -> {
                    String parentid = item.getParentid();

                    if (rootCourseID.equals(parentid)) {
                        result.add(item);
                    }

                    CourseCategoryTreeDto parent = map.get(parentid);
                    if (parent != null) {
                        if (parent.getChildrenTreeNodes() == null) {
                            parent.setChildrenTreeNodes(new ArrayList<>());
                        }
                        parent.getChildrenTreeNodes().add(item);
                    }
                });

        return result;
    }

}