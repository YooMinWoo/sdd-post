# 014 게시글 삭제 시 댓글 삭제 작업

## 문서화

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `docs/INDEX.md` 갱신

## 구현

- [x] `CommentRepositoryPort`에 게시글별 댓글 삭제 메서드 추가
- [x] 댓글 Persistence 어댑터에 게시글별 댓글 삭제 구현
- [x] 게시글 삭제 서비스에서 댓글 삭제 포트 호출
- [x] 게시글 삭제 실패 경로에서 댓글 삭제 미수행 보장

## 검증

- [x] 게시글 삭제 애플리케이션 서비스 테스트 갱신
- [x] 댓글 영속성 어댑터 테스트 갱신
- [x] `.\gradlew.bat test` 실행
