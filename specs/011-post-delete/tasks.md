# 011 게시글 삭제 작업

## 문서화

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `docs/INDEX.md` 갱신

## 구현

- [x] `Post` 도메인 모델에 `deletedAt` 기반 소프트 삭제 상태 추가
- [x] 작성자 본인 삭제 검증 도메인 규칙 추가
- [x] `DeletePostUseCase`, `DeletePostCommand` 추가
- [x] 게시글 삭제 애플리케이션 서비스 구현
- [x] `PostRepositoryPort`와 Persistence 어댑터에 삭제 상태 저장 및 삭제 제외 조회 반영
- [x] 상세 조회에서 삭제된 게시글 제외
- [x] 목록 조회에서 삭제된 게시글 제외
- [x] `BoardErrorCode.POST_DELETE_FORBIDDEN` 추가
- [x] `POST_DELETE_FORBIDDEN -> 403 Forbidden` 전역 예외 매핑 추가
- [x] `DELETE /posts/{postId}` Web 어댑터 추가
- [x] 삭제 API Swagger 문서 추가
- [x] 인증 필요 보안 정책 확인

## 검증

- [x] 애플리케이션 서비스 테스트 추가
- [x] Web 어댑터 테스트 추가
- [x] 영속성 어댑터 테스트 추가
- [x] 상세 조회와 목록 조회 회귀 테스트 추가
- [x] `./gradlew.bat test` 실행
