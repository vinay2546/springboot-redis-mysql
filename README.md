# Spring Boot + MySQL + Redis Demo

## Architecture

    Client
       |
       v
    Spring Boot
       |
       +--> Redis (Cache)
       |
       +--> MySQL (Source of Truth)
       |
       +--> RabbitMQ (Messaging)

## Tech Stack

-   Spring Boot
-   Spring Data JPA
-   MySQL
-   Redis
-   RabbitMQ
-   Docker

------------------------------------------------------------------------

# Start Services

``` bash
docker compose up -d
```

Verify:

``` bash
docker ps
```

------------------------------------------------------------------------

# API Endpoints

Base URL

    http://localhost:8080

## 1. Create User

**POST** `/users`

Request

``` json
{
  "name": "Vinay",
  "email": "vinay@test.com"
}
```

Example

``` bash
curl -X POST http://localhost:8080/users \
-H "Content-Type: application/json" \
-d "{\"name\":\"Vinay\",\"email\":\"vinay@test.com\"}"
```

------------------------------------------------------------------------
## 2. Create User via RabbitMQ

**POST** `/users/mq`

Request

``` json
{
  "name": "Vinay",
  "email": "vinay@test.com"
}
```

Example

``` bash
curl -X POST http://localhost:8080/users/mq \
-H "Content-Type: application/json" \
-d "{\"name\":\"Vinay\",\"email\":\"vinay@test.com\"}"
```
Processing Flow

    Client
       |
    POST /users/mq
       |
    UserController
       |
    UserProducer
       |
    RabbitMQ (user.queue)
       |
    UserConsumer (@RabbitListener)
       |
    MySQL

------------------------------------------------------------------------

## 3. Get User

**GET** `/users/{id}`

Example

``` bash
curl http://localhost:8080/users/1
```

Flow

    Redis
      |
    Hit? ---- Yes ---> Return User
      |
     No
      |
    MySQL
      |
    Store in Redis
      |
    Return User

------------------------------------------------------------------------

## 4. Get All Users

**GET** `/users`

``` bash
curl http://localhost:8080/users
```

------------------------------------------------------------------------

## 5. Delete User

**DELETE** `/users/{id}`

``` bash
curl -X DELETE http://localhost:8080/users/1
```

Flow

    Delete from MySQL
            |
    Delete from Redis

------------------------------------------------------------------------

# Redis Commands

## Connect

``` bash
docker exec -it redis-cache redis-cli
```

------------------------------------------------------------------------

## List Keys

``` redis
KEYS *
```

------------------------------------------------------------------------

## Read Cached User

``` redis
GET user:1
```

------------------------------------------------------------------------

## Check if Key Exists

``` redis
EXISTS user:1
```

------------------------------------------------------------------------

## TTL

``` redis
TTL user:1
```

------------------------------------------------------------------------

## Delete Cache

``` redis
DEL user:1
```

------------------------------------------------------------------------

## Delete All Keys

``` redis
FLUSHALL
```

------------------------------------------------------------------------

# MySQL Commands

Connect

``` bash
docker exec -it mysql-db mysql -u root -p
```

Use database

``` sql
USE springdb;
```

View users

``` sql
SELECT * FROM users;
```

------------------------------------------------------------------------

# Cache Flow

## First Request

    Client
       |
    Spring Boot
       |
    Redis (MISS)
       |
    MySQL
       |
    Store in Redis
       |
    Response

## Second Request

    Client
       |
    Spring Boot
       |
    Redis (HIT)
       |
    Response

------------------------------------------------------------------------

# RabbitMQ Integration

## Architecture

    Client
       |
    POST /users
       |
    Spring Boot Producer
       |
    RabbitTemplate
       |
    RabbitMQ Queue (user.queue)
       |
    @RabbitListener
       |
    Spring Boot Consumer
       |
    MySQL

## RabbitMQ Configuration

``` yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## Queue

    user.queue

## Message Flow

1.  Client calls **POST /users**
2.  Controller invokes `UserProducer`
3.  Producer publishes the `User` as JSON.
4.  RabbitMQ stores the message.
5.  `@RabbitListener` consumes the message.
6.  Consumer persists the user into MySQL.

## RabbitMQ Management

Management UI:

    http://localhost:15672

Default credentials:

    guest / guest

Navigate to **Queues and Streams** to inspect or purge `user.queue`.

## Producer

    POST /users

Publishes a message and returns immediately.

## Consumer

``` java
@RabbitListener(queues = "user.queue")
```

Consumes messages asynchronously and saves them to MySQL.

## Notes

-   Use `Jackson2JsonMessageConverter` instead of Java serialization.
-   Purge the queue after changing serialization format.
-   RabbitMQ decouples the API from the database and enables
    asynchronous processing.

------------------------------------------------------------------------

# Useful Docker Commands

``` bash
docker ps
```

``` bash
docker logs springboot-app
```

``` bash
docker logs redis-cache
```

``` bash
docker logs mysql-db
```

Stop

``` bash
docker compose down
```

Restart

``` bash
docker compose up -d
```
