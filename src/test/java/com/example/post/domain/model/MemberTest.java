package com.example.post.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> Member.create("", "passwordHash", "minu")
		);

		assertEquals("email is required", exception.getMessage());
	}

	@Test
	void rejectsInvalidEmail() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> Member.create("invalid-email", "passwordHash", "minu")
		);

		assertEquals("email must be valid", exception.getMessage());
	}

	@Test
	void rejectsBlankPasswordHash() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> Member.create("minu@example.com", " ", "minu")
		);

		assertEquals("passwordHash is required", exception.getMessage());
	}

	@Test
	void rejectsTooLongNickname() {
		String nickname = "a".repeat(51);

		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> Member.create("minu@example.com", "passwordHash", nickname)
		);

		assertEquals("nickname must be at most 50 characters", exception.getMessage());
	}
}
