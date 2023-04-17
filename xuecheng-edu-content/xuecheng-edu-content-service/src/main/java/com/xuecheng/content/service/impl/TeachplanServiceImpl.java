package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.base.enums.Direction;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.ResponseResult;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanMediaService;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Domenic
 * @Classname TeachplanServiceImpl
 * @Description 课程计划服务接口实现
 * @Created by Domenic
 */
@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanMediaService teachplanMediaService;

    @Override
    public List<TeachplanDto> queryTreeNodes(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    @Transactional
    public ResponseResult saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long id = saveTeachplanDto.getId();

        if (id == null) {
            // 新增
            return addTeachplan(saveTeachplanDto);
        } else {
            // 修改
            return updateTeachplan(saveTeachplanDto, id);
        }
    }

    private ResponseResult addTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Teachplan teachplan = new Teachplan();
        BeanUtils.copyProperties(saveTeachplanDto, teachplan);

        teachplan.setCreateDate(LocalDateTime.now());
        teachplan.setChangeDate(LocalDateTime.now());

        Long parentid = saveTeachplanDto.getParentid();
        Long courseId = saveTeachplanDto.getCourseId();
        int teachplanCount = getChildrenCount(courseId, parentid);
        // 自动设置，排序在最后
        teachplan.setOrderby(teachplanCount + 1);

        int res = teachplanMapper.insert(teachplan);

        ResponseResult resp = new ResponseResult();
        if (res > 0) {
            resp.setStatusCode(HttpStatus.OK.value());
            resp.setMessage("新增章节成功");
        } else {
            XueChengEduException.cast(CommonError.UNKOWN_ERROR);
        }

        return resp;
    }

    private ResponseResult updateTeachplan(SaveTeachplanDto saveTeachplanDto, Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        BeanUtils.copyProperties(saveTeachplanDto, teachplan);

        teachplan.setChangeDate(LocalDateTime.now());

        int res = teachplanMapper.updateById(teachplan);

        ResponseResult resp = new ResponseResult();
        if (res > 0) {
            resp.setStatusCode(HttpStatus.OK.value());
            resp.setMessage("修改章节成功");
        } else {
            XueChengEduException.cast(CommonError.UNKOWN_ERROR);
        }

        return resp;
    }

    @Override
    @Transactional
    public ResponseResult deleteTeachplan(Long id) {
        Teachplan teachplanToDelete = teachplanMapper.selectById(id);
        long parentId = getParentId(id);

        // 删除大章节
        if (parentId == 0) {
            int childCount = getChildrenCount(id);
            // 删除大章节，大章节下有小章节时不允许删除
            if (childCount > 0) {
                XueChengEduException.cast("课程计划信息还有子级信息，无法操作");
            } else {
                // 大章节下没有小章节，允许删除
                int res = teachplanMapper.deleteById(id);
                if (res < 0) {
                    XueChengEduException.cast(CommonError.UNKOWN_ERROR);
                }
            }
        }
        // 删除小章节
        else {
            // 删除小章节，同时将关联的信息进行删除
            int resPlan = teachplanMapper.deleteById(id);
            if (resPlan < 0) {
                XueChengEduException.cast(CommonError.UNKOWN_ERROR);
            } else {
                // 删除小章节后，还要删除在课程计划媒资关联表中的数据
                int resMedia = teachplanMediaService.deleteTeachplanMedia(id);
                if (resMedia < 0) {
                    XueChengEduException.cast(CommonError.UNKOWN_ERROR);
                } else {
                    // 删除小章节后，将大章节下的小章节排序进行重新排序
                    int teachplanCount = getChildrenCount(teachplanToDelete.getCourseId(),
                            teachplanToDelete.getParentid());
                    if (teachplanCount > 0) {
                        // 查询大章节下的所有小章节
                        List<Teachplan> teachplanList = teachplanMapper.selectList(new LambdaQueryWrapper<Teachplan>()
                                .eq(Teachplan::getParentid, teachplanToDelete.getParentid()));
                        // 遍历小章节，重新设置排序字段 orderedby
                        for (int i = 0; i < teachplanList.size(); i++) {
                            Teachplan teachplan = teachplanList.get(i);
                            teachplan.setOrderby(i + 1);
                            teachplanMapper.updateById(teachplan);
                        }
                    }
                }
            }
        }
        // 返回操作成功的响应
        return new ResponseResult(HttpStatus.OK.value(), "删除课程计划成功");
    }

    @Override
    @Transactional
    public ResponseResult moveUp(Long id) {
        move(id, Direction.UP);
        return new ResponseResult(HttpStatus.OK.value(), "上移课程计划成功");
    }

    @Override
    @Transactional
    public ResponseResult moveDown(Long id) {
        move(id, Direction.DOWN);
        return new ResponseResult(HttpStatus.OK.value(), "下移课程计划成功");
    }

    /**
     * 获取指定课程计划的，子节点数量
     * @param courseId 课程 id
     * @param parentId 父级 id
     * @return 课程计划数量
     */
    private int getChildrenCount(Long courseId, Long parentId) {
        // SQL: SELECT COUNT(id) FROM teachplan WHERE course_id = #{courseId} AND parentid = #{parentId}
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

    /**
     * 根据 id 获取指定课程计划的，子级节点数量
     * @param id id
     * @return 子级节点数量
     */
    private int getChildrenCount(Long id) {
        // SQL: SELECT COUNT(id) FROM teachplan WHERE parentid = #{id}
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getParentid, id);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

    /**
     * 根据 id 获取同级节点数量 (包括自己)
     * @param id id
     * @return 同级节点数量
     */
    private int getPeerCount(Long id) {
        // SQL: SELECT parentid FROM teachplan WHERE id = #{id}
        Long parentId = teachplanMapper.selectById(id).getParentid();
        // SQL: SELECT id FROM teachplan WHERE id = #{parentid}
        Long parentPlanId = teachplanMapper
                .selectOne(new LambdaQueryWrapper<Teachplan>().eq(Teachplan::getId, parentId)).getId();
        int count = getChildrenCount(parentPlanId);
        return count;
    }

    /**
     * 获取父级节点 id
     * @param id id
     * @return 父级节点 id
     */
    private long getParentId(Long id) {
        // SQL: SELECT parentid FROM teachplan WHERE parentid = #{id}
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.select(Teachplan::getParentid).eq(Teachplan::getId, id);
        Teachplan teachplan = teachplanMapper.selectOne(queryWrapper);
        return teachplan.getParentid();
    }

    /**
     * 移动课程计划
     * @param id 待移动的课程计划的 id
     * @param direction 移动方向
     */
    public void move(long id, Direction direction) {
        Teachplan teachPlan = teachplanMapper.selectById(id);
        Long parentid = teachPlan.getParentid();

        long courseId = 0;
        // 如果是大章节
        if (parentid == 0)
            courseId = teachPlan.getCourseId();

        // 获取当前排序字段
        int orderby = teachPlan.getOrderby();

        /* 如果是上移 */
        if (direction.equals(Direction.UP)) {
            if (orderby == 1) {
                XueChengEduException.cast("已经是第一个啦!");
            } else {
                // 更新当前章节的排序字段
                int preOrderby = orderby - 1;

                Teachplan preTeachPlan = null;
                if (parentid != 0) {
                    // 查找前一个小章节
                    // SQL: SELECT * FROM teachplan WHERE orderby = #{preOrderby} AND parentid = #{parentid}
                    preTeachPlan = teachplanMapper.selectOne(new LambdaQueryWrapper<Teachplan>()
                            .eq(Teachplan::getParentid, parentid)
                            .eq(Teachplan::getOrderby, preOrderby));
                } else {
                    // 查找前一个大章节
                    // SQL: SELECT * FROM teachplan WHERE orderby = #{preOrderby} AND course_id = #{courseId}
                    preTeachPlan = teachplanMapper.selectOne(new LambdaQueryWrapper<Teachplan>()
                            .eq(Teachplan::getCourseId, courseId)
                            .eq(Teachplan::getOrderby, preOrderby)
                            .eq(Teachplan::getParentid, parentid));
                }

                // 更新当前章节的排序字段
                teachPlan.setOrderby(preOrderby);
                int res1 = teachplanMapper.updateById(teachPlan);
                // 更新前一个章节的排序字段
                preTeachPlan.setOrderby(orderby);
                int res2 = teachplanMapper.updateById(preTeachPlan);

                if (res1 <= 0 || res2 <= 0) {
                    XueChengEduException.cast("上移失败 (T_T)");
                }
            }
        }
        /* 如果是下移 */
        else if (direction.equals(Direction.DOWN)) {
            int nextOrderby = orderby + 1;

            Teachplan nextTeachPlan = null;
            if (parentid != 0) {
                // 查找后一个小章节
                // SQL: SELECT * FROM teachplan WHERE orderby = #{nextOrderby} AND parentid = #{parentid}
                nextTeachPlan = teachplanMapper.selectOne(new LambdaQueryWrapper<Teachplan>()
                        .eq(Teachplan::getParentid, parentid)
                        .eq(Teachplan::getOrderby, nextOrderby));
            } else {
                // 查找后一个大章节
                // SQL: SELECT * FROM teachplan WHERE orderby = #{nextOrderby} AND course_id = #{courseId}
                nextTeachPlan = teachplanMapper.selectOne(new LambdaQueryWrapper<Teachplan>()
                        .eq(Teachplan::getCourseId, courseId)
                        .eq(Teachplan::getOrderby, nextOrderby)
                        .eq(Teachplan::getParentid, parentid));
            }

            if (nextTeachPlan != null) {
                // 更新当前章节的排序字段
                teachPlan.setOrderby(nextOrderby);
                int res1 = teachplanMapper.updateById(teachPlan);

                // 更新后一个章节的排序字段
                nextTeachPlan.setOrderby(orderby);
                int res2 = teachplanMapper.updateById(nextTeachPlan);

                if (res1 <= 0 || res2 <= 0) {
                    XueChengEduException.cast("下移失败 (T_T)");
                }
            } else {
                XueChengEduException.cast("已经是最后一个啦!");
            }
        }
        /* 传入的移动方向参数错误 */
        else {
            log.error("移动课程计划时，传入的移动方向参数错误");
            XueChengEduException.cast(CommonError.UNKOWN_ERROR);
        }
    }

}
