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
- [x] 배포 워크플로를 수동 실행 전용으로 전환

## 검증

- [x] `.\gradlew.bat test` 실행
- [x] Docker Compose 설정 검증

## 후속 수동 작업

- [ ] EC2 생성
- [ ] Elastic IP 또는 퍼블릭 IP 정책 결정
- [ ] 보안 그룹 생성
- [ ] EC2 Docker/Compose 설치
- [ ] EC2 `/opt/post/env/.env.prod` 작성
- [ ] GitHub Secrets 등록
- [ ] GitHub Actions `Deploy` 워크플로 수동 실행
