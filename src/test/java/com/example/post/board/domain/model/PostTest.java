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

		Post post = Post.create(" title ", " content ", " author ", createdAt);

		assertEquals("title", post.getTitle());
		assertEquals("content", post.getContent());
		assertEquals("author", post.getAuthor());
		assertEquals(createdAt, post.getCreatedAt());
		assertNotNull(post.getCreatedAt());
	}

	@Test
	void rejectsBlankTitle() {
		BusinessException exception = assertThrows(BusinessException.class, () -> Post.create(" ", "content", "author"));

		assertEquals(BoardErrorCode.POST_TITLE_REQUIRED, exception.errorCode());
		assertEquals("게시글 제목은 필수입니다.", exception.errorCode().description());
	}

	@Test
	void rejectsTooLongTitle() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Post.create("a".repeat(101), "content", "author")
		);

		assertEquals(BoardErrorCode.POST_TITLE_TOO_LONG, exception.errorCode());
		assertEquals("게시글 제목은 최대 100자까지 허용됩니다.", exception.errorCode().description());
	}

	@Test
	void rejectsBlankContent() {
		BusinessException exception = assertThrows(BusinessException.class, () -> Post.create("title", " ", "author"));

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsTooLongContent() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Post.create("title", "a".repeat(5_001), "author")
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsBlankAuthor() {
		BusinessException exception = assertThrows(BusinessException.class, () -> Post.create("title", "content", " "));

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsTooLongAuthor() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Post.create("title", "content", "a".repeat(51))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}
}
