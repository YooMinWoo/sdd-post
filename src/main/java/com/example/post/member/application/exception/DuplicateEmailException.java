package com.example.post.member.application.exception;

import com.example.post.global.exception.BusinessException;
import com.example.post.member.exception.MemberErrorCode;

public class DuplicateEmailException extends BusinessException {

	public DuplicateEmailException() {
		super(MemberErrorCode.DUPLICATE_EMAIL);
	}
}
