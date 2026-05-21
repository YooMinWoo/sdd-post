# 017 댓글 수정 작업

## 문서화

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `docs/INDEX.md` 갱신

## 구현

- [x] `UpdateCommentUseCase`, `UpdateCommentCommand`, `UpdateCommentResult` 추가
- [x] `Comment.updateBy(...)` 도메인 행위 추가
- [x] 댓글 수정 애플리케이션 서비스 구현
- [x] `PATCH /posts/{postId}/comments/{commentId}` Web 어댑터 추가
- [x] 댓글 수정 요청/응답 DTO 추가
- [x] 댓글 수정 Swagger 문서 추가
- [x] 댓글 수정 권한 에러 코드와 HTTP 상태 매핑 추가

## 검증

- [x] 댓글 수정 애플리케이션 서비스 테스트 추가
- [x] 댓글 도메인 테스트 갱신
- [x] 댓글 영속성 어댑터 테스트 갱신
- [x] Web 어댑터 테스트 갱신
- [x] `.\gradlew.bat test` 실행
