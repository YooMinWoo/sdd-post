# 020 프론트엔드 웹 클라이언트 계획

## 설계 방향

프론트엔드는 백엔드와 분리된 React SPA로 구성한다. 백엔드는 API 계약을 유지하고, 로컬 프론트 개발 서버에서 API를 호출할 수 있도록 CORS만 추가한다.

## 프론트엔드 구조

`post-front` 저장소의 주요 구조는 다음과 같다.

```text
src/
  api/
  auth/
  components/
  pages/
  styles/
  test/
```

- `api`: HTTP 클라이언트, API 타입, 게시글/댓글/인증 API 함수
- `auth`: 토큰 저장소와 인증 컨텍스트
- `components`: 공통 레이아웃, 버튼, 입력, 상태 표시 컴포넌트
- `pages`: 라우트 단위 화면
- `styles`: 전역 스타일
- `test`: 테스트 유틸리티

## 라우팅

- `/`: 게시글 목록과 검색
- `/posts/:postId`: 게시글 상세와 댓글
- `/posts/new`: 게시글 작성
- `/posts/:postId/edit`: 게시글 수정
- `/login`: 로그인
- `/signup`: 회원가입

## 인증 흐름

- 로그인 성공 시 accessToken, refreshToken, tokenType, expiresIn을 저장한다.
- API 요청에는 accessToken을 `Authorization` 헤더에 담는다.
- 인증 API를 제외한 요청에서 401이 발생하면 refreshToken으로 `/auth/refresh`를 1회 호출한다.
- refresh가 성공하면 원래 요청을 재시도한다.
- refresh가 실패하면 저장된 토큰을 제거하고 로그인 상태를 해제한다.

## 백엔드 변경

- `SecurityConfig`에 CORS 설정을 추가한다.
- 허용 Origin은 `app.cors.allowed-origins` 설정값으로 관리한다.
- 기본 허용 Origin은 `http://localhost:5173`이다.
- 허용 메서드는 `GET`, `POST`, `PATCH`, `DELETE`, `OPTIONS`이다.
- 허용 헤더는 `Authorization`, `Content-Type`이다.

## 테스트 전략

- 백엔드는 CORS preflight 요청에 허용 Origin 헤더가 내려오는지 검증한다.
- 프론트엔드는 API 클라이언트, 토큰 refresh 흐름, 주요 화면 렌더링을 테스트한다.
- 수동 검증은 Spring Boot API와 Vite 개발 서버를 함께 실행해 핵심 사용자 흐름을 확인한다.
