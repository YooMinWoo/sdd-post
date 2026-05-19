# 001 게시글 작성 계획

## 아키텍처

DDD와 헥사고날 아키텍처에 맞춰 게시글 작성 기능을 구현한다.

## 도메인

- `Post`는 게시글 작성의 애그리거트 루트다.
- `Post.create(title, content, author)`는 입력값을 정규화하고 검증한다.
- 도메인 검증 실패는 `IllegalArgumentException`으로 표현한다.

## 애플리케이션

- 입력 포트: `CreatePostUseCase`
- 입력 커맨드: `CreatePostCommand`
- 출력 결과: `CreatePostResult`
- 애플리케이션 서비스: `CreatePostService`
- 출력 포트: `PostRepositoryPort`
- 서비스는 도메인 객체를 생성하고 출력 포트를 통해 저장한다.

## 어댑터

- Web 어댑터: `PostController`
- 요청 DTO: `CreatePostRequest`
- 응답 DTO: `CreatePostResponse`
- Persistence 어댑터: `PostPersistenceAdapter`
- JPA 엔티티: `PostJpaEntity`
- Spring Data 저장소: `PostJpaRepository`

## 패키지 배치

```text
com.example.post.board.domain.model
com.example.post.board.application.port.in
com.example.post.board.application.port.out
com.example.post.board.application.service
com.example.post.board.adapter.in.web
com.example.post.board.adapter.out.persistence
```

## 테스트 전략

- 게시글 생성과 검증을 위한 도메인 테스트를 작성한다.
- Fake 저장소 포트를 사용해 애플리케이션 서비스를 테스트한다.
- Web 어댑터는 MockMvc 기반 테스트로 검증한다.
- Persistence 어댑터는 `@DataJpaTest`로 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
