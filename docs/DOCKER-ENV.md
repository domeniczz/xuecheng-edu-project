You can build the docker environment on your own if you like.

Below are the instructions of installing each tool in docker.

## MySQL

[mysql - docker hub](https://hub.docker.com/_/mysql)

```bash
docker pull mysql:8.0.30
```

```bash
docker run -dt \
	--name=mysql \
	-p 8806:8806 \
	# root user password
	-e MYSQL_ROOT_PASSWORD=root \
	mysql:8.0.30
```

```bash
## go into mysql container
docker exec -it mysql /bin/bash

## loginng into mysql (DO NOT write password in command line explicitly)
mysql -uroot -p
```

```bash
docker start mysql
docker stop mysql
```

## Nacos

[nacos - docker hub](https://hub.docker.com/r/nacos/nacos-server)

```bash
docker pull nacos/nacos-server:1.4.1
```

```bash
docker run -dt \
    --name nacos \
    --restart always \
    --privileged \
    -p 8848:8848 \
    -e JVM_XMS=256m \
    -e JVM_XMX=256m \
    -e MODE=standalone \
    -e PREFER_HOST_MODE=hostname \
    -e FUNCTION_MODE=all \
    -e NACOS_DEBUG=n \
    -e TOMCAT_ACCESSLOG_ENABLED=false \
    -e TIME_ZONE=Asia/Shanghai \
    ## volume is optional if you like
    # -v /data/soft/nacos/logs:/home/nacos/logs \
    # -v /data/soft/nacos/conf/application.properties:/home/nacos/conf/application.properties \
    -e MODE=standalone \
    nacos/nacos-server:1.4.1 \
    bin/docker-startup.sh
```

```bash
## go into nacos container
docker exec -it nacos /bin/bash
```

```bash
docker start nacos
docker stop nacos
```

## Minio

[minio - docker hub](https://hub.docker.com/r/minio/minio)

```bash
docker pull minio/minio:RELEASE.2022-09-07T22-25-02Z
```

```bash
docker run -dt \
	--name=minio \
	-p 9000:9000 \
	-p 9001:9001 \
	--restart=always \
	-e MINIO_ROOT_USER=minioadmin \
	-e MINIO_ROOT_PASSWORD=minioadmin \
	## to mount volume for "/data", you should have multiple drives to store data
	## otherwise, error occurs: "Read failed. Insufficient number of drives online"
	# -v /home/domenic/minio/data:/data \
	# -v /home/domenic/minio/config:/root/.minio \
	minio/minio:RELEASE.2022-09-07T22-25-02Z \
	server /data \
	## let minio store data in multi directories using following command
	## to simulate multiple hard drives
	# server /data/1 /data/2 /data/3 /data/4 \
	## 9001 is console port; 9000 is API port
	--console-address ":9001" -address ":9000"
```

Minio Console:

User: minioadmin; Password: minioadmin

```bash
## go into minio container
docker exec -it minio /bin/bash
```

```bash
docker start minio
docker stop minio
```

