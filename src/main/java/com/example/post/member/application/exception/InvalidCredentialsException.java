package com.example.post.member.application.exception;

import com.example.post.global.exception.BusinessException;
import com.example.post.member.exception.MemberErrorCode;

public class InvalidCredentialsException extends BusinessException {

	public InvalidCredentialsException() {
		super(MemberErrorCode.INVALID_CREDENTIALS);
	}
}
