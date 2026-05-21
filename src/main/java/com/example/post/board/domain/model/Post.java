package com.example.post.board.domain.model;

import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.ErrorCode;
import com.example.post.global.exception.GlobalErrorCode;
import java.time.Instant;
import java.util.Objects;

public class Post {

	private static final int MAX_TITLE_LENGTH = 100;
	private static final int MAX_CONTENT_LENGTH = 5_000;

	private Long id;
	private Long authorMemberId;
	private String title;
	private String content;
	private Instant createdAt;
	private Instant deletedAt;

	private Post(Long id, String title, String content, Long authorMemberId, Instant createdAt, Instant deletedAt) {
		this.id = id;
		this.title = validateRequired("title", title, MAX_TITLE_LENGTH);
		this.content = validateRequired("content", content, MAX_CONTENT_LENGTH);
		this.authorMemberId = validateAuthorMemberId(authorMemberId);
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
		this.deletedAt = deletedAt;
	}

	public static Post create(String title, String content, Long authorMemberId) {
		return create(title, content, authorMemberId, Instant.now());
	}

	public static Post create(String title, String content, Long authorMemberId, Instant createdAt) {
		return new Post(null, title, content, authorMemberId, createdAt, null);
	}

	public static Post rehydrate(Long id, String title, String content, Long authorMemberId, Instant createdAt) {
		return rehydrate(id, title, content, authorMemberId, createdAt, null);
	}

	public static Post rehydrate(
			Long id,
			String title,
			String content,
			Long authorMemberId,
			Instant createdAt,
			Instant deletedAt
	) {
		if (id == null) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return new Post(id, title, content, authorMemberId, createdAt, deletedAt);
	}

	public void deleteBy(Long requesterMemberId, Instant deletedAt) {
		Long validatedRequesterMemberId = validateAuthorMemberId(requesterMemberId);
		if (!authorMemberId.equals(validatedRequesterMemberId)) {
			throw new BusinessException(BoardErrorCode.POST_DELETE_FORBIDDEN);
		}
		if (isDeleted()) {
			throw new BusinessException(BoardErrorCode.POST_NOT_FOUND);
		}
		this.deletedAt = Objects.requireNonNull(deletedAt, "deletedAt must not be null");
	}

	public void updateBy(Long requesterMemberId, String title, String content) {
		Long validatedRequesterMemberId = validateAuthorMemberId(requesterMemberId);
		if (!authorMemberId.equals(validatedRequesterMemberId)) {
			throw new BusinessException(BoardErrorCode.POST_UPDATE_FORBIDDEN);
		}
		if (isDeleted()) {
			throw new BusinessException(BoardErrorCode.POST_NOT_FOUND);
		}
		this.title = validateRequired("title", title, MAX_TITLE_LENGTH);
		this.content = validateRequired("content", content, MAX_CONTENT_LENGTH);
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}

	private static String validateRequired(String fieldName, String value, int maxLength) {
		if (value == null) {
			throw new BusinessException(requiredErrorCode(fieldName));
		}

		String normalized = value.trim();
		if (normalized.isBlank()) {
			throw new BusinessException(requiredErrorCode(fieldName));
		}
		if (normalized.length() > maxLength) {
			throw new BusinessException(tooLongErrorCode(fieldName));
		}
		return normalized;
	}

	private static Long validateAuthorMemberId(Long authorMemberId) {
		if (authorMemberId == null || authorMemberId <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return authorMemberId;
	}

	private static ErrorCode requiredErrorCode(String fieldName) {
		if ("title".equals(fieldName)) {
			return BoardErrorCode.POST_TITLE_REQUIRED;
		}
		return GlobalErrorCode.INVALID_REQUEST;
	}

	private static ErrorCode tooLongErrorCode(String fieldName) {
		if ("title".equals(fieldName)) {
			return BoardErrorCode.POST_TITLE_TOO_LONG;
		}
		return GlobalErrorCode.INVALID_REQUEST;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public Long getAuthorMemberId() {
		return authorMemberId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getDeletedAt() {
		return deletedAt;
	}
}
