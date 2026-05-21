package com.example.post.board.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PostTest {

	@Test
	void createsPostWithNormalizedValues() {
		Instant createdAt = Instant.parse("2026-05-20T00:00:00Z");

		Post post = Post.create(" title ", " content ", 1L, createdAt);

		assertEquals("title", post.getTitle());
		assertEquals("content", post.getContent());
		assertEquals(1L, post.getAuthorMemberId());
		assertEquals(createdAt, post.getCreatedAt());
		assertNotNull(post.getCreatedAt());
		assertFalse(post.isDeleted());
		assertNull(post.getDeletedAt());
	}

	@Test
	void deletesPostByAuthor() {
		Post post = Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		);

		post.deleteBy(2L, Instant.parse("2026-05-20T01:00:00Z"));

		assertEquals(1L, post.getId());
		assertTrue(post.isDeleted());
		assertEquals(Instant.parse("2026-05-20T01:00:00Z"), post.getDeletedAt());
	}

	@Test
	void rejectsDeleteByNonAuthor() {
		Post post = Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> post.deleteBy(3L, Instant.parse("2026-05-20T01:00:00Z"))
		);

		assertEquals(BoardErrorCode.POST_DELETE_FORBIDDEN, exception.errorCode());
	}

	@Test
	void rejectsDeleteAlreadyDeletedPost() {
		Post post = Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z"),
				Instant.parse("2026-05-20T01:00:00Z")
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> post.deleteBy(2L, Instant.parse("2026-05-20T02:00:00Z"))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
	}

	@Test
	void rejectsBlankTitle() {
		BusinessException exception = assertThrows(BusinessException.class, () -> Post.create(" ", "content", 1L));

		assertEquals(BoardErrorCode.POST_TITLE_REQUIRED, exception.errorCode());
		assertEquals("게시글 제목은 필수입니다.", exception.errorCode().description());
	}

	@Test
	void rejectsTooLongTitle() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Post.create("a".repeat(101), "content", 1L)
		);

		assertEquals(BoardErrorCode.POST_TITLE_TOO_LONG, exception.errorCode());
		assertEquals("게시글 제목은 최대 100자까지 허용됩니다.", exception.errorCode().description());
	}

	@Test
	void rejectsBlankContent() {
		BusinessException exception = assertThrows(BusinessException.class, () -> Post.create("title", " ", 1L));

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsTooLongContent() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Post.create("title", "a".repeat(5_001), 1L)
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsNullAuthorMemberId() {
		BusinessException exception = assertThrows(BusinessException.class, () -> Post.create("title", "content", null));

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsNonPositiveAuthorMemberId() {
		BusinessException exception = assertThrows(BusinessException.class, () -> Post.create("title", "content", 0L));

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}
}
