# 014 게시글 삭제 시 댓글 삭제 계획

## 설계 방향

게시글 삭제 유스케이스 안에서 댓글 저장소 포트를 호출해 게시글 삭제와 댓글 삭제를 하나의 트랜잭션으로 묶는다. 댓글에는 삭제 상태가 없으므로 물리 삭제를 사용하고, 외부 API 계약은 변경하지 않는다.

## 애플리케이션 계층

- `DeletePostService`에 `CommentRepositoryPort`를 주입한다.
- 게시글 id와 요청자 회원 id 검증은 기존 정책을 유지한다.
- 삭제 가능한 게시글 조회와 작성자 검증이 성공한 뒤 `deleteAllByPostId(postId)`를 호출한다.
- 댓글 삭제 후 기존처럼 `Post.deleteBy(...)` 결과를 저장한다.
- 게시글 없음, 이미 삭제된 게시글, 권한 없음, 잘못된 요청에서는 댓글 삭제를 수행하지 않는다.

## 포트와 어댑터

- `CommentRepositoryPort`에 `deleteAllByPostId(Long postId)`를 추가한다.
- `CommentPersistenceAdapter`는 `CommentJpaRepository.deleteByPostId(Long postId)`로 구현한다.
- 삭제 대상 댓글이 없어도 예외 없이 완료한다.
- 댓글 삭제는 `DeletePostService`의 트랜잭션에 참여한다.

## 기존 명세와의 관계

- `011-post-delete`의 게시글 소프트 삭제, 작성자 본인 삭제, `204 No Content` 응답 정책을 유지한다.
- `012-comment-create`에서 생성한 댓글은 게시글 삭제 성공 시 함께 삭제된다.
- `013-post-comments-read`의 삭제된 게시글 댓글 목록 조회 `404 POST_NOT_FOUND` 정책을 유지한다.

## 테스트 전략

- 애플리케이션 서비스 테스트에서 게시글 삭제 성공 시 댓글 삭제 포트 호출을 검증한다.
- 애플리케이션 서비스 테스트에서 게시글 없음, 이미 삭제됨, 권한 없음, 잘못된 요청 시 댓글 삭제 포트가 호출되지 않는지 검증한다.
- Persistence 어댑터 테스트에서 특정 게시글 댓글만 삭제되고 다른 게시글 댓글은 유지되는지 검증한다.
- Persistence 어댑터 테스트에서 댓글 삭제 후 댓글 수 집계와 댓글 목록 조회가 삭제 결과를 반영하는지 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
