package com.example.post.board.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
