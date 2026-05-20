package com.example.post.member.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.member.application.exception.InvalidCredentialsException;
import com.example.post.member.application.port.in.LoginCommand;
import com.example.post.member.application.port.in.TokenResult;
import com.example.post.member.application.port.out.AccessTokenMemberClaims;
import com.example.post.member.application.port.out.MemberRepositoryPort;
import com.example.post.member.application.port.out.PasswordMatcherPort;
import com.example.post.member.application.port.out.RefreshTokenStorePort;
import com.example.post.member.application.port.out.TokenProviderPort;
import com.example.post.member.domain.model.Member;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

	private final FakeMemberRepositoryPort memberRepositoryPort = new FakeMemberRepositoryPort();
	private final FakePasswordMatcherPort passwordMatcherPort = new FakePasswordMatcherPort();
	private final FakeTokenProviderPort tokenProviderPort = new FakeTokenProviderPort();
	private final FakeRefreshTokenStorePort refreshTokenStorePort = new FakeRefreshTokenStorePort();
	private final LoginService loginService = new LoginService(
			memberRepositoryPort,
			passwordMatcherPort,
			tokenProviderPort,
			refreshTokenStorePort
	);

	@Test
	void logsInAndStoresRefreshToken() {
		memberRepositoryPort.member = Optional.of(member());

		TokenResult result = loginService.login(new LoginCommand(" MINU@EXAMPLE.COM ", "password123"));

		assertEquals("Bearer", result.tokenType());
		assertEquals("access-token", result.accessToken());
		assertEquals("refresh-token", result.refreshToken());
		assertEquals(900, result.expiresIn());
		assertEquals("minu@example.com", memberRepositoryPort.requestedEmail);
		assertEquals(1L, refreshTokenStorePort.savedMemberId);
		assertEquals("refresh-token", refreshTokenStorePort.savedRefreshToken);
		assertEquals(Duration.ofDays(14), refreshTokenStorePort.savedTtl);
	}

	@Test
	void rejectsMissingEmailWithoutExposingReason() {
		assertThrows(
				InvalidCredentialsException.class,
				() -> loginService.login(new LoginCommand("missing@example.com", "password123"))
		);
	}

	@Test
	void rejectsWrongPasswordWithoutExposingReason() {
		memberRepositoryPort.member = Optional.of(member());
		passwordMatcherPort.matches = false;

		assertThrows(
				InvalidCredentialsException.class,
				() -> loginService.login(new LoginCommand("minu@example.com", "wrong-password"))
		);
	}

	private static Member member() {
		return Member.rehydrate(
				1L,
				"minu@example.com",
				"encoded-password",
				"minu",
				Instant.parse("2026-05-20T00:00:00Z")
		);
	}

	private static class FakeMemberRepositoryPort implements MemberRepositoryPort {

		private Optional<Member> member = Optional.empty();
		private String requestedEmail;

		@Override
		public boolean existsByEmail(String email) {
			return false;
		}

		@Override
		public Optional<Member> findByEmail(String email) {
			requestedEmail = email;
			return member;
		}

		@Override
		public Optional<Member> findById(Long id) {
			return Optional.empty();
		}

		@Override
		public Member save(Member member) {
			return member;
		}
	}

	private static class FakePasswordMatcherPort implements PasswordMatcherPort {

		private boolean matches = true;

		@Override
		public boolean matches(String rawPassword, String encodedPassword) {
			return matches;
		}
	}

	private static class FakeTokenProviderPort implements TokenProviderPort {

		@Override
		public TokenResult issue(Member member) {
			return new TokenResult("Bearer", "access-token", "refresh-token", 900);
		}

		@Override
		public AccessTokenMemberClaims extractAccessTokenMember(String accessToken) {
			return new AccessTokenMemberClaims(1L, "minu@example.com", "minu");
		}

		@Override
		public Long extractRefreshTokenMemberId(String refreshToken) {
			return 1L;
		}

		@Override
		public Duration refreshTokenTtl() {
			return Duration.ofDays(14);
		}
	}

	private static class FakeRefreshTokenStorePort implements RefreshTokenStorePort {

		private Long savedMemberId;
		private String savedRefreshToken;
		private Duration savedTtl;

		@Override
		public void save(Long memberId, String refreshToken, Duration ttl) {
			savedMemberId = memberId;
			savedRefreshToken = refreshToken;
			savedTtl = ttl;
		}

		@Override
		public Optional<String> findByMemberId(Long memberId) {
			return Optional.empty();
		}

		@Override
		public void deleteByMemberId(Long memberId) {
		}
	}
}
