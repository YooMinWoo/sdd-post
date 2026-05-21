# 019 EC2 Docker Compose 배포 작업

## 문서화

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `docs/DEPLOYMENT.md` 작성
- [x] `docs/INDEX.md` 갱신

## 구현

- [x] `Dockerfile` 추가
- [x] `.dockerignore` 추가
- [x] `docker-compose.prod.yml` 추가
- [x] `env/.env.prod.example` 추가
- [x] `application.yml` datasource/JPA 운영 환경 변수 설정 추가
- [x] `.github/workflows/deploy.yml` 추가

## 검증

- [x] `.\gradlew.bat test` 실행
- [x] Docker Compose 설정 검증
