package com.example.post.member.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.member.application.exception.DuplicateEmailException;
import com.example.post.member.application.port.in.SignupCommand;
import com.example.post.member.application.port.in.SignupResult;
import com.example.post.member.application.port.out.MemberRepositoryPort;
import com.example.post.member.application.port.out.PasswordEncoderPort;
import com.example.post.member.domain.model.Member;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SignupServiceTest {

	private final FakeMemberRepositoryPort memberRepositoryPort = new FakeMemberRepositoryPort();
	private final FakePasswordEncoderPort passwordEncoderPort = new FakePasswordEncoderPort();
	private final SignupService signupService = new SignupService(memberRepositoryPort, passwordEncoderPort);

	@Test
	void signsUpMember() {
		SignupResult result = signupService.signup(
				new SignupCommand(" MINU@EXAMPLE.COM ", "password123", " minu ")
		);

		assertEquals(1L, result.id());
		assertEquals("minu@example.com", result.email());
		assertEquals("minu", result.nickname());
		assertEquals("minu@example.com", memberRepositoryPort.savedMember.getEmail());
		assertEquals("encoded-password123", memberRepositoryPort.savedMember.getPasswordHash());
		assertFalse(memberRepositoryPort.savedMember.getPasswordHash().equals("password123"));
	}

	@Test
	void rejectsDuplicateEmail() {
		memberRepositoryPort.existingEmails.put("minu@example.com", true);

		assertThrows(
				DuplicateEmailException.class,
				() -> signupService.signup(new SignupCommand("MINU@example.com", "password123", "minu"))
		);

		assertEquals(0, memberRepositoryPort.saveCount);
	}

	@Test
	void rejectsShortPassword() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> signupService.signup(new SignupCommand("minu@example.com", "short", "minu"))
		);

		assertEquals("password must be at least 8 characters", exception.getMessage());
	}

	private static class FakeMemberRepositoryPort implements MemberRepositoryPort {

		private final Map<String, Boolean> existingEmails = new HashMap<>();
		private Member savedMember;
		private int saveCount;

		@Override
		public boolean existsByEmail(String email) {
			return existingEmails.getOrDefault(email, false);
		}

		@Override
		public Member save(Member member) {
			saveCount++;
			savedMember = Member.rehydrate(
					1L,
					member.getEmail(),
					member.getPasswordHash(),
					member.getNickname(),
					Instant.parse("2026-05-20T00:00:00Z")
			);
			return savedMember;
		}
	}

	private static class FakePasswordEncoderPort implements PasswordEncoderPort {

		@Override
		public String encode(String rawPassword) {
			return "encoded-" + rawPassword;
		}
	}
}
