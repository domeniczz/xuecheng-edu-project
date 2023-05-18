[English](https://github.com/domeniczz/xuecheng-edu-project/blob/master/sql/README.md) | [中文](https://github.com/domeniczz/xuecheng-edu-project/blob/master/sql/README-zh.md)

## Data Dictionary

Database name: xc_edu_system

SQL file: xc_edu_system.sql

Audit status, course status, course type, user type, etc., have a common feature in that they have some classification items, and these classification items are relatively fixed. To improve the system's scalability, a special data dictionary table is defined to maintain these data.

## Content Module Data

Database name: xc_edu_content

SQL file: xc_edu_content.sql

Course information, chapter information, teacher information...

## Media Module Data

Database name: xc_edu_content

SQL file: xc_edu_media.sql

Media resource files, such as: images, course videos...

---

## Nacos Initialization Database

Database name: nacos_schema_1.4.1

SQL file: nacos_1.4.1.sql

## XXL-JOB Initialization Database

Database name: xxl-job_schema_2.3.1

SQL file: xxl_job_2.3.1.sql