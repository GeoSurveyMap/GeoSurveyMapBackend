# Getting Started

### Prerequisites

- running docker

### How to use the application?
Before running the application, set the executable mode for the `wait-for-it.sh` file  from the following directory
`dev/docker/`<br/>
```shell
chmod +x wait-for-it.sh
```

Then just use the `start-remote.sh` script  from the `dev/` directory<br/>
```shell
sh start-remote.sh
```

Api will be available under the following [URL](http://localhost:8080/swagger-ui/index.html#/)

Minio is available under the following [URL](http://localhost:9090/buckets)

Minio credentials:
 - minioadmin
 - minioadmin
