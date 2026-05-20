# 005 로그인 계획

## 아키텍처

로그인은 `member` bounded context에 구현한다. JWT 생성/검증과 Redis 저장은 외부 기술이므로 application 포트로 분리하고 adapter에서 구현한다.

## 애플리케이션

- 입력 포트:
  - `LoginUseCase`
  - `RefreshTokenUseCase`
  - `LogoutUseCase`
- 입력 커맨드:
  - `LoginCommand(email, password)`
  - `RefreshTokenCommand(refreshToken)`
  - `LogoutCommand(refreshToken)`
- 출력 결과:
  - `TokenResult(tokenType, accessToken, refreshToken, expiresIn)`
- 애플리케이션 서비스:
  - `LoginService`
  - `RefreshTokenService`
  - `LogoutService`

## 출력 포트

- `MemberRepositoryPort`
  - 이메일로 회원을 조회하는 메서드를 추가한다.
- `PasswordMatcherPort`
  - 평문 비밀번호와 저장된 BCrypt 해시 일치 여부를 확인한다.
- `TokenProviderPort`
  - accessToken과 refreshToken을 발급한다.
  - refreshToken을 검증하고 회원 id를 추출한다.
- `RefreshTokenStorePort`
  - Redis에 refreshToken을 저장한다.
  - Redis에 저장된 refreshToken을 조회한다.
  - Redis에서 refreshToken을 삭제한다.

## 어댑터

- Web 어댑터:
  - `AuthController`에 로그인, 토큰 재발급, 로그아웃 API를 추가한다.
  - 요청 DTO: `LoginRequest`, `RefreshTokenRequest`, `LogoutRequest`
  - 응답 DTO: `TokenResponse`
- Security 어댑터:
  - `BCryptPasswordEncoderAdapter`가 비밀번호 일치 여부도 제공하도록 확장한다.
  - JWT 생성/검증 어댑터를 추가한다.
- Redis 어댑터:
  - `RefreshTokenRedisAdapter`를 추가한다.
  - key는 `refresh-token:{memberId}`를 사용한다.
  - value는 refreshToken 원문을 저장한다.
  - TTL은 14일로 설정한다.

## 예외와 에러 코드

- 로그인 실패:
  - 예외: `InvalidCredentialsException`
  - HTTP 상태: `401 Unauthorized`
  - 에러 코드: `INVALID_CREDENTIALS`
- refreshToken 실패:
  - 예외: `InvalidRefreshTokenException`
  - HTTP 상태: `401 Unauthorized`
  - 에러 코드: `INVALID_REFRESH_TOKEN`
- 전역 예외 처리에서 위 예외를 구조화된 `ErrorResponse`로 변환한다.

## 의존성 및 설정

향후 구현 시 다음 의존성을 추가한다.

- Redis:
  - Spring Data Redis starter
- JWT:
  - JWT 생성/검증 라이브러리

향후 설정값:

```yaml
jwt:
  access-token:
    secret: ${JWT_ACCESS_TOKEN_SECRET}
    expiration-seconds: 900
  refresh-token:
    secret: ${JWT_REFRESH_TOKEN_SECRET}
    expiration-seconds: 1209600
spring:
  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST}
      port: ${SPRING_DATA_REDIS_PORT}
```

## 패키지 배치

```text
com.example.post.member.application.exception
com.example.post.member.application.port.in
com.example.post.member.application.port.out
com.example.post.member.application.service
com.example.post.member.adapter.in.web
com.example.post.member.adapter.out.security
com.example.post.member.adapter.out.redis
com.example.post.global.web
com.example.post.global.config
```

## 테스트 전략

- Application Test:
  - 로그인 성공
  - 이메일이 없거나 비밀번호가 틀리면 동일하게 `InvalidCredentialsException`
  - refresh 성공 시 기존 refreshToken을 새 refreshToken으로 교체
  - 저장된 refreshToken과 요청 refreshToken이 다르면 `InvalidRefreshTokenException`
  - 로그아웃 시 Redis 저장소에서 refreshToken 삭제
- Web Adapter Test:
  - `/auth/login` 성공과 실패 응답
  - `/auth/refresh` 성공과 실패 응답
  - `/auth/logout` 성공 응답
- Redis Adapter Test:
  - refreshToken 저장, 조회, 삭제, TTL 설정
- Security Adapter Test:
  - JWT 발급, claims 검증, 만료/타입 불일치 검증

