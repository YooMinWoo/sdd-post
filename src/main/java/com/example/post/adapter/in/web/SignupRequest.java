package com.example.post.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청")
public record SignupRequest(
		@Schema(description = "로그인에 사용할 이메일. 앞뒤 공백은 제거되고 소문자로 저장됩니다.", example = "minu@example.com")
		String email,

		@Schema(description = "비밀번호. 최소 8자 이상이어야 하며 저장 시 BCrypt로 암호화됩니다.", example = "password123")
		String password,

		@Schema(description = "게시판에서 표시할 닉네임. 앞뒤 공백은 제거되며 최대 50자까지 허용됩니다.", example = "minu")
		String nickname
) {
}
