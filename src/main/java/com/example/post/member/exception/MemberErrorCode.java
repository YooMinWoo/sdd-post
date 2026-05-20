package com.example.post.member.exception;

import com.example.post.global.exception.ErrorCode;

public enum MemberErrorCode implements ErrorCode {
	INVALID_EMAIL("이메일 형식이 올바르지 않습니다."),
	PASSWORD_REQUIRED("비밀번호는 필수입니다."),
	PASSWORD_TOO_SHORT("비밀번호는 최소 8자 이상이어야 합니다."),
	DUPLICATE_EMAIL("이미 가입된 이메일입니다."),
	INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다."),
	INVALID_REFRESH_TOKEN("유효하지 않은 refreshToken입니다.");

	private final String description;

	MemberErrorCode(String description) {
		this.description = description;
	}

	@Override
	public String code() {
		return name();
	}

	@Override
	public String description() {
		return description;
	}
}
