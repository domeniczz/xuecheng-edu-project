package com.xuecheng.base.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Domenic
 * @Classname Beanutil
 * @Description Bean 工具类
 * @Created by Domenic
 */
public class BeanUtil {

    private BeanUtil() {
        // prevents other classes from instantiating it
    }

    /**
     * Bean properties merge policy
     */
    public enum MergePolicy {
        /**
         * Use properties in source if present, otherwise use properties in target
         * <pre>
         * source = Sample(id=null, age=28, name=null)
         * target = Sample(id=1001, age=18, name=domenic)
         * BeanUtil.mergeProperties(source, target)
         * target = Sample(id=1001, age=28, name=domenic)
         * </pre>
         */
        USE_SOURCE_IF_PRESENT,
        /**
         * Use properties in target if present, otherwise use properties in source
         * <pre>
         * source = Sample(id=null, age=28, name=domenic)
         * target = Sample(id=1001, age=18, name=null)
         * BeanUtil.mergeProperties(source, target)
         * target = Sample(id=1001, age=18, name=domenic)
         * </pre>
         */
        USE_TARGET_IF_PRESENT
    }

    /**
     * <p>
     * Merge properties from source to target,<br/>
     * default policy is {@link MergePolicy}.USE_SOURCE_IF_PRESENT
     * <p>
     * @param source source Bean
     * @param target target Bean
     */
    public static void mergeProperties(Object source, Object target) {
        mergeProperties(source, target, MergePolicy.USE_SOURCE_IF_PRESENT);
    }

    /**
     * <p>
     * Merge properties from source to target,<br/>
     * provide merge policy
     * <p>
     * @param source source Bean
     * @param target target Bean
     */
    public static void mergeProperties(Object source, Object target, MergePolicy mergePolicy) {
        if (mergePolicy == MergePolicy.USE_SOURCE_IF_PRESENT) {
            BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        } else if (mergePolicy == MergePolicy.USE_TARGET_IF_PRESENT) {
            BeanUtils.copyProperties(source, target, getNonNullPropertyNames(target));
        }
    }

    /**
     * 获取对象中所有属性值为 {@code null} 的属性名
     * @param obj 对象
     * @return 属性名数组 {@link String}[]
     */
    private static String[] getNullPropertyNames(Object obj) {
        final BeanWrapper src = new BeanWrapperImpl(obj);
        PropertyDescriptor[] descriptors = src.getPropertyDescriptors();

        Set<String> nullPropertyNames = new HashSet<>();

        for (PropertyDescriptor descriptor : descriptors) {
            String propertyName = descriptor.getName();
            Object srcValue = src.getPropertyValue(propertyName);
            if (srcValue == null) {
                nullPropertyNames.add(propertyName);
            }
        }

        return nullPropertyNames.toArray(new String[nullPropertyNames.size()]);
    }

    /**
     * 获取对象中所有属性值不为 {@code null} 的属性名
     * @param obj 对象
     * @return 属性名数组 {@link String}[]
     */
    private static String[] getNonNullPropertyNames(Object obj) {
        final BeanWrapper src = new BeanWrapperImpl(obj);
        PropertyDescriptor[] descriptors = src.getPropertyDescriptors();

        Set<String> nonNullPropertyNames = new HashSet<>();

        for (PropertyDescriptor descriptor : descriptors) {
            String propertyName = descriptor.getName();
            if (!"class".equals(propertyName)) {
                Object propertyValue = src.getPropertyValue(propertyName);
                if (propertyValue != null) {
                    nonNullPropertyNames.add(propertyName);
                }
            }
        }

        return nonNullPropertyNames.toArray(new String[nonNullPropertyNames.size()]);
    }

}
