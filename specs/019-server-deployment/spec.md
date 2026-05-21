# 019 EC2 Docker Compose 배포 명세

## 목표

EC2 인스턴스 1대에서 Docker Compose로 Spring Boot 애플리케이션, PostgreSQL, Redis를 함께 실행할 수 있게 한다.

## 사용자 이야기

운영자는 GitHub `main` 브랜치에 변경이 반영되면 테스트와 이미지 빌드가 자동으로 수행되고, EC2 서버에서 최신 애플리케이션 컨테이너가 재시작되기를 원한다.

## 범위

- EC2 1대에 `app`, `postgres`, `redis` 컨테이너를 실행한다.
- 애플리케이션 이미지는 Dockerfile로 빌드한다.
- 운영 컨테이너 구성은 `docker-compose.prod.yml`로 관리한다.
- 운영 환경 변수 예시는 `env/.env.prod.example`로 제공한다.
- GitHub Actions는 `main` 브랜치 push 시 테스트, Docker 이미지 빌드, GHCR push, EC2 재배포를 수행한다.
- EC2 배포 절차, 로그 확인, 재시작, 롤백 절차를 문서화한다.
- PostgreSQL 데이터는 Docker volume으로 유지한다.

## 비범위

- 도메인 연결
- HTTPS 인증서 발급
- Nginx reverse proxy 구성
- RDS, ElastiCache 등 관리형 서비스 전환
- Flyway 또는 Liquibase 마이그레이션 도입
- 무중단 배포
- 오토스케일링

## 운영 규칙

- 운영 비밀값은 Git에 커밋하지 않는다.
- EC2의 실제 `.env.prod`는 서버에 직접 배치한다.
- JWT secret은 운영 전용 값으로 교체한다.
- 앱 컨테이너는 PostgreSQL과 Redis가 준비된 뒤 기동한다.
- 초기 운영 스키마는 `SPRING_JPA_HIBERNATE_DDL_AUTO=update`로 생성한다.

## 런타임 구성

```text
EC2
├─ app: Spring Boot
├─ postgres: PostgreSQL
└─ redis: Redis
```

외부 요청은 우선 EC2의 `8080` 포트로 앱 컨테이너에 전달한다.
