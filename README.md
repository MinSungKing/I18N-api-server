# I18N-api-server
API server for I18N service

## Requirement
Spring Boot 2.1.7  
MySQL 8.0.11  
Maven  

## Create Table
DB 커넥션 설정은 src/main/resources/application-local.properties 파일에서 수정 가능

```
CREATE SCHEMA workdb;

#key는 MySQL 예약어라서 테이블명으로 사용하기에 무리가 있음
#keyword로 테이블 명명 
CREATE TABLE keyword(
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE translation(
  id int NOT NULL AUTO_INCREMENT,
  key_id int NOT NULL,
  locale VARCHAR(2) NOT NULL,
  value VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (key_id) REFERENCES keyword (id)
);

```


## Run project
```
$ mvn package

$ java -jar target/I18N-API-Server-0.0.1-SNAPSHOT.jar  

```


## API Test setting
### Postman 테스트 기준  
Authorization - Basic Auth  
Username : user001  
Password : password00123  
