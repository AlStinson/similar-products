# Similar products service

This is my solution to the technical test described in
the [backendDevTest repository](https://github.com/dalogax/backendDevTest). I
have created a new REST API operation that will provide the product details of the similar products for a given
one.

## Setup

The setup need [docker](https://www.docker.com/)

First you have to start the mock APIs and other infrastructure needed. In order to achieve that you have to
clone/download the [backendDevTest repository](https://github.com/dalogax/backendDevTest), navigate to that folder with
the command prompt and run

``` 
docker compose up -d simulado influxdb grafana
```

Now, download [docker-compose.yml](./docker-compose.yml) from this repository (you don't need the whole repository,
just this file), put it in a folder where you don't have other files with name ```docker-compose.yml```, go to that
folder with the command prompt and run

``` 
docker compose up -d
```

## Requests

The API only enables one operation at http://localhost:5000/product/{productId}/similar.
As it is a GET request, it can be consumed via browser, but it can also be consumed with postman
importing [this collection](./SimilarProducts.postman_collection.json)

### Request examples

http://localhost:5000/product/1/similar <br>
http://localhost:5000/product/3/similar <br>
http://localhost:5000/product/5/similar <br>
http://localhost:5000/product/8/similar

## Testing and performance

Go with the command prompt to the folder where you cloned/downloaded
the [backendDevTest repository](https://github.com/dalogax/backendDevTest) and run

```
docker compose run --rm k6 run scripts/test.js
```

Results can be seen in the command prompt and in [this page](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test)

## Configuration

A different configuration can be used adding values to the ```environment``` key
in [docker-compose.yml](./docker-compose.yml) file. This should be done before the last setup action, but if you already
did it, you can stop that docker container, make this changes, and run the last command in setup section again.

Adding values in the [docker-compose.yml](./docker-compose.yml) file is similar to adding values in
the [application.properties](./src/main/resources/application.properties) file of the application, but the keys should
be in uppercase and hyphen(-) and dot(.) should be replaced by underscore(_). For example, application name and port can
be changed adding the following

```
environment:
  SPRINGAPPLICATION_NAME=new-application-name
  SERVER_PORT=6001
```

The url of the existing API (products service) is configured with ```product-service.url```. The connection is made via
[Feign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/), so all its configurations can be
applied. For example ```spring.cloud.openfeign.client.config.default.connectTimeout``` and
```spring.cloud.openfeign.client.config.default.readTimeout``` for connection timeout and read timeout. Default
configuration is 2000 (2 seconds) for both values.

Cache is managed by [Caffeine](https://github.com/ben-manes/caffeine), so again, all its configurations can be applied.
Default configuration is ```spring.cache.caffeine.spec=expireAfterWrite=5s```, so the only default configuration is that
cache entries expire 5 seconds after they are registered.
