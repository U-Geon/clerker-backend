## 📍 Project 소개
Clerker [AI를 활용한 회의 지원 솔루션 플랫폼]

2024 AI빅데이터융합경영학과 인공지능학회 X:AI x 소프트웨어학부 웹 학술동아리 WINK 협업 프로젝트

## 배경 및 목적
**배경**
- 특정 커뮤니티 내에서 회의를 진행할 때 회의 내용을 정리하는데 드는 비용 (시간, 노력)이 크다.
- 회의 정리 시 발언했던 내용들이 정확하게 정리되지 않고 정보가 누락될 수 있다.

**목적**
- 회의 진행 시 회의 전체적인 내용을 요약해 한 눈에 볼 수 있게 한다.
- 회의에 참여하지 못한 팀원들도 해당 회의의 전체적인 내용을 확인할 수 있게 한다.

## 📍 기술 스택
- java : 17
- Spring boot : 3.3.2
- build : Gradle
- DB : MySQL
- Cloud
    - AWS EC2
    - AWS S3
    - Docker
- CI / CD : GitHub Actions, self-hosted Runner
- Collaboration : Notion, Swagger-ui

## 🗓️ 개발 기간
2024.07 ~ 2024.11

## 👨🏻‍💻 팀원
- [20203119 이정욱](https://github.com/ukly)
- [20203103 류건](https://github.com/U-Geon)
- [20213071 장민우](https://github.com/alsdn4956)


## 📍 사용 라이브러리
- `org.springframework.boot:spring-boot-starter-data-jpa`
- `org.springframework.boot:spring-boot-starter-security`
- `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0` -> swagger-ui
- `org.springframework.boot:spring-boot-starter-validation` 
- `com.auth0:java-jwt:4.4.0` -> jwt
- ```org.springframework.boot:spring-boot-starter-data-redis``` -> redis
- ```org.springframework.cloud:spring-cloud-starter-aws``` -> aws-cli
- ```io.awspring.cloud:spring-cloud-starter-aws:2.4.4``` -> aws sagemaker 
- ```com.amazonaws:aws-java-sdk-sagemakerruntime:1.12.762``` -> aws sagemaker 
- ```software.amazon.awssdk:s3:2.28.29``` -> aws S3
- ```software.amazon.awssdk:sts:2.28.29``` -> aws S3

## 📍 ERD 및 WireFrame
<img width="1446" alt="image" src="https://github.com/user-attachments/assets/f93e3c49-501c-45c5-9ba8-d5128c819279">
<img width="1172" alt="image" src="https://github.com/user-attachments/assets/f561e163-3fe9-4911-a354-84af3b91afb0">

## 🤖 모델 서빙 동작 과정
1. SageMaker Endpoint 호출
2. 모델 서버에서 모델 결괏값을 S3에 저장
3. 저장된 URL을 웹서버에 전송 (images.zip + 요약텍스트.md + rawText.txt)
4. 웹서버에서 해당 zip 파일을 압축 해제하여 S3에 저장 및 Meeting File 엔티티 생성


## 📍 상세 기술
### 1. Auth
Spring Security + JWT + OAuth2 라이브러리를 활용하여 구글 소셜 로그인 구현.

구글 로그인 성공 시 AccessToken을 쿼리 파라미터에 담아서 전송
(보안 상 좋지 않기에 추후 리팩토링 필요)

### 2. Project
Project에 대한 CRUD API 작성
- 프로젝트 생성
- 하위 프로젝트 생성
- 프로젝트 삭제 (soft delete -> isDeleted 컬럼)
- 프로젝트명 업데이트

**프로젝트 내 멤버 (Organization)** 에 대한 CRUD API 작성
- 프로젝트 owner 생성
- 멤버 초대
- 특정 회원의 전체 프로젝트 목록
- 프로젝트 이름 + 소속 멤버들 정보
- 프로젝트 내 멤버들 조회
- 멤버들 정보 수정
- 프로젝트 나가기

### 3. Schedule
회의 일정 스케쥴링 기능 CRUD API 작성
- 스케쥴 생성
- project ID를 통한 스케쥴 전체 조회
- 개인별 스케쥴 기록 생성.
- 해당 스케쥴 상세 조회


### 4. Meeting
- Google Calendar API를 활용하여 Google Meet 생성 로직 구현.
- Meeting 도메인에 대한 CRUD API 구현
  - project ID를 통한 미팅 목록 조회
  - 미팅 파일 목록 조회

### 5. Notification
프로젝트 및 스케쥴링 생성에 대한 알림 기능 API 생성

### 6. Model Serving
AWS Sagemaker의 비동기적 추론 방법을 사용하여 기능을 구현하려고 했지만, 프로젝트 마감 기한에 맞추지 못하여 임시 방편으로 was 서버와 sagemaker가 서로 API를 호출하는 식으로 비동기 처리를 구현하였습니다. 
-> 세이지 메이커 엔드포인트 호출
-> 모델링 진행 중일 때는 미팅 결과 상태를 PENDING (요약 파일 생성 중) 으로 변경. 

### 7. Global
- Organization의 권한(OWNER, MEMBER)에 대한 API Authorization을 설정하기 위한 AOP 생성
- Error Custom Exception을 생성하여 에러 커스터마이징 구현
- Front-End 와의 협업을 위한 Swagger Configuration 구현
- 


## 📍 프로젝트 설치 및 실행 방법

1. 리포지토리를 클론합니다.

``` 
$ git clone https://github.com/U-Geon/clerker-backend.git
```

2. 각 `application-*.yml` 파일들을 생성해줍니다.

- `main/resource/application-aws.yml`
```yaml
cloud:
  aws:
    credentials:
      accessKey: ${accessKey}
      secretKey: ${secretKey}
    s3:
      bucket: ${s3.bucket}
    region: ${region}

other:
  aws:
    s3:
      bucket: ${sagemaker.name}
    region: ${sagemaker.region}
    access-key: ${sagemaker.accessKey}
    secret-key: ${sagemaker.secretKey}
    sagemaker:
      endpoint: ${sagemaker.endpoint}
```

- `main/resource/application-aws.yml`
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ${db.url}
    username: ${db.username}
    password: ${db.password}
    hikari:
      maximum-pool-size: 20
  jpa:
    database: mysql
    hibernate:
      ddl-auto: update # create none update
    properties:
      hibernate:
        format_sql: true
  data:
    redis:
      host: ${redis.url}
      port: 6379
      password: ${redis.password}


logging:
  level:
    org.hibernate.SQL: debug
```

- `main/resource/application-jwt.yml`
```yaml
jwt:
  secret-key: ${jwt.secretKey}
  access-token:
    expiration: 12
  refresh-token:
    expiration: 24
```

- `main/resource/application-oauth.yml`
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${client.id}
            client-secret: ${client.secret}
            redirect-uri: ${redirectURL}
            client-name: Clerker
            scope:
              - email
              - profile
              - https://www.googleapis.com/auth/calendar
```

- `main/resource/application-swagger.yml`
```yaml
springdoc:
  swagger-ui:
    path: /swagger # 스웨거 접근 경로
    groups-order: DESC # API 그룹 표시 순서
    tags-sorter: alpha # 태그 정렬 순서.
    operationsSorter: method # 컨트롤러 정렬 순서
    disable-swagger-default-url: true # swagger-ui default url인 petstore html의 비활성화 설정
    display-request-duration: true
  api-docs:
    path: /api-docs # openAPI 접근 경로. default 값은 /v3/api-docs 이다.
  show-actuator: true # Spring Actuator의 endpoint까지 보여줄 것인지?
  default-consumes-media-type: application/json # request media type 의 기본 값
  default-produces-media-type: application/json # response media type 의 기본 값
  paths-to-match: /api/** # 해당 패턴에 매칭되는 controller만 swagger-ui에 노출한다.

baseUrl:
  server: ${baseURL.server}
  model: ${baseURL.model}
  front: https://clerker.vercel.app
```

3. 프로젝트를 실행합니다.
