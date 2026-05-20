package com.example.post.member.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.post.member.application.exception.InvalidRefreshTokenException;
import com.example.post.member.application.port.in.LogoutCommand;
import com.example.post.member.application.port.in.TokenResult;
import com.example.post.member.application.port.out.AccessTokenMemberClaims;
import com.example.post.member.application.port.out.RefreshTokenStorePort;
import com.example.post.member.application.port.out.TokenProviderPort;
import com.example.post.member.domain.model.Member;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LogoutServiceTest {

	private final FakeTokenProviderPort tokenProviderPort = new FakeTokenProviderPort();
	private final FakeRefreshTokenStorePort refreshTokenStorePort = new FakeRefreshTokenStorePort();
	private final LogoutService logoutService = new LogoutService(tokenProviderPort, refreshTokenStorePort);

	@Test
	void deletesRefreshToken() {
		logoutService.logout(new LogoutCommand("refresh-token"));

		assertEquals(1L, refreshTokenStorePort.deletedMemberId);
	}

	@Test
	void treatsInvalidRefreshTokenAsSuccess() {
		tokenProviderPort.throwInvalidRefreshTokenException = true;

		logoutService.logout(new LogoutCommand("expired-refresh-token"));

		assertEquals(0, refreshTokenStorePort.deleteCount);
	}

	private static class FakeTokenProviderPort implements TokenProviderPort {

		private boolean throwInvalidRefreshTokenException;

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
			if (throwInvalidRefreshTokenException) {
				throw new InvalidRefreshTokenException();
			}
			return 1L;
		}

		@Override
		public Duration refreshTokenTtl() {
			return Duration.ofDays(14);
		}
	}

	private static class FakeRefreshTokenStorePort implements RefreshTokenStorePort {

		private Long deletedMemberId;
		private int deleteCount;

		@Override
		public void save(Long memberId, String refreshToken, Duration ttl) {
		}

		@Override
		public Optional<String> findByMemberId(Long memberId) {
			return Optional.empty();
		}

		@Override
		public void deleteByMemberId(Long memberId) {
			deleteCount++;
			deletedMemberId = memberId;
		}
	}
}
