package com.example.post.member.domain.model;

import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.ErrorCode;
import com.example.post.global.exception.GlobalErrorCode;
import com.example.post.member.exception.MemberErrorCode;
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
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return new Member(id, email, passwordHash, nickname, createdAt);
	}

	private static String validateEmail(String email) {
		String normalized = validateRequired("email", email, MAX_EMAIL_LENGTH, MemberErrorCode.INVALID_EMAIL).toLowerCase(Locale.ROOT);
		if (!EMAIL_PATTERN.matcher(normalized).matches()) {
			throw new BusinessException(MemberErrorCode.INVALID_EMAIL);
		}
		return normalized;
	}

	private static String validateRequired(String fieldName, String value, int maxLength) {
		return validateRequired(fieldName, value, maxLength, GlobalErrorCode.INVALID_REQUEST);
	}

	private static String validateRequired(String fieldName, String value, int maxLength, ErrorCode errorCode) {
		if (value == null) {
			throw new BusinessException(errorCode);
		}

		String normalized = value.trim();
		if (normalized.isBlank()) {
			throw new BusinessException(errorCode);
		}
		if (normalized.length() > maxLength) {
			throw new BusinessException(errorCode);
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
