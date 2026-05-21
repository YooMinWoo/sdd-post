package com.example.post.board.exception;

import com.example.post.global.exception.ErrorCode;

public enum BoardErrorCode implements ErrorCode {
	POST_TITLE_REQUIRED("게시글 제목은 필수입니다."),
	POST_TITLE_TOO_LONG("게시글 제목은 최대 100자까지 허용됩니다."),
	POST_NOT_FOUND("게시글을 찾을 수 없습니다."),
	POST_DELETE_FORBIDDEN("게시글 삭제 권한이 없습니다."),
	POST_UPDATE_FORBIDDEN("게시글 수정 권한이 없습니다."),
	COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다."),
	COMMENT_CONTENT_REQUIRED("댓글 본문은 필수입니다."),
	COMMENT_CONTENT_TOO_LONG("댓글 본문은 최대 1,000자까지 허용됩니다."),
	COMMENT_DELETE_FORBIDDEN("댓글 삭제 권한이 없습니다."),
	COMMENT_UPDATE_FORBIDDEN("댓글 수정 권한이 없습니다.");

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
