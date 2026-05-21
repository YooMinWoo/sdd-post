# 013 게시글 댓글 조회 및 댓글 수 작업

## 문서화

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `docs/INDEX.md` 갱신

## 구현

- [x] `ReadPostResult`에 `commentCount` 추가
- [x] `PostSummaryResult`에 `commentCount` 추가
- [x] 댓글 요약 결과와 댓글 페이지 결과 DTO 추가
- [x] `ListPostCommentsUseCase`, `ListPostCommentsQuery`, `ListPostCommentsResult` 추가
- [x] 댓글 목록 조회 애플리케이션 서비스 구현
- [x] `CommentRepositoryPort`에 게시글별 댓글 페이지 조회 추가
- [x] `CommentRepositoryPort`에 게시글 id별 댓글 수 배치 조회 추가
- [x] 댓글 Persistence 어댑터에 최신순 페이징 조회 구현
- [x] 댓글 Persistence 어댑터에 댓글 수 배치 집계 구현
- [x] 게시글 상세 조회 서비스에 댓글 수 조합 로직 추가
- [x] 게시글 목록 조회 서비스에 댓글 수 조합 로직 추가
- [x] `GET /posts/{postId}/comments` Web 어댑터 추가
- [x] 게시글 상세 조회 응답 DTO에 `commentCount` 추가
- [x] 게시글 목록 항목 응답 DTO에 `commentCount` 추가
- [x] 게시글 상세/목록/댓글 목록 Swagger 문서 갱신
- [x] 댓글 목록 조회 공개 보안 정책 추가

## 검증

- [x] 상세 조회 애플리케이션 서비스 테스트 추가 또는 갱신
- [x] 댓글 목록 조회 애플리케이션 서비스 테스트 추가
- [x] 목록 조회 애플리케이션 서비스 테스트 추가 또는 갱신
- [x] 댓글 영속성 어댑터 테스트 추가 또는 갱신
- [x] Web 어댑터 테스트 추가 또는 갱신
- [x] `./gradlew.bat test` 실행
