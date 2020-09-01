# sickdan back-end 개발 이슈사항 기록노트

## Docker - Jenkins 배포 자동화 관련
### 최초 시도: 프리티어 과금 문제로 하나의 EC2에 도커로 mariaDB, 스프링 서버, 젠킨스 모두 띄우는 걸 시도.
### 문제: EC2 프리티어는 메모리가 상당히 제한적(젠킨스 빌드에서 OOM 날때도 있음), DB서버의 경우 영속성이 유지되어야함.
### 결국 요금문제 등과 타협해서 AWS RDS(mariaDB) + 로컬PC 도커(젠킨스 + 스프링서버)로 변경.

## ordered list in db 관련

## Exception Handling 관련

## jpa 관련

## JWT 및 OAuth2관련
### OAuth2 소셜 로그인의 몇가지 방식중 implicit 방식과 authorization code 방식을 고민해봄
### 네이버 및 카카오 등에서 기본적으로 안드로이드에 제공하는 방식은 implicit 방식이다.(access token을 바로 리턴함)
### 이 방식 사용 시 client secret key 및 access 토큰이 앱 내부에서 직접 명시되고 사용됨
### 따라서 앱 내부 브라우저를 이용해서 authorization code 방식으로 진행하기로함
### 흐름 : 앱 로그인 -> 네이버 로그인 브라우저 호출 -> 스프링 서버로 Redirect -> 브라우저로 authorization code 
###       -> 앱으로 authorization code -> authorization code로 회원가입 및 로그인

## Service구조 관련

## 테스트코드 관련
### 테스트 단위를 정하는 것이 중요하다.
### 경계가 모호해지면 테스트도 모호해진다.
### 테스트 코드를 작성하다보면 잘못 짠 코드가 보임.
### 의미는 있으나 경험상 장애는 테스트 시나리오가 커버하지 못하는 부분에서 일어난다.
### 1차적 필터 역할, 다른 이에게 레퍼런스 역할이 크다고 보므로 큰 기대와 의지는 금물.
