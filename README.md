# EatTable

Spring Boot로 구현한 음식점 예약 서버입니다.

이 프로젝트는 현재 개발 중에 있으며 개발된 기능만 아래에 기재하였습니다.

+ [EatTable's Message Relay Server](https://github.com/psh3253/EatTableMessageRelay)

### Prerequisites / 선행 조건

아래 사항들이 설치 및 세팅이 되어있어야 합니다.

```
JDK 17 이상, MySQL Server 8.0 이상
```


### 사전 설정
1. application.properties 파일에 아래 내용 추가
```
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url={{MySQL URL}}
spring.datasource.username={{MySQL Username}}
spring.datasource.password={{MySQL Password}}
spring.data.mongodb.uri={{MongoDB URL}}
spring.data.mongodb.database=EatTable
spring.kafka.properties.sasl.mechanism=PLAIN
spring.kafka.bootstrap-servers={{Kafka URL}}
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username={{Kafka Username}} password={{Kafka Password}};
spring.kafka.properties.security.protocol=SASL_SSL
```


## Built With / 누구랑 만들었나요?

* [박세훈](https://github.com/psh3253) - 프로젝트 전체 설계 및 제작

## Function / 기능
+ 로그인 및 회원가입
+ 음식점 조회, 등록, 수정, 삭제
+ 영업시간 수정
+ 메뉴 등록, 수정, 삭제

## Technology / 기술

+ Spring Security를 사용하여 JWT 로그인 구현
+ CQRS 패턴을 적용해 Command와 Query를 분리한 아키텍처 구현
+ Transaction Outbox Pattern을 통해 데이터 일관성 유지 및 이벤트 기반 아키텍처 구축
+ Command와 Query 모델 동기화를 위한 Message Relay Server 구축

## License / 라이센스

이 프로젝트는 MIT 라이센스로 라이센스가 부여되어 있습니다. 자세한 내용은 LICENSE 파일을 참고하세요.
