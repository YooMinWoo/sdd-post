# 006 표준 API 응답과 Swagger 정리 명세

## 목표

컨트롤러에 반복적으로 작성하던 Swagger 응답 문서를 공통 규칙으로 정리하고, API 성공/실패 응답을 표준 `ApiResponse` 구조로 통일한다.

## 사용자 이야기

API 사용자는 모든 응답의 성공 여부, 메시지, 데이터, 에러 정보를 같은 구조로 해석하고 싶다. 개발자는 컨트롤러에 Swagger 응답 예시를 길게 반복하지 않고 API별 문서 어노테이션으로 핵심 설명을 관리하고 싶다.

## 범위

- 성공 응답과 실패 응답에 사용할 공통 `ApiResponse<T>`를 추가한다.
- 기존 `ErrorResponse` 기반 실패 응답을 `ApiResponse<Void>` 실패 응답으로 전환한다.
- 컨트롤러의 긴 Swagger 응답 어노테이션을 API별 문서 어노테이션으로 분리한다.
- 게시글 작성, 회원가입, 로그인, 토큰 재발급 응답을 표준 응답으로 감싼다.
- 로그아웃은 `204 No Content`를 유지하고 본문 없는 성공 응답 예외로 문서화한다.

## 비범위

- Bean Validation 도입
- 응답 코드 체계의 enum 전환
- API 버전 관리
- 배포 환경별 Swagger 접근 제어

## 표준 응답 규칙

- 응답 본문은 `success`, `message`, `data`, `path`, `timestamp`, `errors`를 포함한다.
- 성공 응답은 `success=true`, `data`에 결과 DTO, `errors=[]`를 사용한다.
- 실패 응답은 `success=false`, `data=null`, `path`에 요청 경로, `errors`에 필드 오류 목록을 사용한다.
- 성공 응답의 `message`는 한글 사용자 메시지를 사용한다.
- 실패 응답의 `message`는 `INVALID_REQUEST` 같은 영문 대문자 스네이크 케이스 에러 코드를 사용한다.
- 본문이 없는 `204 No Content` 응답은 표준 응답 본문을 강제하지 않는다.

## Swagger 문서 규칙

- 컨트롤러에는 `@Tag`와 API별 문서 어노테이션만 작성한다.
- API별 `@Operation`, `@ApiResponse`, `@Content`, `@Schema` 조합은 `global.web.swagger` 패키지의 `{Action}ApiDocs` 어노테이션으로 제공한다.
- 요청/응답 DTO의 필드 설명은 각 DTO의 `@Schema`로 유지한다.
- 긴 JSON 예시는 컨트롤러에 직접 작성하지 않는다.
