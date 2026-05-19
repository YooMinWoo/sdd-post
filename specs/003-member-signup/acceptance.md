# 003 회원가입 인수 조건

## 성공 조건

- 유효한 이메일, 비밀번호, 닉네임으로 `POST /auth/signup`을 호출하면 `201 Created`를 반환한다.
- 응답에는 생성된 회원 id, email, nickname, createdAt이 포함된다.
- 응답에는 비밀번호 원문이나 비밀번호 해시가 포함되지 않는다.
- 저장된 회원의 비밀번호는 BCrypt 해시 형태다.

## 잘못된 입력 조건

- 빈 이메일은 `400 Bad Request`를 반환한다.
- 이메일 형식이 아니면 `400 Bad Request`를 반환한다.
- 8자 미만 비밀번호는 `400 Bad Request`를 반환한다.
- 빈 닉네임은 `400 Bad Request`를 반환한다.
- 50자를 초과한 닉네임은 `400 Bad Request`를 반환한다.
- 이미 가입된 이메일은 `409 Conflict`와 `DUPLICATE_EMAIL`을 반환한다.

## 완료 기준

- 도메인, 애플리케이션, Web 어댑터, Persistence 어댑터 테스트가 기능을 검증한다.
- Swagger에서 회원가입 요청, 성공 응답, 실패 응답 예시를 확인할 수 있다.
- `.\gradlew.bat test`가 통과하거나 실패 사유가 기록된다.
