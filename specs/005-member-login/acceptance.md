# 005 로그인 인수 조건

## 로그인 성공 조건

- 가입된 이메일과 올바른 비밀번호로 `POST /auth/login`을 호출하면 `200 OK`를 반환한다.
- 응답에는 `tokenType`, `accessToken`, `refreshToken`, `expiresIn`이 포함된다.
- `tokenType`은 `Bearer`다.
- `expiresIn`은 `900`이다.
- 발급된 `accessToken`은 15분 뒤 만료된다.
- 발급된 `refreshToken`은 14일 뒤 만료된다.
- Redis에는 `refresh-token:{memberId}` key로 refreshToken 원문이 저장된다.
- Redis key의 TTL은 refreshToken 만료 시간과 동일한 14일이다.

## 로그인 실패 조건

- 존재하지 않는 이메일로 로그인하면 `401 Unauthorized`와 `INVALID_CREDENTIALS`를 반환한다.
- 비밀번호가 틀리면 `401 Unauthorized`와 `INVALID_CREDENTIALS`를 반환한다.
- 응답 메시지는 이메일 존재 여부를 드러내지 않는다.
- 잘못된 JSON 요청은 기존 전역 정책대로 `400 Bad Request`와 `MALFORMED_JSON`을 반환한다.

## 토큰 재발급 조건

- 유효한 refreshToken으로 `POST /auth/refresh`를 호출하면 새 `accessToken`과 새 `refreshToken`을 반환한다.
- refresh 성공 시 Redis의 기존 refreshToken 값은 새 refreshToken으로 교체된다.
- 만료된 refreshToken은 `401 Unauthorized`와 `INVALID_REFRESH_TOKEN`을 반환한다.
- Redis에 저장된 값과 요청 refreshToken이 다르면 `401 Unauthorized`와 `INVALID_REFRESH_TOKEN`을 반환한다.
- accessToken을 refresh API에 보내면 `401 Unauthorized`와 `INVALID_REFRESH_TOKEN`을 반환한다.

## 로그아웃 조건

- `POST /auth/logout` 호출 시 Redis에 저장된 refreshToken을 삭제한다.
- 로그아웃 성공 응답은 `204 No Content`다.
- 로그아웃 이후 같은 refreshToken으로 토큰 재발급을 요청하면 `401 Unauthorized`와 `INVALID_REFRESH_TOKEN`을 반환한다.
- 이미 만료되었거나 저장소에 없는 refreshToken으로 로그아웃을 요청해도 `204 No Content`를 반환한다.

## 완료 기준

- 로그인, 토큰 재발급, 로그아웃 API 명세가 문서화되어 있다.
- accessToken과 refreshToken의 만료 시간, 저장 위치, 회전 정책이 명확하다.
- Redis refreshToken 저장 key, value, TTL 정책이 명확하다.
- 향후 구현 작업이 `tasks.md`에 체크박스로 정리되어 있다.

