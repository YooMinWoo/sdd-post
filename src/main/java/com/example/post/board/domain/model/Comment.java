package com.example.post.board.domain.model;

import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.time.Instant;
import java.util.Objects;

public class Comment {

	private static final int MAX_CONTENT_LENGTH = 1_000;

	private Long id;
	private Long postId;
	private Long authorMemberId;
	private String content;
	private Instant createdAt;

	private Comment(Long id, Long postId, Long authorMemberId, String content, Instant createdAt) {
		this.id = id;
		this.postId = validateId(postId);
		this.authorMemberId = validateId(authorMemberId);
		this.content = validateContent(content);
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
	}

	public static Comment create(Long postId, Long authorMemberId, String content) {
		return create(postId, authorMemberId, content, Instant.now());
	}

	public static Comment create(Long postId, Long authorMemberId, String content, Instant createdAt) {
		return new Comment(null, postId, authorMemberId, content, createdAt);
	}

	public static Comment rehydrate(Long id, Long postId, Long authorMemberId, String content, Instant createdAt) {
		if (id == null) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return new Comment(id, postId, authorMemberId, content, createdAt);
	}

	public void deleteBy(Long requestedPostId, Long requesterMemberId) {
		Long validatedPostId = validateId(requestedPostId);
		Long validatedRequesterMemberId = validateId(requesterMemberId);
		if (!postId.equals(validatedPostId)) {
			throw new BusinessException(BoardErrorCode.COMMENT_NOT_FOUND);
		}
		if (!authorMemberId.equals(validatedRequesterMemberId)) {
			throw new BusinessException(BoardErrorCode.COMMENT_DELETE_FORBIDDEN);
		}
	}

	public void updateBy(Long requestedPostId, Long requesterMemberId, String content) {
		Long validatedPostId = validateId(requestedPostId);
		Long validatedRequesterMemberId = validateId(requesterMemberId);
		if (!postId.equals(validatedPostId)) {
			throw new BusinessException(BoardErrorCode.COMMENT_NOT_FOUND);
		}
		if (!authorMemberId.equals(validatedRequesterMemberId)) {
			throw new BusinessException(BoardErrorCode.COMMENT_UPDATE_FORBIDDEN);
		}
		this.content = validateContent(content);
	}

	private static Long validateId(Long id) {
		if (id == null || id <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return id;
	}

	private static String validateContent(String content) {
		if (content == null) {
			throw new BusinessException(BoardErrorCode.COMMENT_CONTENT_REQUIRED);
		}
		String normalized = content.trim();
		if (normalized.isBlank()) {
			throw new BusinessException(BoardErrorCode.COMMENT_CONTENT_REQUIRED);
		}
		if (normalized.length() > MAX_CONTENT_LENGTH) {
			throw new BusinessException(BoardErrorCode.COMMENT_CONTENT_TOO_LONG);
		}
		return normalized;
	}

	public Long getId() {
		return id;
	}

	public Long getPostId() {
		return postId;
	}

	public Long getAuthorMemberId() {
		return authorMemberId;
	}

	public String getContent() {
		return content;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
