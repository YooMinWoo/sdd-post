# 012 댓글 작성 작업

## 문서화

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `docs/INDEX.md` 갱신
- [x] `docs/DOMAIN_GLOSSARY.md` 갱신
- [ ] GitHub Issue 발행 (권한 문제로 미발행: GitHub App 403, `gh` CLI 없음)

## 구현

- [x] `Comment` 도메인 모델 추가
- [x] 댓글 본문 검증 도메인 규칙 추가
- [x] `CreateCommentUseCase`, `CreateCommentCommand`, `CreateCommentResult` 추가
- [x] 댓글 작성 애플리케이션 서비스 구현
- [x] `CommentRepositoryPort` 추가
- [x] 댓글 JPA Entity와 Persistence 어댑터 구현
- [x] 삭제되지 않은 게시글 존재 확인 로직 반영
- [x] `BoardErrorCode.COMMENT_CONTENT_REQUIRED` 추가
- [x] `BoardErrorCode.COMMENT_CONTENT_TOO_LONG` 추가
- [x] 댓글 에러 코드 전역 예외 매핑 추가
- [x] `POST /posts/{postId}/comments` Web 어댑터 추가
- [x] 댓글 작성 요청/응답 DTO 추가
- [x] 댓글 작성 API Swagger 문서 추가
- [x] 인증 필요 보안 정책 확인

## 검증

- [x] 도메인 모델 테스트 추가
- [x] 애플리케이션 서비스 테스트 추가
- [x] Web 어댑터 테스트 추가
- [x] 영속성 어댑터 테스트 추가
- [x] `./gradlew.bat test` 실행
