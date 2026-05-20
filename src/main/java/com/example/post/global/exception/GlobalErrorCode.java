package com.example.post.global.exception;

public enum GlobalErrorCode implements ErrorCode {
	INVALID_REQUEST("요청 값이 올바르지 않습니다."),
	MALFORMED_JSON("요청 본문 형식이 올바르지 않습니다."),
	INTERNAL_SERVER_ERROR("예상하지 못한 서버 오류가 발생했습니다.");

	private final String description;

	GlobalErrorCode(String description) {
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
