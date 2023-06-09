You can build the docker environment for this project on your own if you like.

Below are the instructions of installing each tool in docker.

> **NOTE:** If you're running Docker in Windows, it's recommended to run docker commands in the WSL distribution for better compatibility. Otherwise, you may encounter some weird errors, for example, failed to mount directories in WSL to docker containers.
>
> **NOTE:** Remember to check if the target directory of `--volume` exists in docker container, if not sure, you can create a test container first to find target directory in container.
>
> **NOTE:** Before running a Docker container, if you mount a volume to it, make sure

## MySQL

### Install

[mysql - docker hub](https://hub.docker.com/_/mysql)

**Pull the image:**

```bash
docker pull mysql:8.0.30
```

**Create & Run the container:**

```bash
docker run -d \
	--name=mysql \
	-p 3304:3306 \
	# mount local directory to mysql container (may have some problem here)
	# -v /data/mysql/:/var/lib/mysql
	# set password for root user
	-e MYSQL_ROOT_PASSWORD=root \
	mysql:8.0.30
```

**Attach shell & Log into the container:**

```bash
## go into mysql container
docker exec -it mysql /bin/bash

## loginng into mysql (DO NOT write password in command line explicitly)
mysql -uroot -p
```

**Start & Stop container:**

```bash
docker start mysql
docker stop mysql
```

### Dump

**Preserve the data** from the current MySQL container, and restore from the other.

1. **Create a dump of your database**. Use the `mysqldump` command for this.

   Execute it in yout running MySQL container:

   ```bash
   # replace $MYSQL_ROOT_PASSWORD with the root password
   docker exec mysql sh -c 'exec mysqldump --all-databases -uroot -p"$MYSQL_ROOT_PASSWORD"' > all-databases.sql
   ```

   This will create a dump of all databases in your MySQL container and save it to a file named `all-databases.sql` in your current directory.

2. **Stop the old container**:

   ```bash
   docker stop mysql
   ```

3. **Rename the old container** (optional):

   ```bash
   docker rename mysql mysql_old
   ```

3. **Create the new MySQL container**

4. **Restore the data from the dump**:

   ```bash
   # replace $MYSQL_ROOT_PASSWORD with the root password
   docker exec -i mysql sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD"' < all-databases.sql
   ```

   > If you are using Powershell, execute this command instead:
   >
   > ```powershell
   > Get-Content all-databases.sql | docker exec -i mysql sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD"'
   > ```

## Nacos

[nacos - docker hub](https://hub.docker.com/r/nacos/nacos-server)

**Pull the image:**

```bash
docker pull nacos/nacos-server:1.4.1
```

**Create & Run the container:**

```bash
docker run -d \
    --name nacos \
    --restart always \
    # --privileged \
    -p 8848:8848 \
    -e JVM_XMS=256m \
    -e JVM_XMX=256m \
    -e MODE=standalone \
    -e PREFER_HOST_MODE=hostname \
    -e FUNCTION_MODE=all \
    -e NACOS_DEBUG=n \
    -e TOMCAT_ACCESSLOG_ENABLED=false \
    -e TIME_ZONE=Asia/Shanghai \
    # config to connect to MySQL database in another docker container
    -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/nacos_1.4.1?useSSL=false&characterEncoding=utf8&serverTimezone=UTC&connectTimeout=1000&socketTimeout=3000&autoReconnect=true" \
    -e SPRING_DATASOURCE_USERNAME=root \
    -e SPRING_DATASOURCE_PASSWORD=root \
    # connect to a custom network
    --network=<network-name> \
    ## volume is optional if you like (may have some problem here)
    # -v /data/nacos/logs:/home/nacos/data \
    # -v /data/nacos/logs:/home/nacos/logs \
    # -v /data/nacos/conf:/home/nacos/conf \
    -e MODE=standalone \
    nacos/nacos-server:1.4.1 \
    bin/docker-startup.sh
```

**Attach shell & Log into the container:**

```bash
## go into nacos container
docker exec -it nacos /bin/bash
```

**Start & Stop container:**

```bash
docker start nacos
docker stop nacos
```

## Minio

[minio - docker hub](https://hub.docker.com/r/minio/minio)

**Pull the image:**

```bash
docker pull minio/minio:RELEASE.2022-09-07T22-25-02Z
```

**Create & Run the container:**

```bash
docker run -d \
	--name=minio \
	-p 9000:9000 \
	-p 9001:9001 \
	--restart=always \
	-e MINIO_ROOT_USER=minioadmin \
	-e MINIO_ROOT_PASSWORD=minioadmin \
	## to mount volume for "/data", you should have multiple drives to store data
	## otherwise, error occurs: "Read failed. Insufficient number of drives online"
	## (may have some problem here)
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

**Attach shell & Log into the container:**

```bash
## go into minio container
docker exec -it minio /bin/bash
```

**Start & Stop container:**

```bash
docker start minio
docker stop minio
```

## XXL-JOB

[xxl-job - docker hub](https://hub.docker.com/r/xuxueli/xxl-job-admin)

**Pull the image:**

```bash
docker pull xuxueli/xxl-job-admin:2.3.1
```

> XXL-JOB needs to connect to a database to work.
>
> There's already a [MySQL database container](#MySQL) installed, then, we can connect MySQL container and XXL-JOB container to a same [docker network](https://docs.docker.com/engine/reference/commandline/network/), to let XXL-JOB use MySQL.
>
> Actually, there's a default network (called `bridge`) in docker that every containers are connected to it. However, the limitation of the default bridge network is that, it doesn't provide **automatic service discovery**, which means containers cannot **communicate** with each other **by container name**.
>
> When you create a custom network, Docker's **built-in DNS resolver** allows containers to **communicate** with each other **using container name**.
>
> Thus, we're creating a new network.
>
> ```bash
> # Create a new network
> docker network create <network-name>
>
> # Connect MySQL container to the network
> docker network connect <network-name> mysql
>
> # Connect XXL-JOB container to the network (after creating)
> docker network connect <network-name> xxl-job-admin
>
> # Disconnect from a network (if needed)
> docker network disconnect <network-name> <container-name>
> ```

**Create & Run the container:**

```bash
docker run -d \
	--name xxl-job-admin \
	# In this case, database url is "jdbc:mysql://mysql:3306/xxl_job_2.3.1", 
	# `mysql` is the container name of MySQL database that it's connected to
	# 3306 is the default MySQL port (NOT the mapped port)
	-e PARAMS="--spring.datasource.url=jdbc:mysql://mysql:3306/xxl_job_2.3.1?useUnicode=true&serverTimezone=UTC&useSSL=false&characterEncoding=UTF-8&autoReconnect=true --spring.datasource.username=root --spring.datasource.password=root" \
	-p 8088:8080 \
	# mount local directory /data/xxl-job-admin to the container (may have some problem here)
	# -v /data/xxl-job-admin:/data/applogs \
	xuxueli/xxl-job-admin:2.3.1
```

```bash
# set container auto start-up
docker update --restart=always xxl-job-admin
```

**Attach shell & Log into the container:**

```bash
# go into xxl-job-admin container
docker exec -it xxl-job-admin bash
```

**Start & Stop container:**

```bash
docker start xxl-job-admin
docker stop xxl-job-admin
```

## Nginx

### Install

[nginx - docker hub](https://hub.docker.com/_/nginx)

**pull the image:**

```bash
docker pull nginx:latest
```

**Create & Run the container:**

```bash
docker run -d \
	--name nginx \
	-v /data/nginx/html:/usr/share/nginx/html \
	-v /data/nginx/conf:/etc/nginx \
	-p 80:80 \
	nginx:latest
```

```bash
# set container auto start-up
docker update --restart=always nginx
```

**Attach shell & Log into the container:**

```bash
# go into nginx container
docker exec -it nginx /bin/bash
```

**Start & Stop container:**

```bash
docker start nginx
docker stop nginx
```

### Usage

everytime config file has been changed, you should reload nginx:

```bash
# run the command inside the container
nginx -s reload

# run command outside of the container
docker exec nginx nginx -s reload
```

To access minio from another container via nginx, it's recommended not to use the localhost address (127.0.0.1) as the host. Instead, the container name (for instance, "minio") should be specified as the host.
