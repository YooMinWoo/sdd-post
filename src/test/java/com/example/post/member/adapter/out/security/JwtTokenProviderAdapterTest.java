package com.example.post.member.adapter.out.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.global.config.JwtProperties;
import com.example.post.member.application.exception.InvalidRefreshTokenException;
import com.example.post.member.application.port.in.TokenResult;
import com.example.post.member.application.port.out.AccessTokenMemberClaims;
import com.example.post.member.domain.model.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtTokenProviderAdapterTest {

	private static final String ACCESS_SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
	private static final String REFRESH_SECRET = "YWJjZGVmMDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODk=";
	private static final Instant FIXED_NOW = Instant.parse("2026-05-20T00:00:00Z");

	private final JwtProperties jwtProperties = jwtProperties();
	private final JwtTokenProviderAdapter adapter = new JwtTokenProviderAdapter(
			jwtProperties,
			Clock.fixed(FIXED_NOW, ZoneOffset.UTC)
	);

	@Test
	void issuesTokenWithClaims() {
		TokenResult result = adapter.issue(member());

		Claims accessClaims = parse(result.accessToken(), ACCESS_SECRET);

		assertEquals("Bearer", result.tokenType());
		assertEquals(900, result.expiresIn());
		assertEquals("1", accessClaims.getSubject());
		assertEquals("minu@example.com", accessClaims.get("email", String.class));
		assertEquals("minu", accessClaims.get("nickname", String.class));
		assertEquals("ACCESS", accessClaims.get("tokenType", String.class));
		assertEquals(Instant.parse("2026-05-20T00:15:00Z"), accessClaims.getExpiration().toInstant());
	}

	@Test
	void extractsRefreshTokenMemberId() {
		TokenResult result = adapter.issue(member());

		assertEquals(1L, adapter.extractRefreshTokenMemberId(result.refreshToken()));
	}

	@Test
	void extractsAccessTokenMember() {
		TokenResult result = adapter.issue(member());

		AccessTokenMemberClaims claims = adapter.extractAccessTokenMember(result.accessToken());

		assertEquals(1L, claims.memberId());
		assertEquals("minu@example.com", claims.email());
		assertEquals("minu", claims.nickname());
	}

	@Test
	void rejectsAccessTokenAsRefreshToken() {
		TokenResult result = adapter.issue(member());

		assertThrows(
				InvalidRefreshTokenException.class,
				() -> adapter.extractRefreshTokenMemberId(result.accessToken())
		);
	}

	@Test
	void rejectsRefreshTokenAsAccessToken() {
		TokenResult result = adapter.issue(member());

		assertThrows(
				com.example.post.member.application.exception.InvalidAccessTokenException.class,
				() -> adapter.extractAccessTokenMember(result.refreshToken())
		);
	}

	private static Claims parse(String token, String secret) {
		return Jwts.parser()
				.verifyWith(secretKey(secret))
				.clock(() -> java.util.Date.from(FIXED_NOW))
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private static SecretKey secretKey(String secret) {
		return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
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

	private static JwtProperties jwtProperties() {
		JwtProperties properties = new JwtProperties();
		properties.getAccessToken().setSecret(ACCESS_SECRET);
		properties.getAccessToken().setExpirationSeconds(900);
		properties.getRefreshToken().setSecret(REFRESH_SECRET);
		properties.getRefreshToken().setExpirationSeconds(1209600);
		return properties;
	}
}
