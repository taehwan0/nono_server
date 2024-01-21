<div align="center">
<br>
<img src="https://user-images.githubusercontent.com/81221429/213846984-d902a3b9-4451-4320-b720-1d4104cfb734.png" width="200"/>
<h2>NONO DELUXE</h2>
<h3>화성시니어클럽 노노유통 재고 관리 시스템</h3>
</div>

---

## how to run

### 1. jar 빌드

```shell
./gradlew build -x test
```

### 2. docker image 빌드

```shell
docker build --build-arg JAR_FILE=build/libs/\*.jar -t nono-image .
```

### 3. docker image 실행

```shell
docker rm --force nono-container
docker run -d --name nono-container -p 3000:3000 nono-image
```

---

## 사용 기술

| 종류          | 이름                                                                                     |
|:------------|:---------------------------------------------------------------------------------------|
| 언어          | Java(OpenJDK@11)                                                                       |
| 프레임워크       | SpringBoot 2.7.0                                                                       |
| 빌드 툴        | Gradle                                                                                 |
| 인프라(라이브 서버) | nginx<br/>Ubuntu Linux                                                                 |
| 인프라(테스트 서버) | nginx<br/>Ubuntu Linux<br/>AWS EC2, RDS, S3                                            
| 데이터베이스      | MariaDB                                                                                |
| 주요 라이브러리    | JPA<br/>thymeleaf<br/>JavaMailSender<br/>jwt0<br/>thumbnailator<br/>apache POI<br/>etc |

## 아키텍쳐

<img width="1050" alt="구조도" src="https://user-images.githubusercontent.com/81221429/213877879-6ad0bc51-62c4-4020-bc10-59be26e32e04.png">

## 기능 목록

<img width="756" alt="기능목록1" src="https://user-images.githubusercontent.com/81221429/213879104-f526e28f-eeaa-4549-b18a-acf0cadc4cdc.png">
<img width="756" alt="기능목록2" src="https://user-images.githubusercontent.com/81221429/213879107-0fc6f8f5-d4dc-4040-b94a-c178907c0570.png">
