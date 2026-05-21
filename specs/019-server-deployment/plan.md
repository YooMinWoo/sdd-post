# 019 EC2 Docker Compose 배포 계획

## 설계 방향

단일 EC2 서버에서 Docker Compose로 애플리케이션과 필수 인프라 컨테이너를 함께 실행한다. GitHub Actions는 테스트와 이미지 빌드를 담당하고, EC2에는 SSH로 접속해 최신 이미지를 pull 후 Compose를 재기동한다.

## 배포 산출물

- `Dockerfile`은 Gradle wrapper로 `bootJar`를 빌드하고 JRE 런타임 이미지에서 jar를 실행한다.
- `.dockerignore`는 빌드 산출물, Git, IDE, 로컬 환경 파일을 이미지 컨텍스트에서 제외한다.
- `docker-compose.prod.yml`은 `app`, `postgres`, `redis` 서비스를 정의한다.
- `env/.env.prod.example`은 운영에 필요한 환경 변수 목록과 예시값을 제공한다.
- `docs/DEPLOYMENT.md`는 EC2 준비, 배포, 로그 확인, 재시작, 롤백 절차를 설명한다.

## 애플리케이션 설정

- `application.yml`에 datasource와 JPA 설정을 환경 변수 기반으로 추가한다.
- PostgreSQL 연결 기본값은 Compose 서비스명 `postgres`를 사용한다.
- Redis 연결 기본값은 기존 설정을 유지하되 Compose에서는 `redis`로 주입한다.
- 운영 초기값은 `SPRING_JPA_HIBERNATE_DDL_AUTO=update`를 사용한다.

## CI/CD

- `.github/workflows/deploy.yml`을 추가한다.
- `main` 브랜치 push 시 `./gradlew test`를 실행한다.
- Docker 이미지를 GHCR에 `latest`, commit SHA 태그로 push한다.
- SSH로 EC2의 `VPS_DEPLOY_PATH`에 접속해 `docker compose -f docker-compose.prod.yml pull`과 `up -d`를 실행한다.

## 검증 전략

- 로컬에서 `.\gradlew.bat test`를 실행한다.
- Docker가 사용 가능하면 `docker compose -f docker-compose.prod.yml --env-file env/.env.prod.example config`로 Compose 구문을 검증한다.
- EC2에서는 `docker compose ps`, `docker compose logs -f app`, `GET /posts?page=0&size=10`으로 배포 상태를 확인한다.
