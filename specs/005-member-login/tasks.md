# 005 로그인 작업

## 문서 작업

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `tasks.md` 작성
- [x] `docs/INDEX.md` 갱신
- [x] `docs/DOMAIN_GLOSSARY.md` 갱신

## 향후 구현 작업

- [x] Redis 의존성 추가
- [x] JWT 의존성 추가
- [x] JWT 설정 프로퍼티 추가
- [x] Redis 설정 프로퍼티 추가
- [x] `MemberRepositoryPort`에 이메일 조회 메서드 추가
- [x] `PasswordMatcherPort` 추가 및 BCrypt 어댑터 확장
- [x] `TokenProviderPort` 추가 및 JWT 어댑터 구현
- [x] `RefreshTokenStorePort` 추가 및 Redis 어댑터 구현
- [x] 로그인/토큰 재발급/로그아웃 유스케이스 구현
- [x] 인증 실패 예외와 refreshToken 실패 예외 추가
- [x] 전역 예외 처리에서 `401` 응답 처리
- [x] `AuthController`에 로그인/재발급/로그아웃 API 추가
- [x] Swagger 문서화 추가
- [x] 애플리케이션 테스트 추가
- [x] Web 어댑터 테스트 추가
- [x] Redis 어댑터 테스트 추가
- [x] JWT 어댑터 테스트 추가
- [x] `.\gradlew.bat test` 실행
