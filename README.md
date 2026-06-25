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

## Tech Stack

-   Spring Boot
-   Spring Data JPA
-   MySQL
-   Redis
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

## 2. Get User

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

## 3. Get All Users

**GET** `/users`

``` bash
curl http://localhost:8080/users
```

------------------------------------------------------------------------

## 4. Delete User

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
