package com.example.post.member.application.exception;

import com.example.post.global.exception.BusinessException;
import com.example.post.member.exception.MemberErrorCode;

public class InvalidAccessTokenException extends BusinessException {

	public InvalidAccessTokenException() {
		super(MemberErrorCode.INVALID_ACCESS_TOKEN);
	}
}
