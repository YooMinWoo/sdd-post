# EC2 Docker Compose 배포 가이드

이 문서는 EC2 인스턴스 1대에서 Spring Boot 애플리케이션, PostgreSQL, Redis를 Docker Compose로 실행하는 절차를 설명한다.

## 현재 상태

현재 저장소에는 배포 준비 파일만 포함되어 있으며, 실제 AWS 리소스는 아직 생성하지 않는다.

- EC2 생성 보류
- Elastic IP 생성 보류
- 보안 그룹 생성 보류
- 실제 운영 배포 보류
- GitHub Actions 배포 워크플로는 수동 실행(`workflow_dispatch`) 전용

배포를 시작할 때만 아래 절차에 따라 EC2와 GitHub Secrets를 준비한 뒤 GitHub Actions에서 `Deploy` 워크플로를 수동 실행한다.

## 구성

```text
EC2
├─ app: Spring Boot
├─ postgres: PostgreSQL
└─ redis: Redis
```

- 외부 노출 포트: `APP_PORT` 기본값 `8080`
- 앱 내부 DB 주소: `jdbc:postgresql://postgres:5432/post`
- 앱 내부 Redis 주소: `redis:6379`
- PostgreSQL 데이터: `postgres_data` Docker volume

## EC2 준비

Ubuntu 계열 EC2에서 Docker와 Compose plugin을 설치한다.

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo usermod -aG docker $USER
```

설치 후 SSH 세션을 다시 접속한다.

## 서버 디렉터리

예시 배포 경로는 `/opt/post`다.

```bash
sudo mkdir -p /opt/post/env
sudo chown -R $USER:$USER /opt/post
```

서버에는 아래 파일이 있어야 한다.

- `/opt/post/docker-compose.prod.yml`
- `/opt/post/env/.env.prod`

## 환경 변수

`env/.env.prod.example`을 참고해 EC2 서버에 `/opt/post/env/.env.prod`를 만든다.

운영 비밀값은 Git에 커밋하지 않는다.

필수 값:

- `APP_IMAGE`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_REDIS_HOST`
- `SPRING_DATA_REDIS_PORT`
- `JWT_ACCESS_TOKEN_SECRET`
- `JWT_REFRESH_TOKEN_SECRET`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`

## 최초 실행

```bash
cd /opt/post
docker compose -f docker-compose.prod.yml --env-file env/.env.prod pull
docker compose -f docker-compose.prod.yml --env-file env/.env.prod up -d
docker compose -f docker-compose.prod.yml --env-file env/.env.prod ps
```

## 확인

```bash
docker compose -f docker-compose.prod.yml --env-file env/.env.prod logs -f app
curl http://localhost:8080/posts?page=0&size=10
```

Swagger UI:

```text
http://EC2_PUBLIC_IP:8080/swagger-ui/index.html
```

## 재시작

```bash
cd /opt/post
docker compose -f docker-compose.prod.yml --env-file env/.env.prod restart app
```

## 롤백

GHCR에 남아 있는 이전 SHA 태그로 `APP_IMAGE`를 바꾼 뒤 재기동한다.

```bash
cd /opt/post
vi env/.env.prod
docker compose -f docker-compose.prod.yml --env-file env/.env.prod pull app
docker compose -f docker-compose.prod.yml --env-file env/.env.prod up -d app
```

## GitHub Actions Secrets

저장소 secrets에 아래 값을 등록한다.

- `VPS_HOST`: EC2 public IP 또는 DNS
- `VPS_USER`: SSH 사용자명
- `VPS_SSH_KEY`: private key
- `VPS_DEPLOY_PATH`: 예시 `/opt/post`

GHCR push는 기본 `GITHUB_TOKEN` 권한을 사용한다.

`Deploy` 워크플로는 자동 실행되지 않는다. GitHub Actions 화면에서 수동으로 실행해야 한다.

## 후속 과제

- 도메인 연결
- HTTPS와 Nginx reverse proxy 구성
- Flyway 마이그레이션 도입
- DB 백업 정책 수립
- 배포를 실제로 시작할 때 `main` push 자동 배포 여부 재검토
