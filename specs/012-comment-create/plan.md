# 012 댓글 작성 계획

## 설계 방향

댓글 작성은 `board` bounded context의 쓰기 유스케이스로 구현한다. Web 어댑터는 인증 회원과 게시글 id, 댓글 본문을 애플리케이션 계층으로 전달하고, 게시글 존재 여부와 댓글 본문 규칙은 애플리케이션 및 도메인 계층에서 검증한다.

## 애플리케이션 계층

- 입력 포트는 `CreateCommentUseCase`로 둔다.
- 입력 값은 `CreateCommentCommand(postId, content, authorMemberId)`로 둔다.
- 결과 값은 생성된 댓글 id만 담는 `CreateCommentResult(id)`로 둔다.
- 게시글 id 또는 작성자 회원 id가 유효하지 않으면 `BusinessException(GlobalErrorCode.INVALID_REQUEST)`를 던진다.
- 삭제되지 않은 게시글을 찾지 못하면 `BusinessException(BoardErrorCode.POST_NOT_FOUND)`를 던진다.
- 댓글 본문이 없거나 비어 있으면 `BusinessException(BoardErrorCode.COMMENT_CONTENT_REQUIRED)`를 던진다.
- 댓글 본문이 1,000자를 초과하면 `BusinessException(BoardErrorCode.COMMENT_CONTENT_TOO_LONG)`를 던진다.
- 댓글 저장 전 `PostRepositoryPort.findById(postId)`로 삭제되지 않은 게시글 존재를 확인한다.

## 도메인 모델

- `Comment` 도메인 모델을 추가한다.
- `Comment`는 `id`, `postId`, `authorMemberId`, `content`, `createdAt`을 가진다.
- `Comment.create(postId, authorMemberId, content)`는 댓글 본문을 trim하고 도메인 불변식을 검증한다.
- `Comment.rehydrate(...)`는 영속성 어댑터에서 도메인 모델 복원에 사용한다.
- 댓글 도메인 계층은 Spring Security, HTTP, JPA에 의존하지 않는다.
- 이번 기능에서 `Comment`는 대댓글 부모 식별자를 가지지 않는다.

## 포트와 어댑터

- `CommentRepositoryPort`를 추가하고 `save(Comment comment)`를 제공한다.
- Persistence 어댑터는 `comments` 테이블에 댓글을 저장한다.
- 댓글 JPA Entity는 도메인 모델과 분리하고, 도메인 모델과 상호 변환한다.
- 게시글 존재 확인은 기존 `PostRepositoryPort`의 삭제 제외 조회 정책을 재사용한다.
- 다른 bounded context의 애플리케이션 서비스나 어댑터에 직접 의존하지 않는다.

## Web 어댑터

- `PostController`에 `POST /posts/{postId}/comments`를 추가한다.
- 요청 DTO는 `CreateCommentRequest(content)`로 둔다.
- 응답 DTO는 `CreateCommentResponse(id)`로 둔다.
- `@AuthenticationPrincipal AuthenticatedMemberPrincipal`이 없으면 `MemberErrorCode.UNAUTHORIZED`를 던진다.
- 성공 시 `201 Created`와 `ApiResponse<CreateCommentResponse>`를 반환한다.
- `SecurityConfig`는 `POST /posts/*/comments`를 공개 API에 포함하지 않고 인증 필요 상태로 둔다.

## Swagger

- `CreateCommentApiDocs`를 추가해 댓글 작성 성공, 인증 실패, 게시글 없음, 잘못된 요청, 댓글 본문 검증 실패를 한글로 설명한다.
- 실패 응답은 기존 공통 에러 응답 구조와 `code`, `message` 규칙을 따른다.
- 요청/응답 DTO의 필드 설명은 DTO의 `@Schema`에 작성한다.

## 기존 명세와의 관계

- `008-post-create-auth-korean-errors`의 인증 회원 식별 정책을 따른다.
- `009-post-read`의 댓글 포함 조회 비범위를 유지한다.
- `011-post-delete`의 삭제된 게시글 조회 제외 정책을 댓글 작성 대상 검증에도 적용한다.
- `007-global-exception-policy`의 에러 응답 구조를 유지한다.

## 테스트 전략

- 도메인 테스트에서 댓글 생성 성공, 본문 trim, 본문 누락, 빈 본문, 길이 초과, 잘못된 게시글 id, 잘못된 작성자 회원 id를 검증한다.
- 애플리케이션 서비스 테스트에서 댓글 작성 성공, 게시글 없음, 삭제된 게시글, 잘못된 입력을 검증한다.
- Web 어댑터 테스트에서 `POST /posts/{postId}/comments` 성공 `201`, 인증 실패 `401`, 게시글 없음 `404`, 잘못된 id `400`, 댓글 본문 검증 실패 `400`을 검증한다.
- Persistence 어댑터 테스트에서 댓글 저장과 도메인 모델 복원을 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
