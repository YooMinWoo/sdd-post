# 015 댓글 삭제 작업

## 문서화

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `docs/INDEX.md` 갱신

## 구현

- [x] `DeleteCommentUseCase`, `DeleteCommentCommand` 추가
- [x] 댓글 삭제 애플리케이션 서비스 구현
- [x] `CommentRepositoryPort`에 댓글 단건 조회/삭제 메서드 추가
- [x] 댓글 Persistence 어댑터에 댓글 단건 조회/삭제 구현
- [x] `DELETE /posts/{postId}/comments/{commentId}` Web 어댑터 추가
- [x] 댓글 삭제 Swagger 문서 추가
- [x] 댓글 삭제 에러 코드와 HTTP 상태 매핑 추가

## 검증

- [x] 댓글 삭제 애플리케이션 서비스 테스트 추가
- [x] 댓글 영속성 어댑터 테스트 갱신
- [x] Web 어댑터 테스트 갱신
- [x] `.\gradlew.bat test` 실행
