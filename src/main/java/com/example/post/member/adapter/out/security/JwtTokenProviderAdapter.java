package com.example.post.member.adapter.out.security;

import com.example.post.global.config.JwtProperties;
import com.example.post.member.application.exception.InvalidRefreshTokenException;
import com.example.post.member.application.port.in.TokenResult;
import com.example.post.member.application.port.out.TokenProviderPort;
import com.example.post.member.domain.model.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

	private static final String TOKEN_TYPE_CLAIM = "tokenType";
	private static final String ACCESS_TOKEN_TYPE = "ACCESS";
	private static final String REFRESH_TOKEN_TYPE = "REFRESH";
	private static final String BEARER_TOKEN_TYPE = "Bearer";

	private final JwtProperties jwtProperties;
	private final Clock clock;

	@Autowired
	public JwtTokenProviderAdapter(JwtProperties jwtProperties) {
		this(jwtProperties, Clock.systemUTC());
	}

	JwtTokenProviderAdapter(JwtProperties jwtProperties, Clock clock) {
		this.jwtProperties = jwtProperties;
		this.clock = clock;
	}

	@Override
	public TokenResult issue(Member member) {
		Instant now = Instant.now(clock);
		String accessToken = issueAccessToken(member, now);
		String refreshToken = issueRefreshToken(member, now);
		return new TokenResult(
				BEARER_TOKEN_TYPE,
				accessToken,
				refreshToken,
				jwtProperties.getAccessToken().getExpirationSeconds()
		);
	}

	@Override
	public Long extractRefreshTokenMemberId(String refreshToken) {
		try {
			Claims claims = Jwts.parser()
					.verifyWith(secretKey(jwtProperties.getRefreshToken().getSecret()))
					.clock(() -> Date.from(Instant.now(clock)))
					.build()
					.parseSignedClaims(refreshToken)
					.getPayload();

			if (!REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class))) {
				throw new InvalidRefreshTokenException();
			}
			return Long.valueOf(claims.getSubject());
		}
		catch (JwtException | IllegalArgumentException exception) {
			throw new InvalidRefreshTokenException();
		}
	}

	@Override
	public Duration refreshTokenTtl() {
		return Duration.ofSeconds(jwtProperties.getRefreshToken().getExpirationSeconds());
	}

	private String issueAccessToken(Member member, Instant now) {
		return Jwts.builder()
				.subject(member.getId().toString())
				.claim("email", member.getEmail())
				.claim("nickname", member.getNickname())
				.claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(jwtProperties.getAccessToken().getExpirationSeconds())))
				.signWith(secretKey(jwtProperties.getAccessToken().getSecret()))
				.compact();
	}

	private String issueRefreshToken(Member member, Instant now) {
		return Jwts.builder()
				.subject(member.getId().toString())
				.claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(jwtProperties.getRefreshToken().getExpirationSeconds())))
				.signWith(secretKey(jwtProperties.getRefreshToken().getSecret()))
				.compact();
	}

	private static SecretKey secretKey(String secret) {
		return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
	}
}
