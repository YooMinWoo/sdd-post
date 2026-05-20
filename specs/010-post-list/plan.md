# 010 게시글 목록 페이징 조회 계획

## 설계 방향

게시글 목록 조회는 `board` bounded context의 읽기 유스케이스로 구현한다. 상세 조회와 같은 작성자 닉네임 조합 정책을 사용하되, 목록 응답에서는 게시글 본문을 제외한다.

## 애플리케이션 계층

- 입력 포트는 `ListPostsUseCase`로 둔다.
- 입력 값은 `ListPostsQuery(page, size)`로 둔다.
- 출력 값은 `ListPostsResult`와 `PostSummaryResult`로 둔다.
- 페이지 번호와 페이지 크기 검증은 애플리케이션 서비스에서 수행한다.
- 페이지 번호가 0 미만이거나 페이지 크기가 1 미만 또는 100 초과이면 `BusinessException(GlobalErrorCode.INVALID_REQUEST)`를 던진다.

## 포트와 어댑터

- `PostRepositoryPort`에 최신순 페이징 조회 메서드를 추가한다.
- Spring Data `Pageable`은 persistence 어댑터 내부에 격리하고, 애플리케이션 계층에는 자체 페이징 결과 DTO를 반환한다.
- `PostJpaRepository`는 `findAllByOrderByCreatedAtDesc(Pageable pageable)`을 사용한다.
- 작성자 닉네임 조회는 N+1을 피하기 위해 목록 내 `authorMemberId`를 모아 `AuthorMemberPort`의 배치 조회 메서드로 한 번에 조회한다.
- 목록 조회는 게시글 페이지 조회 1회와 작성자 배치 조회 1회로 처리한다.

## Web 어댑터

- `PostController`에 `GET /posts`를 추가한다.
- 요청 파라미터는 `page` 기본값 0, `size` 기본값 10을 사용한다.
- 응답 DTO는 `ListPostsResponse`와 `PostSummaryResponse`로 둔다.
- 목록 항목 응답은 `content` 필드를 포함하지 않는다.
- 조회 API는 공개 API로 둔다.

## Swagger

- `ListPostsApiDocs`를 추가해 성공 응답과 잘못된 페이징 요청을 한글로 설명한다.

## 테스트 전략

- 애플리케이션 서비스 테스트에서 최신순 목록 결과, 작성자 닉네임 조합, 잘못된 페이징 요청을 검증한다.
- 애플리케이션 서비스 테스트에서 작성자 닉네임을 게시글별 개별 조회하지 않고 배치 조회하는지 검증한다.
- 영속성 어댑터 테스트에서 생성 시각 기준 최신순 페이징 조회를 검증한다.
- 컨트롤러 테스트에서 `GET /posts` 응답 구조와 `content` 미포함을 검증한다.
