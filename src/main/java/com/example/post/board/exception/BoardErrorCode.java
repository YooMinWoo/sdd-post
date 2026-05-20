package com.example.post.board.exception;

import com.example.post.global.exception.ErrorCode;

public enum BoardErrorCode implements ErrorCode {
	POST_TITLE_REQUIRED("게시글 제목은 필수입니다."),
	POST_TITLE_TOO_LONG("게시글 제목은 최대 100자까지 허용됩니다."),
	POST_NOT_FOUND("게시글을 찾을 수 없습니다.");

	private final String description;

	BoardErrorCode(String description) {
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
