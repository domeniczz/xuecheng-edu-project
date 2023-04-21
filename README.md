# xuecheng-edu

Online Education Platform

[English](https://github.com/domeniczz/xuecheng-edu-project#readme) | [中文](https://github.com/domeniczz/xuecheng-edu-project/blob/master/docs/README-zh.md)

## Introduction

### Tech Stack

<img src="https://github.com/domeniczz/xuecheng-edu-project/blob/master/docs/assets/project-tech-stack.png" style="border-radius:.4rem" width="650rem" alt="Tech Stack"/>

### Develop Environment

<table style="width:50rem">
    <thead>
        <tr style="text-align:left">
            <th style="width:35%">Tool</th>
            <th style="width:35%">Version</th>
            <th style="width:30%">Install place</th>
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
            <td>2021.x and newer</td>
            <td>PC</td>
        </tr>
        <tr>
            <td>Visual Studio Code</td>
            <td>1.77 and newer</td>
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
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>Nacos</td>
            <td>1.4.1</td>
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>RabbitMQ</td>
            <td>3.8.34</td>
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>Redis</td>
            <td>6.2.7</td>
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>XXL-JOB-Admin</td>
            <td>2.3.1</td>
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>Minio</td>
            <td>RELEASE.2022-09-07</td>
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>Elasticsearch</td>
            <td>7.12.1</td>
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>Kibana</td>
            <td>7.12.1</td>
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>Gogs</td>
            <td>0.13.0</td>
            <td>Docker in VM</td>
        </tr>
        <tr>
            <td>Ngnix</td>
            <td>1.12.2</td>
            <td>Docker in VM</td>
        </tr>
    </tbody>
</table>
## Usage

run on localhost

1. Import CentOS VM into VMware

   Go to Edit -> virtual network editor, set VMnet8 subnet IP to 192.168.101.0 and subnet mask to 255.255.255.0

   Virtual machine recommend hardware setting: 8GB Memory, 2 Processor with 4 core for each, 20 ~ 40GB Hard Disk

   > VM IP Address: 192.168.101.65  
   > Account: root  
   > Password: centos

2. Run Docker

   ```bash
   systemctl start docker
   sh /data/soft/restart.sh
   ```

   Check docker processes:

   ```bash
   docker ps
   ```

