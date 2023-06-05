[中文](https://github.com/domeniczz/xuecheng-edu-project/blob/master/dumps/sql/README-zh.md) | [English](https://github.com/domeniczz/xuecheng-edu-project/blob/master/dumps/sql/README.md)

## 数据字典

数据库名称：xc_edu_system

SQL 文件：xc_edu_system.sql

审核状态、课程状态、课程类型、用户类型等信息，有一个共同点就是它有一些分类项，且这些分类项较为固定。针对这些数据，为提高系统的可扩展性，故定义专门的数据字典表去维护

## 内容模块数据

数据库名称：xc_edu_content

SQL 文件：xc_edu_content.sql

课程信息，章节信息，教师信息...

## 媒资模块数据

数据库名称：xc_edu_content

SQL 文件：xc_edu_media.sql

媒体资源文件，如：图片，课程视频...

---

## Nacos 初始化数据库

数据库名称：nacos_1.4.1

SQL 文件：nacos_schema_1.4.1.sql

## XXL-JOB 初始化数据库

数据库名称：xxl_job_2.3.1

SQL 文件：xxl-job_schema_2.3.1.sql