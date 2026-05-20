# 008 게시글 작성 인증과 한글 에러 응답 명세

## 목표

게시글 작성 API는 로그인한 회원만 사용할 수 있게 하고, API 실패 응답은 사용자가 이해할 수 있는 한글 메시지를 제공한다.

## 사용자 이야기

회원은 로그인 후 자신의 계정으로 게시글을 작성하고 싶다. API 사용자는 실패 원인을 안정적인 에러 코드로 분기하면서도, 화면에 표시할 수 있는 한글 메시지를 함께 받고 싶다.

## 범위

- `POST /posts`는 `Authorization: Bearer <accessToken>` 헤더가 필요한 인증 API로 변경한다.
- 게시글 작성자는 요청 본문이 아니라 인증된 회원 정보에서 결정한다.
- accessToken의 `sub` 클레임을 게시글 작성자의 회원 id로 사용한다.
- 게시글에는 작성자 닉네임이 아니라 `authorMemberId`를 저장한다.
- 응답에서 작성자 표시가 필요한 경우 저장된 `authorMemberId`로 현재 회원 닉네임을 조회해 제공한다.
- 실패 응답은 영문 `code`와 한글 `message`를 함께 제공한다.
- 인증 토큰 누락, 잘못된 accessToken, 만료된 accessToken은 `401 Unauthorized`로 응답한다.
- 기존 게시글 제목, 본문 검증 실패도 한글 `message`를 포함한다.

## 비범위

- 권한/역할 기반 인가
- 게시글 수정, 삭제, 조회 인증 정책
- request body의 `author` 값을 신뢰하는 방식 유지
- 작성 당시 닉네임 스냅샷 저장
- refreshToken을 게시글 작성 인증에 사용하는 방식
- 다국어 메시지 선택 정책
- 프론트엔드 화면 처리

## 도메인 규칙

- 게시글은 반드시 인증된 회원에 의해 생성된다.
- 작성자는 인증된 회원의 id로 저장한다.
- 닉네임은 변경 가능한 표시 값이므로 게시글의 작성자 식별 값으로 저장하지 않는다.
- 요청 본문에 `author`나 닉네임이 포함되더라도 저장 작성자를 결정하지 않는다.
- accessToken은 `tokenType=ACCESS`인 JWT만 허용한다.
- 만료되었거나 서명이 올바르지 않거나 `tokenType`이 다른 토큰은 유효하지 않은 accessToken으로 본다.

## API

```http
POST /posts
Authorization: Bearer access.jwt.token
Content-Type: application/json
```

요청:

```json
{
  "title": "안녕하세요",
  "content": "첫 번째 게시글입니다."
}
```

성공 응답:

```http
201 Created
```

```json
{
  "success": true,
  "message": "게시글이 생성되었습니다.",
  "data": {
    "id": 1,
    "title": "안녕하세요",
    "content": "첫 번째 게시글입니다.",
    "authorMemberId": 1,
    "author": "minu",
    "createdAt": "2026-05-20T00:00:00Z"
  },
  "path": null,
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

인증 실패 응답:

```http
401 Unauthorized
```

```json
{
  "success": false,
  "code": "UNAUTHORIZED",
  "message": "로그인이 필요합니다.",
  "data": null,
  "path": "/posts",
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

잘못된 accessToken 응답:

```http
401 Unauthorized
```

```json
{
  "success": false,
  "code": "INVALID_ACCESS_TOKEN",
  "message": "유효하지 않은 accessToken입니다.",
  "data": null,
  "path": "/posts",
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

검증 실패 응답:

```http
400 Bad Request
```

```json
{
  "success": false,
  "code": "POST_TITLE_REQUIRED",
  "message": "게시글 제목은 필수입니다.",
  "data": null,
  "path": "/posts",
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

## 에러 응답 규칙

- 실패 응답의 `code`는 안정적인 영문 에러 코드다.
- 실패 응답의 `message`는 사용자에게 표시 가능한 한글 문구다.
- `errors` 배열의 필드별 오류도 한글 `message`와 필요한 경우 영문 `code`를 함께 사용할 수 있다.
- 에러 코드 enum의 `description()`은 기본 한글 메시지로 사용한다.

## Lombok 사용 규칙

- Domain Model에는 `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`를 사용하지 않는다.
- Domain Model은 정적 팩터리와 private constructor로 불변식 검증을 강제한다.
- JPA Entity에는 필요한 경우 `@NoArgsConstructor(access = PROTECTED)`처럼 JPA 요구사항을 만족하는 제한적 Lombok만 사용한다.
- JPA Entity에도 `@AllArgsConstructor`, `@Builder`는 기본 도입하지 않고, 기존 private constructor와 `from(...)`, `toDomain()` 변환 방식을 유지한다.
