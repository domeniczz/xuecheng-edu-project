# xuecheng-edu-project

线上教育平台

[中文](https://github.com/domeniczz/xuecheng-edu-project/blob/master/docs/README-zh.md) | [English](https://github.com/domeniczz/xuecheng-edu-project#readme)

## 技术栈

<img src="https://github.com/domeniczz/xuecheng-edu-project/blob/master/doc/assets/project-tech-stack.png" style="border-radius:.4rem" width="650rem" alt="Tech Stack"/>

## 开发环境

<table style="width:50rem">
    <thead>
        <tr style="text-align:left">
            <th style="width:35%">开发工具</th>
            <th style="width:35%">版本</th>
            <th style="width:30%">安装位置</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Windows 11</td>
            <td>22H2</td>
            <td>PC</td>
        </tr>
        <tr>
            <td>IntelliJ IDEA</td>
            <td>2021.x 及以上</td>
            <td>PC</td>
        </tr>
        <tr>
            <td>JDK</td>
            <td>1.8.x</td>
            <td>PC</td>
        </tr>
        <tr>
            <td>Maven</td>
            <td>3.8.x</td>
            <td>PC</td>
        </tr>
        <tr>
            <td>Git</td>
            <td>2.40.x</td>
            <td>PC</td>
        </tr>
        <tr>
            <td>VMWare Workstation</td>
            <td>17.x</td>
            <td>PC</td>
        </tr>
        <tr>
            <td>CentOS</td>
            <td>7.x</td>
            <td>VM</td>
        </tr>
        <tr>
            <td>Docker</td>
            <td>18.09.0</td>
            <td>VM</td>
        </tr>
        <tr>
            <td>MySQL</td>
            <td>8.x</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>Nacos</td>
            <td>1.4.1</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>RabbitMQ</td>
            <td>3.8.34</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>Redis</td>
            <td>6.2.7</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>XXL-JOB-Admin</td>
            <td>2.3.1</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>Minio</td>
            <td>RELEASE.2022-09-07</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>Elasticsearch</td>
            <td>7.12.1</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>Kibana</td>
            <td>7.12.1</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>Gogs</td>
            <td>0.13.0</td>
            <td>Docker</td>
        </tr>
        <tr>
            <td>Ngnix</td>
            <td>1.12.2</td>
            <td>Docker</td>
        </tr>
    </tbody>
</table>

## 使用

在本地运行

1. 将 CentOS 导入 VMware

   前往 编辑 -> 虚拟网络编辑器, 将 VMnet8 的子网 IP 设置为 192.168.101.0，子网掩码设置为 255.255.255.0

   虚拟机推荐配置：8GB 内存, 2 个 4 核心的处理器, 20 ~ 40GB 硬盘空间

   > 虚拟机 IP 地址：192.168.101.65  
   > 账号: root  
   > 密码: centos

2. 启动 Docker 容器

   ```bash
   systemctl start docker
   sh /data/soft/restart.sh
   ```

   查询 Docker 中的进程:

   ```bash
   docker ps
   ```

