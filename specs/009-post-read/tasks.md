# 009 게시글 상세 조회 작업

- [x] SDD 명세 작성
- [x] 구현 계획 작성
- [x] 인수 조건 작성
- [x] 문서 색인 갱신
- [x] `BoardErrorCode.POST_NOT_FOUND` 추가
- [x] 전역 예외 핸들러에 `POST_NOT_FOUND -> 404 Not Found` 매핑 추가
- [x] `ReadPostUseCase`, `ReadPostQuery`, `ReadPostResult` 추가
- [x] 게시글 id 기반 조회 포트/어댑터 구현
- [x] 작성자 현재 닉네임 조합 구현
- [x] `GET /posts/{postId}` Web 어댑터 추가
- [x] Swagger 문서 어노테이션 추가
- [x] 도메인, 애플리케이션, Web, Persistence 테스트 추가
- [x] `.\gradlew.bat test` 실행
