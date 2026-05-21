# 013 게시글 댓글 조회 및 댓글 수 계획

## 설계 방향

게시글 상세/목록 조회는 댓글 수만 포함하고, 댓글 페이지 조회는 별도 읽기 유스케이스로 분리한다. 댓글은 `board` bounded context 안의 별도 Aggregate로 유지하고, 조회 유스케이스에서 댓글 작성자 닉네임을 조합한다.

## 애플리케이션 계층

- `ReadPostQuery`는 `postId`만 가진다.
- `ReadPostResult`에 `commentCount`를 추가한다.
- `PostSummaryResult`에 `commentCount`를 추가한다.
- 댓글 목록 입력 포트는 `ListPostCommentsUseCase`로 둔다.
- 댓글 목록 입력 값은 `ListPostCommentsQuery(postId, page, size)`로 둔다.
- 댓글 목록 결과는 `ListPostCommentsResult(items, page, size, totalElements, totalPages, first, last)`로 둔다.
- 댓글 항목 결과는 `CommentSummaryResult(id, authorMemberId, author, content, createdAt)`로 둔다.
- 댓글 목록 조회는 게시글 존재를 먼저 확인한 뒤 댓글 페이지를 조회한다.
- 게시글이 없거나 삭제된 게시글이면 `BoardErrorCode.POST_NOT_FOUND`를 던지고 댓글 조회를 수행하지 않는다.
- 댓글 목록 페이징 값이 유효하지 않으면 `BusinessException(GlobalErrorCode.INVALID_REQUEST)`를 던진다.

## 포트와 어댑터

- `CommentRepositoryPort`에 게시글별 댓글 페이지 조회 메서드를 추가한다.
- `CommentRepositoryPort`에 게시글 id 목록별 댓글 수 배치 조회 메서드를 추가한다.
- Persistence 어댑터는 댓글 페이지를 `createdAt desc` 기준으로 조회한다.
- 댓글 수 배치 조회는 게시글 id별 `count`를 반환하고, 댓글이 없는 게시글은 애플리케이션 계층에서 0으로 처리한다.
- 댓글 작성자 닉네임 조회는 `AuthorMemberPort.getNicknamesByIds`를 재사용한다.
- 댓글이 없는 경우 작성자 닉네임 조회를 수행하지 않는다.

## Web 어댑터

- `GET /posts/{postId}`는 댓글 목록을 포함하지 않고 `commentCount`만 반환한다.
- `GET /posts/{postId}/comments`를 추가한다.
- 댓글 목록 API의 `page` 기본값은 0, `size` 기본값은 10으로 둔다.
- 댓글 페이지 응답 DTO는 `items`, `page`, `size`, `totalElements`, `totalPages`, `first`, `last`를 포함한다.
- 목록 항목 응답 DTO에 `commentCount`를 추가한다.
- 댓글 목록 조회 API는 공개 조회 API로 두고 보안 설정에 명시한다.
- Swagger 문서는 상세 조회의 댓글 수, 목록 조회의 댓글 수, 댓글 목록 API를 한글로 설명한다.

## 기존 명세와의 관계

- `009-post-read`의 댓글 포함 조회 비범위는 유지하고 댓글 수만 추가한다.
- `010-post-list`의 목록 항목에 `commentCount`를 추가한다.
- `012-comment-create`에서 생성한 댓글을 조회 대상으로 사용한다.
- `011-post-delete`의 삭제된 게시글 조회 제외 정책을 유지한다.
- `007-global-exception-policy`의 공통 실패 응답 구조를 유지한다.

## 테스트 전략

- 상세 조회 서비스 테스트에서 `commentCount` 포함과 게시글 없음 시 댓글 수 조회 미수행을 검증한다.
- 댓글 목록 서비스 테스트에서 댓글 최신순 페이징, 빈 댓글 페이지, 잘못된 페이징 요청, 게시글 없음을 검증한다.
- 목록 조회 서비스 테스트에서 게시글별 댓글 수가 포함되는지 검증한다.
- Persistence 어댑터 테스트에서 게시글별 댓글 최신순 페이징과 게시글 id별 댓글 수 배치 집계를 검증한다.
- Web 어댑터 테스트에서 상세 응답의 `commentCount`, 댓글 목록 응답, 목록 응답의 `posts[].commentCount`, 잘못된 댓글 페이징 400 응답을 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
