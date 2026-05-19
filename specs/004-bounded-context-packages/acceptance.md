# 004 bounded context 패키지 분리 인수 조건

## 성공 조건

- 게시글 작성 관련 코드는 `com.example.post.board` 아래에 위치한다.
- 회원가입 관련 코드는 `com.example.post.member` 아래에 위치한다.
- 공통 에러 처리와 설정은 `com.example.post.global` 아래에 위치한다.
- 기존 API 경로와 응답 형식은 변경되지 않는다.
- 전체 테스트가 통과한다.

## 완료 기준

- 운영 코드와 테스트 코드가 bounded context 기준 패키지 구조를 따른다.
- 문서가 새 패키지 구조를 설명한다.
- `.\gradlew.bat test`가 통과하거나 실패 사유가 기록된다.
