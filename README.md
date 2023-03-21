# Tenpo Challenge


### Prerequisites
* Apache Maven
* Docker

### Frameworks & Technologies
* Java 17
* Spring Boot 3.0.2
* PostgreSQL
* H2 Memory Database para ejecución de Test
* Controller advice
* Junit

### Install
```
git clone https://github.com/rsrivero/challenge-tenpo.git
```

### Build
```
mvn clean package
```

### Collection Postman

Import postman collection from:

.../challenge-tenpo/src/main/resources/Tenpo.postman_collection.json

### Execute
```
docker-compose up --build
```