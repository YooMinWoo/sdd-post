# 018 게시글 검색 작업

## 문서화

- [x] `spec.md` 작성
- [x] `plan.md` 작성
- [x] `acceptance.md` 작성
- [x] `docs/INDEX.md` 갱신

## 구현

- [x] `ListPostsQuery`에 `keyword` 추가
- [x] 목록 조회 서비스에 검색 분기 추가
- [x] `PostRepositoryPort`에 키워드 검색 메서드 추가
- [x] 게시글 Persistence 어댑터에 제목/본문 검색 구현
- [x] `GET /posts` Web 어댑터에 `keyword` 파라미터 추가
- [x] 목록 조회 Swagger 문서 갱신

## 검증

- [x] 목록 조회 애플리케이션 서비스 테스트 갱신
- [x] 게시글 영속성 어댑터 테스트 갱신
- [x] Web 어댑터 테스트 갱신
- [x] `.\gradlew.bat test` 실행
