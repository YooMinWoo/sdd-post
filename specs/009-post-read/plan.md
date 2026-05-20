# 009 게시글 상세 조회 계획

## 아키텍처

게시글 상세 조회는 `board` bounded context의 읽기 유스케이스로 구현한다. Web 어댑터는 HTTP 요청/응답 변환을 담당하고, 애플리케이션 서비스는 게시글 조회와 작성자 닉네임 조합을 수행한다.

## 구현 방향

- 입력 포트는 `ReadPostUseCase`, 입력 값은 `ReadPostQuery(postId)`, 출력 값은 `ReadPostResult`로 둔다.
- 출력 포트는 `PostRepositoryPort`에 id 기반 조회 메서드를 추가하거나 별도 읽기 포트를 둔다. 기존 포트 일관성을 우선해 `findById(Long id)` 추가를 기본으로 한다.
- 작성자 닉네임은 기존 `AuthorMemberPort`를 재사용해 `authorMemberId`로 현재 닉네임을 조회한다.
- 게시글이 없으면 `PostNotFoundException` 또는 `BusinessException(BoardErrorCode.POST_NOT_FOUND)`로 표현한다.
- `BoardErrorCode`에 `POST_NOT_FOUND`를 추가하고 `GlobalExceptionHandler`는 해당 코드를 `404 Not Found`로 매핑한다.
- `PostController`에 `GET /posts/{postId}`를 추가하고, 응답 DTO는 작성 응답과 동일한 필드 구조를 유지한다.
- 조회 API는 공개 API로 둔다. 현재 비공개 게시글 정책이 없으므로 인증을 요구하지 않는다.
- Swagger 문서는 `ReadPostApiDocs`를 추가해 성공, 400, 404 응답을 한글로 설명한다.

## 기존 명세와의 관계

- `001-post-create`에서 비범위였던 상세 조회를 이 명세에서 새 기능으로 정의한다.
- `008-post-create-auth-korean-errors`의 `authorMemberId` 저장 정책을 따른다.
- 실패 응답은 `007-global-exception-policy`와 `008-post-create-auth-korean-errors`의 `code=<ERROR_CODE>`, `message=<한글 설명>` 구조를 따른다.

## 테스트 전략

- 도메인 테스트는 id가 없는 재구성이나 잘못된 값이 기존 예외 정책을 따르는지 확인한다.
- 애플리케이션 서비스 테스트는 게시글 조회 성공, 게시글 없음, 작성자 닉네임 조합을 검증한다.
- Web 어댑터 테스트는 `GET /posts/{postId}` 성공 응답, `POST_NOT_FOUND` 404 응답, 잘못된 id 400 응답을 검증한다.
- Persistence 어댑터 테스트는 id 기반 조회 성공과 없음 케이스를 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
