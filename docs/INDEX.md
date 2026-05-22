# 문서 색인

이 디렉터리는 프로젝트의 공통 개발 기준과 아키텍처 문서를 관리한다.

## 문서 목록

- [ARCHITECTURE.md](ARCHITECTURE.md): DDD와 헥사고날 아키텍처 기준
- [CONVENTIONS.md](CONVENTIONS.md): 패키지, 명명, 코딩, 문서 작성 규칙
- [TESTING.md](TESTING.md): 테스트 계층과 실행 기준
- [DOMAIN_GLOSSARY.md](DOMAIN_GLOSSARY.md): 게시판 도메인 용어집
- [DEPLOYMENT.md](DEPLOYMENT.md): EC2 Docker Compose 배포 가이드
- [adr/0001-architecture-style.md](adr/0001-architecture-style.md): 아키텍처 스타일 결정 기록

## 기능 명세 위치

기능별 SDD 문서는 `specs/{번호}-{기능명}/` 아래에 둔다.

필수 파일:

- `spec.md`
- `plan.md`
- `tasks.md`
- `acceptance.md`

## 갱신 규칙

- 새 공통 문서를 추가하면 이 파일에 링크를 추가한다.
- 새 ADR을 추가하면 이 파일에 링크를 추가한다.
- 기능 명세는 개별 기능 완료 후 필요한 경우에만 대표 링크를 추가한다.

## 기능 명세

- [001-post-create](../specs/001-post-create/spec.md): 제목, 본문, 작성자로 게시글 작성
- [002-api-error-swagger](../specs/002-api-error-swagger/spec.md): 전역 API 에러 처리와 한글 Swagger 문서화
- [003-member-signup](../specs/003-member-signup/spec.md): 이메일, 비밀번호, 닉네임으로 회원가입
- [004-bounded-context-packages](../specs/004-bounded-context-packages/spec.md): bounded context 기준 패키지 분리
- [005-member-login](../specs/005-member-login/spec.md): accessToken과 refreshToken 기반 로그인
- [006-api-response-swagger-cleanup](../specs/006-api-response-swagger-cleanup/spec.md): 표준 API 응답과 Swagger 문서 정리
- [007-global-exception-policy](../specs/007-global-exception-policy/spec.md): 명시적 예외와 에러 코드 기반 전역 예외 처리 정책
- [008-post-create-auth-korean-errors](../specs/008-post-create-auth-korean-errors/spec.md): 게시글 작성 인증 필수화와 한글 에러 응답 정책
- [009-post-read](../specs/009-post-read/spec.md): 게시글 id 기반 상세 조회
- [010-post-list](../specs/010-post-list/spec.md): 게시글 최신순 페이징 목록 조회
- [011-post-delete](../specs/011-post-delete/spec.md): 작성자 본인 게시글 소프트 삭제
- [012-comment-create](../specs/012-comment-create/spec.md): 로그인 회원의 댓글 작성
- [013-post-comments-read](../specs/013-post-comments-read/spec.md): 게시글 댓글 페이지 분리 조회와 댓글 수 제공
- [014-post-delete-comments](../specs/014-post-delete-comments/spec.md): 게시글 삭제 시 댓글 물리 삭제
- [015-comment-delete](../specs/015-comment-delete/spec.md): 댓글 작성자 본인 댓글 삭제
- [016-post-update](../specs/016-post-update/spec.md): 게시글 작성자 본인 게시글 수정
- [017-comment-update](../specs/017-comment-update/spec.md): 댓글 작성자 본인 댓글 수정
- [018-post-search](../specs/018-post-search/spec.md): 게시글 제목과 본문 키워드 검색
- [019-server-deployment](../specs/019-server-deployment/spec.md): EC2 Docker Compose 서버 배포 준비
- [020-frontend-web-client](../specs/020-frontend-web-client/spec.md): React 기반 프론트엔드 웹 클라이언트 구축
