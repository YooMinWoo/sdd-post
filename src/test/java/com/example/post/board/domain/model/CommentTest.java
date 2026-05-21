package com.example.post.board.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CommentTest {

	@Test
	void createsCommentWithNormalizedContent() {
		Instant createdAt = Instant.parse("2026-05-21T00:00:00Z");

		Comment comment = Comment.create(1L, 2L, " content ", createdAt);

		assertNull(comment.getId());
		assertEquals(1L, comment.getPostId());
		assertEquals(2L, comment.getAuthorMemberId());
		assertEquals("content", comment.getContent());
		assertEquals(createdAt, comment.getCreatedAt());
	}

	@Test
	void rehydratesComment() {
		Instant createdAt = Instant.parse("2026-05-21T00:00:00Z");

		Comment comment = Comment.rehydrate(1L, 2L, 3L, "content", createdAt);

		assertEquals(1L, comment.getId());
		assertEquals(2L, comment.getPostId());
		assertEquals(3L, comment.getAuthorMemberId());
		assertEquals("content", comment.getContent());
		assertEquals(createdAt, comment.getCreatedAt());
	}

	@Test
	void updatesCommentByAuthorWithNormalizedContent() {
		Comment comment = Comment.rehydrate(
				1L,
				2L,
				3L,
				"content",
				Instant.parse("2026-05-21T00:00:00Z")
		);

		comment.updateBy(2L, 3L, " updated ");

		assertEquals("updated", comment.getContent());
	}

	@Test
	void rejectsUpdateByNonAuthor() {
		Comment comment = Comment.rehydrate(
				1L,
				2L,
				3L,
				"content",
				Instant.parse("2026-05-21T00:00:00Z")
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> comment.updateBy(2L, 4L, "updated")
		);

		assertEquals(BoardErrorCode.COMMENT_UPDATE_FORBIDDEN, exception.errorCode());
	}

	@Test
	void rejectsNullContent() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Comment.create(1L, 2L, null)
		);

		assertEquals(BoardErrorCode.COMMENT_CONTENT_REQUIRED, exception.errorCode());
	}

	@Test
	void rejectsBlankContent() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Comment.create(1L, 2L, " ")
		);

		assertEquals(BoardErrorCode.COMMENT_CONTENT_REQUIRED, exception.errorCode());
		assertEquals("댓글 본문은 필수입니다.", exception.errorCode().description());
	}

	@Test
	void rejectsTooLongContent() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Comment.create(1L, 2L, "a".repeat(1_001))
		);

		assertEquals(BoardErrorCode.COMMENT_CONTENT_TOO_LONG, exception.errorCode());
		assertEquals("댓글 본문은 최대 1,000자까지 허용됩니다.", exception.errorCode().description());
	}

	@Test
	void rejectsInvalidPostId() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Comment.create(0L, 2L, "content")
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsInvalidAuthorMemberId() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Comment.create(1L, 0L, "content")
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsNullIdWhenRehydrating() {
		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> Comment.rehydrate(null, 1L, 2L, "content", Instant.parse("2026-05-21T00:00:00Z"))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}
}
