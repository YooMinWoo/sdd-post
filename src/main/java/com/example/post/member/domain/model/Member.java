package com.example.post.member.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class Member {

	private static final int MAX_EMAIL_LENGTH = 255;
	private static final int MAX_NICKNAME_LENGTH = 50;
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private final Long id;
	private final String email;
	private final String passwordHash;
	private final String nickname;
	private final Instant createdAt;

	private Member(Long id, String email, String passwordHash, String nickname, Instant createdAt) {
		this.id = id;
		this.email = validateEmail(email);
		this.passwordHash = validateRequired("passwordHash", passwordHash, 255);
		this.nickname = validateRequired("nickname", nickname, MAX_NICKNAME_LENGTH);
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
	}

	public static Member create(String email, String passwordHash, String nickname) {
		return create(email, passwordHash, nickname, Instant.now());
	}

	public static Member create(String email, String passwordHash, String nickname, Instant createdAt) {
		return new Member(null, email, passwordHash, nickname, createdAt);
	}

	public static Member rehydrate(Long id, String email, String passwordHash, String nickname, Instant createdAt) {
		if (id == null) {
			throw new IllegalArgumentException("id must not be null");
		}
		return new Member(id, email, passwordHash, nickname, createdAt);
	}

	private static String validateEmail(String email) {
		String normalized = validateRequired("email", email, MAX_EMAIL_LENGTH).toLowerCase(Locale.ROOT);
		if (!EMAIL_PATTERN.matcher(normalized).matches()) {
			throw new IllegalArgumentException("email must be valid");
		}
		return normalized;
	}

	private static String validateRequired(String fieldName, String value, int maxLength) {
		if (value == null) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		String normalized = value.trim();
		if (normalized.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}
		if (normalized.length() > maxLength) {
			throw new IllegalArgumentException(fieldName + " must be at most " + maxLength + " characters");
		}
		return normalized;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getNickname() {
		return nickname;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
