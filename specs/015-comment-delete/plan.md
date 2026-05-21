# 015 댓글 삭제 계획

## 설계 방향

댓글 삭제는 `board` bounded context의 쓰기 유스케이스로 구현한다. Web 어댑터는 인증 회원, 게시글 id, 댓글 id를 애플리케이션 계층으로 전달하고, 게시글 존재와 댓글 소유권을 검증한 뒤 댓글을 물리 삭제한다.

## 애플리케이션 계층

- 입력 포트는 `DeleteCommentUseCase`로 둔다.
- 입력 값은 `DeleteCommentCommand(postId, commentId, requesterMemberId)`로 둔다.
- 게시글 id, 댓글 id, 요청자 회원 id가 유효하지 않으면 `GlobalErrorCode.INVALID_REQUEST`를 던진다.
- 게시글이 없거나 삭제된 게시글이면 `BoardErrorCode.POST_NOT_FOUND`를 던진다.
- 댓글이 없거나 요청 게시글에 속하지 않으면 `BoardErrorCode.COMMENT_NOT_FOUND`를 던진다.
- 댓글 작성자와 요청자가 다르면 `BoardErrorCode.COMMENT_DELETE_FORBIDDEN`을 던진다.

## 포트와 어댑터

- `CommentRepositoryPort`에 `findById(Long id)`와 `deleteById(Long id)`를 추가한다.
- Persistence 어댑터는 Spring Data JPA의 `findById`, `deleteById`를 사용한다.
- 댓글 삭제는 물리 삭제로 처리한다.
- 삭제 대상 댓글이 없는 경우에는 애플리케이션 계층에서 먼저 차단한다.

## Web 어댑터

- `PostController`에 `DELETE /posts/{postId}/comments/{commentId}`를 추가한다.
- 인증 principal이 없으면 `MemberErrorCode.UNAUTHORIZED`를 던진다.
- 성공 시 `204 No Content`를 반환한다.
- Swagger 문서는 성공, 인증 실패, 게시글 없음, 댓글 없음, 권한 없음, 잘못된 요청을 한글로 설명한다.

## 테스트 전략

- 애플리케이션 서비스 테스트에서 삭제 성공, 잘못된 id, 게시글 없음, 댓글 없음, 게시글 불일치, 작성자 불일치를 검증한다.
- Web 어댑터 테스트에서 성공 `204`, 인증 실패 `401`, 게시글 없음 `404`, 댓글 없음 `404`, 권한 없음 `403`, 잘못된 요청 `400`을 검증한다.
- Persistence 어댑터 테스트에서 댓글 단건 삭제와 다른 댓글 유지 여부를 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
