# 016 게시글 수정 계획

## 설계 방향

게시글 수정은 `board` bounded context의 쓰기 유스케이스로 구현한다. 제목과 본문 검증은 `Post` 도메인 모델의 기존 생성 검증을 재사용하고, 작성자 검증도 도메인 행위로 둔다.

## 애플리케이션 계층

- 입력 포트는 `UpdatePostUseCase`로 둔다.
- 입력 값은 `UpdatePostCommand(postId, title, content, requesterMemberId)`로 둔다.
- 출력 값은 `UpdatePostResult(id, title, content, authorMemberId, author, createdAt, commentCount)`로 둔다.
- 게시글 id 또는 요청자 회원 id가 유효하지 않으면 `GlobalErrorCode.INVALID_REQUEST`를 던진다.
- 게시글이 없거나 삭제된 게시글이면 `BoardErrorCode.POST_NOT_FOUND`를 던진다.
- 작성자와 요청자가 다르면 `BoardErrorCode.POST_UPDATE_FORBIDDEN`을 던진다.
- 수정 후 작성자 닉네임과 댓글 수를 조회해 응답에 조합한다.

## 도메인 모델

- `Post.updateBy(requesterMemberId, title, content)`를 추가한다.
- `updateBy`는 작성자 본인 여부와 삭제 여부를 확인한 뒤 제목/본문 검증을 수행한다.
- 도메인 계층은 Spring Security, HTTP, JPA에 의존하지 않는다.

## Web 어댑터

- `PostController`에 `PATCH /posts/{postId}`를 추가한다.
- 요청 DTO는 `UpdatePostRequest(title, content)`로 둔다.
- 응답 DTO는 `UpdatePostResponse`로 둔다.
- 인증 principal이 없으면 `MemberErrorCode.UNAUTHORIZED`를 던진다.
- Swagger 문서는 성공, 인증 실패, 게시글 없음, 권한 없음, 잘못된 요청을 한글로 설명한다.

## 테스트 전략

- 애플리케이션 서비스 테스트에서 수정 성공, 잘못된 id, 게시글 없음, 작성자 불일치, 제목/본문 검증 실패를 검증한다.
- 도메인 테스트에서 작성자 본인 수정과 권한 없음을 검증한다.
- Persistence 어댑터 테스트에서 수정된 제목/본문 저장을 검증한다.
- Web 어댑터 테스트에서 성공 `200`, 인증 실패 `401`, 권한 없음 `403`, 게시글 없음 `404`, 잘못된 요청 `400`을 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
