# 011 게시글 삭제 계획

## 설계 방향

게시글 삭제는 `board` bounded context의 쓰기 유스케이스로 구현한다. Web 어댑터는 인증 회원과 게시글 id를 애플리케이션 계층으로 전달하고, 삭제 가능 여부는 도메인 규칙으로 검증한다.

## 애플리케이션 계층

- 입력 포트는 `DeletePostUseCase`로 둔다.
- 입력 값은 `DeletePostCommand(postId, requesterMemberId)`로 둔다.
- 삭제 성공은 별도 결과 DTO 없이 완료로 표현한다.
- 게시글 id 또는 요청자 회원 id가 유효하지 않으면 `BusinessException(GlobalErrorCode.INVALID_REQUEST)`를 던진다.
- 삭제 가능한 게시글을 찾지 못하면 `BusinessException(BoardErrorCode.POST_NOT_FOUND)`를 던진다.
- 작성자와 요청자가 다르면 `BusinessException(BoardErrorCode.POST_DELETE_FORBIDDEN)`를 던진다.

## 도메인 모델

- `Post`에 `deletedAt`을 추가한다.
- `Post`는 삭제 여부를 판단하는 `isDeleted()`를 제공한다.
- `Post`는 작성자 본인인지 확인한 뒤 현재 객체의 삭제 시각을 기록하는 삭제 행위를 제공한다.
- 이미 삭제된 게시글은 삭제 대상 조회 단계에서 제외하므로 도메인 삭제 행위의 정상 입력으로 보지 않는다.
- 도메인 계층은 Spring Security, HTTP, JPA에 의존하지 않는다.

## 포트와 어댑터

- `PostRepositoryPort`에 삭제되지 않은 게시글 id 조회 메서드를 추가하거나 기존 `findById`의 삭제 필터 정책을 명확히 조정한다.
- 삭제 상태 저장은 기존 `save(Post post)`를 재사용하거나 `update(Post post)` 성격의 포트 메서드로 표현한다.
- Persistence 어댑터는 `posts.deleted_at` 컬럼을 사용한다.
- 상세 조회와 목록 조회는 `deleted_at is null` 조건으로 삭제된 게시글을 제외한다.
- 목록 조회의 정렬과 작성자 닉네임 배치 조회 정책은 기존 `010-post-list` 정책을 유지한다.

## Web 어댑터

- `PostController`에 `DELETE /posts/{postId}`를 추가한다.
- `@AuthenticationPrincipal AuthenticatedMemberPrincipal`이 없으면 `MemberErrorCode.UNAUTHORIZED`를 던진다.
- 성공 시 `ResponseEntity.noContent().build()`로 `204 No Content`를 반환한다.
- `SecurityConfig`는 `DELETE /posts/*`를 공개 API에 포함하지 않고 인증 필요 상태로 둔다.

## Swagger

- `DeletePostApiDocs`를 추가해 삭제 성공, 인증 실패, 권한 없음, 게시글 없음, 잘못된 요청을 한글로 설명한다.
- 본문이 없는 `204 No Content` 응답은 공통 `ApiResponse` 스키마를 강제하지 않는다.
- 실패 응답은 기존 공통 에러 응답 구조와 `code`, `message` 규칙을 따른다.

## 기존 명세와의 관계

- `008-post-create-auth-korean-errors`의 인증 회원 식별 정책을 따른다.
- `009-post-read`의 상세 조회는 삭제된 게시글을 조회하지 않도록 확장한다.
- `010-post-list`의 목록 조회는 삭제된 게시글을 목록에서 제외하도록 확장한다.
- `007-global-exception-policy`의 에러 응답 구조를 유지한다.

## 테스트 전략

- 애플리케이션 서비스 테스트에서 삭제 성공, 게시글 없음, 이미 삭제된 게시글, 작성자 불일치를 검증한다.
- Web 어댑터 테스트에서 `DELETE /posts/{postId}` 성공 `204`, 인증 실패 `401`, 권한 없음 `403`, 게시글 없음 `404`, 잘못된 id `400`을 검증한다.
- Persistence 어댑터 테스트에서 `deletedAt` 저장과 삭제된 게시글 조회 제외를 검증한다.
- 상세 조회와 목록 조회 회귀 테스트에서 삭제된 게시글이 노출되지 않는지 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
