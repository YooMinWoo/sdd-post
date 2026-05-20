package com.example.post.member.application.exception;

import com.example.post.global.exception.BusinessException;
import com.example.post.member.exception.MemberErrorCode;

public class InvalidRefreshTokenException extends BusinessException {

	public InvalidRefreshTokenException() {
		super(MemberErrorCode.INVALID_REFRESH_TOKEN);
	}
}
