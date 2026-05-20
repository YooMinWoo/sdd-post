package com.example.post.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private final Token accessToken = new Token();
	private final Token refreshToken = new Token();

	public Token getAccessToken() {
		return accessToken;
	}

	public Token getRefreshToken() {
		return refreshToken;
	}

	public static class Token {

		private String secret;
		private long expirationSeconds;

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public long getExpirationSeconds() {
			return expirationSeconds;
		}

		public void setExpirationSeconds(long expirationSeconds) {
			this.expirationSeconds = expirationSeconds;
		}
	}
}
