# EXPLORE WITH ME
##### Приложение предоставляет возможность делиться информацией об интересных событиях и помогать найти компанию для участия в них.

[Pull-request] (https://github.com/SirBogdan/java-explore-with-me/pull/2)

# Используемые технологии
* Java 11, Lombok;
* Spring Boot
* PostgreSQL, SQL;
* Hibernate, JPA, JPQL;
* Docker;
* Maven (multi-module project);
* Swagger;
* Postman.

# Инструкция (команды) по запуску приложения

1. git clone https://github.com/SirBogdan/java-explore-with-me.git
2. mvn package
3. docker-compose up

# Архитектура приложения

![ewm architecture](https://user-images.githubusercontent.com/100284165/205351591-40b3031f-b4be-425d-8dd0-c633783989fc.png)

# Компоненты приложения

### ewm-service
##### PORTS: 8080:8080
Реализует основную бизнес-логику приложения. API подразделяется на три части:
1. **Public** - содержит логику для работы с приложением для любого, неавторизованного пользователя.
2. **Private** - содержит логику для работы с авторизованными пользователями.
3. **Admin** - содержит логику работы в роли администратора ресурса

### ewm-database
##### PORTS: 6541:5432

<img width="324" alt="ewm-service database scheme" src="https://user-images.githubusercontent.com/100284165/205351666-c23cbad9-2a65-4fd9-adde-d57063d639d5.PNG">


### stats-server
##### PORTS: 9090:9090
Сервис собирает информацию о количестве обращений пользователей к спискам событий и о количестве запросов к подробной
информации о событии. На основе этой информации формируется статистика о работе приложения.

### stats-database
##### PORTS: 6542:5432
<img width="133" alt="stats-server database scheme" src="https://user-images.githubusercontent.com/100284165/205351696-69e49d11-701b-43f0-8fc3-6ed80c3348de.PNG">


