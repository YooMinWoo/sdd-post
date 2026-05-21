# 019 EC2 Docker Compose 배포 인수 조건

## 문서 완료 조건

- EC2 1대에서 `app`, `postgres`, `redis` 컨테이너를 실행한다고 명시되어 있다.
- EC2 준비, Docker 설치, `.env.prod` 작성, Compose 실행 절차가 문서화되어 있다.
- GitHub Actions 배포에 필요한 secrets가 문서화되어 있다.
- 로그 확인, 재시작, 롤백 절차가 문서화되어 있다.

## 기능 인수 조건

- Dockerfile로 애플리케이션 이미지를 빌드할 수 있다.
- `docker-compose.prod.yml`은 `app`, `postgres`, `redis` 서비스를 포함한다.
- PostgreSQL 데이터는 Docker volume에 저장된다.
- Redis는 앱 컨테이너에서 Compose 서비스명 `redis`로 접근할 수 있다.
- 앱 컨테이너는 환경 변수로 datasource, Redis, JWT 설정을 주입받는다.
- GitHub Actions는 `main` push 시 테스트, 이미지 빌드, GHCR push, EC2 재배포 단계를 수행한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
- Docker가 설치된 환경에서는 `docker compose -f docker-compose.prod.yml --env-file env/.env.prod.example config`가 성공한다.
