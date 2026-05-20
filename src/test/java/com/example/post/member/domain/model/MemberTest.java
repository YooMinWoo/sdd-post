package com.example.post.member.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import com.example.post.member.exception.MemberErrorCode;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class MemberTest {

	@Test
	void createsMember() {
		Member member = Member.create(
				" MINU@EXAMPLE.COM ",
				"$2a$10$abcdefghijklmnopqrstuvabcdefghiabcdefghiabcdefghiabcdef",
				" minu ",
				Instant.parse("2026-05-20T00:00:00Z")
		);

		assertEquals("minu@example.com", member.getEmail());
		assertEquals("minu", member.getNickname());
		assertEquals(Instant.parse("2026-05-20T00:00:00Z"), member.getCreatedAt());
	}

	@Test
	void rejectsBlankEmail() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Member.create("", "passwordHash", "minu")
		);

		assertEquals(MemberErrorCode.INVALID_EMAIL, exception.errorCode());
		assertEquals("이메일 형식이 올바르지 않습니다.", exception.errorCode().description());
	}

	@Test
	void rejectsInvalidEmail() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Member.create("invalid-email", "passwordHash", "minu")
		);

		assertEquals(MemberErrorCode.INVALID_EMAIL, exception.errorCode());
	}

	@Test
	void rejectsBlankPasswordHash() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Member.create("minu@example.com", " ", "minu")
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsTooLongNickname() {
		String nickname = "a".repeat(51);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Member.create("minu@example.com", "passwordHash", nickname)
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}
}
