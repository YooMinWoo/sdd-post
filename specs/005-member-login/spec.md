# 005 로그인 명세

## 목표

회원이 이메일과 비밀번호로 로그인하고, `accessToken`과 `refreshToken`을 발급받아 인증이 필요한 API를 사용할 수 있게 한다.

## 사용자 이야기

회원은 가입한 이메일과 비밀번호로 로그인해 본인 계정으로 게시판 서비스를 이용하고 싶다.

## 범위

- 이메일과 비밀번호로 로그인한다.
- 로그인 성공 시 `accessToken`과 `refreshToken`을 JSON 응답으로 반환한다.
- `accessToken`은 짧은 만료 시간으로 API 인증에 사용한다.
- `refreshToken`은 Redis에 저장하고 토큰 재발급에 사용한다.
- refresh 요청이 성공하면 `accessToken`과 `refreshToken`을 모두 새로 발급한다.
- 로그아웃 시 Redis에 저장된 `refreshToken`을 삭제한다.
- Swagger에 로그인, 토큰 재발급, 로그아웃 API를 한글로 문서화한다.

## 비범위

- 회원가입 구현 변경
- 이메일 인증
- 비밀번호 재설정
- 소셜 로그인
- 권한/역할 기반 인가
- refreshToken 쿠키 전달
- 다중 기기 세션 관리

## 도메인 규칙

- 로그인 식별자는 이메일을 사용한다.
- 이메일은 앞뒤 공백을 제거하고 소문자로 정규화한 뒤 조회한다.
- 비밀번호 검증은 저장된 BCrypt 해시와 요청 비밀번호를 비교한다.
- 로그인 실패 시 이메일 존재 여부와 비밀번호 불일치 여부를 구분해 노출하지 않는다.
- `accessToken` 만료 시간은 15분이다.
- `refreshToken` 만료 시간은 14일이다.
- Redis에는 `refreshToken` 원문을 저장한다.
- refreshToken 저장 key는 `refresh-token:{memberId}` 형식을 사용한다.
- refresh 성공 시 기존 Redis 값을 새 `refreshToken`으로 교체한다.
- 로그아웃 후 기존 `refreshToken`은 사용할 수 없다.

## API

### 로그인

```http
POST /auth/login
Content-Type: application/json
```

요청:

```json
{
  "email": "minu@example.com",
  "password": "password123"
}
```

성공 응답:

```http
200 OK
```

```json
{
  "tokenType": "Bearer",
  "accessToken": "access.jwt.token",
  "refreshToken": "refresh.jwt.token",
  "expiresIn": 900
}
```

인증 실패 응답:

```http
401 Unauthorized
```

```json
{
  "code": "INVALID_CREDENTIALS",
  "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
  "path": "/auth/login",
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

### 토큰 재발급

```http
POST /auth/refresh
Content-Type: application/json
```

요청:

```json
{
  "refreshToken": "refresh.jwt.token"
}
```

성공 응답:

```http
200 OK
```

```json
{
  "tokenType": "Bearer",
  "accessToken": "new.access.jwt.token",
  "refreshToken": "new.refresh.jwt.token",
  "expiresIn": 900
}
```

refreshToken 실패 응답:

```http
401 Unauthorized
```

```json
{
  "code": "INVALID_REFRESH_TOKEN",
  "message": "유효하지 않은 refreshToken입니다.",
  "path": "/auth/refresh",
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

### 로그아웃

```http
POST /auth/logout
Content-Type: application/json
```

요청:

```json
{
  "refreshToken": "refresh.jwt.token"
}
```

성공 응답:

```http
204 No Content
```

이미 만료되었거나 저장소에 없는 refreshToken으로 로그아웃을 요청해도 `204 No Content`를 반환한다.

## JWT Claims

`accessToken`에는 최소 claims만 포함한다.

| Claim | 설명 |
| --- | --- |
| `sub` | 회원 id |
| `email` | 회원 이메일 |
| `nickname` | 회원 닉네임 |
| `tokenType` | `ACCESS` |

`refreshToken`에는 토큰 재발급에 필요한 최소 claims를 포함한다.

| Claim | 설명 |
| --- | --- |
| `sub` | 회원 id |
| `tokenType` | `REFRESH` |

