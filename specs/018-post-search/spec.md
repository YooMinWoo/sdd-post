# 018 게시글 검색 명세

## 목표

게시판 사용자가 키워드로 게시글 목록을 검색할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 관심 있는 단어가 포함된 게시글을 빠르게 찾고 싶다.

## 범위

- 게시글 목록 조회 API에 `keyword` 요청 파라미터를 추가한다.
- 검색 대상은 게시글 제목과 본문이다.
- 검색 결과는 기존 목록 조회와 같은 페이지 응답 구조를 사용한다.
- 검색 결과는 게시글 생성 시각 기준 최신순으로 정렬한다.
- 삭제된 게시글은 검색 결과에서 제외한다.
- 검색 결과 항목은 기존 목록 조회처럼 본문을 포함하지 않고 댓글 수를 포함한다.
- `keyword`가 없거나 공백뿐이면 기존 전체 목록 조회와 동일하게 동작한다.
- 검색은 대소문자를 구분하지 않는다.

## 비범위

- 작성자 닉네임 검색
- 댓글 본문 검색
- 태그/카테고리 검색
- 검색어 하이라이트
- 검색 정확도 점수 정렬
- 전문 검색 엔진 연동
- 검색어 자동완성

## 도메인 규칙

- `page`는 0 이상이어야 한다.
- `size`는 1 이상 100 이하여야 한다.
- `keyword`는 앞뒤 공백을 제거해 사용한다.
- 공백 제거 후 빈 `keyword`는 검색 조건으로 사용하지 않는다.
- 검색 결과도 작성자 닉네임과 댓글 수를 배치 조회한다.

## API

```http
GET /posts?keyword=spring&page=0&size=10
```

성공 응답:

```http
200 OK
```

```json
{
  "success": true,
  "message": "게시글 목록을 조회했습니다.",
  "data": {
    "posts": [
      {
        "id": 2,
        "title": "Spring Boot 게시글",
        "authorMemberId": 1,
        "author": "minu",
        "createdAt": "2026-05-20T01:00:00Z",
        "commentCount": 3
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "path": null,
  "timestamp": "2026-05-22T00:00:00Z",
  "errors": []
}
```

검색 결과 없음 응답:

```json
{
  "success": true,
  "message": "게시글 목록을 조회했습니다.",
  "data": {
    "posts": [],
    "page": 0,
    "size": 10,
    "totalElements": 0,
    "totalPages": 0,
    "first": true,
    "last": true
  },
  "path": null,
  "timestamp": "2026-05-22T00:00:00Z",
  "errors": []
}
```
