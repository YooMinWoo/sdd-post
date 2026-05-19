# 003 회원가입 계획

## 아키텍처

DDD와 헥사고날 아키텍처에 맞춰 회원가입 기능을 구현한다. 비밀번호 암호화와 저장소 접근은 포트로 분리하고, 도메인 계층은 Spring Security와 JPA에 의존하지 않는다.

## 도메인

- `Member`는 회원가입의 애그리거트 루트다.
- `Member.create(email, passwordHash, nickname)`은 이메일과 닉네임을 정규화하고 검증한다.
- 회원은 id, email, passwordHash, nickname, createdAt을 가진다.
- 도메인 검증 실패는 `IllegalArgumentException`으로 표현한다.

## 애플리케이션

- 입력 포트: `SignupUseCase`
- 입력 커맨드: `SignupCommand`
- 출력 결과: `SignupResult`
- 애플리케이션 서비스: `SignupService`
- 출력 포트: `MemberRepositoryPort`, `PasswordEncoderPort`
- 이메일 중복은 `DuplicateEmailException`으로 표현한다.

## 어댑터

- Web 어댑터: `AuthController`
- 요청 DTO: `SignupRequest`
- 응답 DTO: `SignupResponse`
- Persistence 어댑터: `MemberPersistenceAdapter`
- JPA 엔티티: `MemberJpaEntity`
- Spring Data 저장소: `MemberJpaRepository`
- Security 어댑터: `BCryptPasswordEncoderAdapter`

## 패키지 배치

```text
com.example.post.member.domain.model
com.example.post.member.application.exception
com.example.post.member.application.port.in
com.example.post.member.application.port.out
com.example.post.member.application.service
com.example.post.member.adapter.in.web
com.example.post.member.adapter.out.persistence
com.example.post.member.adapter.out.security
com.example.post.global.config
```

## 테스트 전략

- 회원 생성과 검증을 위한 도메인 테스트를 작성한다.
- Fake 저장소 포트와 Fake 비밀번호 인코더로 애플리케이션 서비스를 테스트한다.
- Web 어댑터는 MockMvc 기반 테스트로 성공, 잘못된 입력, 중복 이메일을 검증한다.
- Persistence 어댑터는 `@DataJpaTest`로 저장과 이메일 존재 여부 조회를 검증한다.
- 구현 완료 후 `.\gradlew.bat test`를 실행한다.
