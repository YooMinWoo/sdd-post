package com.example.post.member.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.member.application.exception.InvalidRefreshTokenException;
import com.example.post.member.application.port.in.RefreshTokenCommand;
import com.example.post.member.application.port.in.TokenResult;
import com.example.post.member.application.port.out.MemberRepositoryPort;
import com.example.post.member.application.port.out.RefreshTokenStorePort;
import com.example.post.member.application.port.out.TokenProviderPort;
import com.example.post.member.domain.model.Member;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RefreshTokenServiceTest {

	private final FakeMemberRepositoryPort memberRepositoryPort = new FakeMemberRepositoryPort();
	private final FakeTokenProviderPort tokenProviderPort = new FakeTokenProviderPort();
	private final FakeRefreshTokenStorePort refreshTokenStorePort = new FakeRefreshTokenStorePort();
	private final RefreshTokenService refreshTokenService = new RefreshTokenService(
			memberRepositoryPort,
			tokenProviderPort,
			refreshTokenStorePort
	);

	@Test
	void refreshesAndRotatesRefreshToken() {
		memberRepositoryPort.member = Optional.of(member());
		refreshTokenStorePort.storedRefreshToken = Optional.of("old-refresh-token");

		TokenResult result = refreshTokenService.refresh(new RefreshTokenCommand("old-refresh-token"));

		assertEquals("new-access-token", result.accessToken());
		assertEquals("new-refresh-token", result.refreshToken());
		assertEquals(1L, refreshTokenStorePort.savedMemberId);
		assertEquals("new-refresh-token", refreshTokenStorePort.savedRefreshToken);
		assertEquals(Duration.ofDays(14), refreshTokenStorePort.savedTtl);
	}

	@Test
	void rejectsWhenStoredRefreshTokenDiffers() {
		refreshTokenStorePort.storedRefreshToken = Optional.of("other-refresh-token");

		assertThrows(
				InvalidRefreshTokenException.class,
				() -> refreshTokenService.refresh(new RefreshTokenCommand("old-refresh-token"))
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

		@Override
		public boolean existsByEmail(String email) {
			return false;
		}

		@Override
		public Optional<Member> findByEmail(String email) {
			return Optional.empty();
		}

		@Override
		public Optional<Member> findById(Long id) {
			return member;
		}

		@Override
		public Member save(Member member) {
			return member;
		}
	}

	private static class FakeTokenProviderPort implements TokenProviderPort {

		@Override
		public TokenResult issue(Member member) {
			return new TokenResult("Bearer", "new-access-token", "new-refresh-token", 900);
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

		private Optional<String> storedRefreshToken = Optional.empty();
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
			return storedRefreshToken;
		}

		@Override
		public void deleteByMemberId(Long memberId) {
		}
	}
}
