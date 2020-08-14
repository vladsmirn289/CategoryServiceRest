[![Build Status](https://travis-ci.org/vladsmirn289/CategoryServiceRest.svg?branch=master)](https://travis-ci.org/github/vladsmirn289/CategoryServiceRest)
[![BCH compliance](https://bettercodehub.com/edge/badge/vladsmirn289/CategoryServiceRest?branch=master)](https://bettercodehub.com/)
# Category service

## About
This is the category service for goods-shop-rest project. This service allows retrieve, save or delete categories.

## If you find a bug, or you have any suggestions
You can follow the next link and describe your problem/suggestion: https://github.com/vladsmirn289/CategoryServiceRest/issues

## Running
If you want to run this service separately, you can run it from your IDE, or use the next command:
```shell script
docker-compose up
```
from a root folder of the project. However, docker-compose.yml file start the postgreSQL database on the port
5432, you can add the next option to postgreSQL service in the docker-compose.yml:
```shell script
network_mode: "host"
```
You also need to have the shop_db database.

## Using
After start, you can use a **[postman]** application or follow the next link http://localhost:8084/categories-rest-swagger.
The second variant is the swagger documentation, where you also can perform any http requests (GET, POST, PUT, etc.).
The first variant is more universal method with using third-party application.

## Package structure
The diagram of the package structure:
*   GoodsShop
    *   data (database schema and data for separately running)
    *   logs
    *   src
        *   [main]
            *   [java]
                *   [com.shop.AuthenticationService]
                    *   [Config] (Swagger config)
                    *   [Controller] (CategoryController that manages categories, and RootController for swagger)
                    *   [Jackson] (Serializers and deserializers rof category and items classes)
                    *   [Model] (JPA entities)
                    *   [Repository] (Spring Data repos)
                    *   [Service] (Service for finding client by a login)
                    *   [CategoryServiceRestApp.java] (Main class for, Spring Boot)
            *   [resources]
                *   [application.properties] (Stores various properties of the database, JWT and swagger)
                *   [log4j2.xml] (Stores log4j2 properties)
        *   [test]
            *   [java][java2]
                *   [com.shop.CategoryServiceRest][comInTest]
                    *   [Controller][ControllerTest]
                    *   [DTO][DTOTest]
                    *   [Model][ModelTest]
                    *   [Repository][RepoTest]
                    *   [Service][ServiceTest]
                    *   [docker-compose-test.yml]
            *   [resources][testRes]
                *   [db][testDb]
                    *   [PostgreSQL] (H2 scripts for tests)
                *   [application.properties][application-test.properties] (Various properties for test environment)

## License
Authentication service is the service released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

[goods-shop-rest]: https://github.com/vladsmirn289/GoodsShopRest
[postman]: https://www.postman.com/

[main]: ./src/main
[java]: ./src/main/java
[com.shop.AuthenticationService]: ./src/main/java/com/shop/CategoryServiceRest
[Config]: ./src/main/java/com/shop/CategoryServiceRest/Config
[Controller]: ./src/main/java/com/shop/CategoryServiceRest/Controller
[Jackson]: ./src/main/java/com/shop/CategoryServiceRest/Jackson
[Model]: ./src/main/java/com/shop/CategoryServiceRest/Model
[Repository]: ./src/main/java/com/shop/CategoryServiceRest/Repository
[Service]: ./src/main/java/com/shop/CategoryServiceRest/Service
[CategoryServiceRestApp.java]: ./src/main/java/com/shop/CategoryServiceRest/CategoryServiceRestApp.java

[resources]: ./src/main/resources
[application.properties]: ./src/main/resources/application.properties
[log4j2.xml]: ./src/main/resources/log4j2.xml

[test]: ./src/test
[testRes]: ./src/test/resources
[testDb]: ./src/test/resources/db
[PostgreSQL]: ./src/test/resources/db/PostgreSQL
[application-test.properties]: ./src/test/resources/application.properties
[java2]: ./src/test/java
[comInTest]: ./src/test/java/com/shop/CategoryServiceRest
[ControllerTest]: ./src/test/java/com/shop/CategoryServiceRest/Controller
[DTOTest]: ./src/test/java/com/shop/CategoryServiceRest/DTO
[ModelTest]: ./src/test/java/com/shop/CategoryServiceRest/Model
[RepoTest]: ./src/test/java/com/shop/CategoryServiceRest/Repository
[ServiceTest]: ./src/test/java/com/shop/CategoryServiceRest/Service
[docker-compose-test.yml]: ./src/test/java/com/shop/CategoryServiceRest/docker-compose-test.yml