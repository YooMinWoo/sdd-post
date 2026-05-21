package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.application.port.in.CreateCommentCommand;
import com.example.post.board.application.port.in.CreateCommentResult;
import com.example.post.board.application.port.out.CommentPageResult;
import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.PostPageResult;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Comment;
import com.example.post.board.domain.model.Post;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CreateCommentServiceTest {

	@Test
	void createsAndSavesComment() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		CreateCommentService service = new CreateCommentService(postRepositoryPort, commentRepositoryPort);

		CreateCommentResult result = service.createComment(new CreateCommentCommand(1L, " comment ", 3L));

		assertEquals(1L, result.id());
		assertEquals(1L, commentRepositoryPort.savedComment.getPostId());
		assertEquals(3L, commentRepositoryPort.savedComment.getAuthorMemberId());
		assertEquals("comment", commentRepositoryPort.savedComment.getContent());
	}

	@Test
	void rejectsInvalidPostId() {
		CreateCommentService service = new CreateCommentService(
				new FakePostRepositoryPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.createComment(new CreateCommentCommand(0L, "comment", 1L))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsInvalidAuthorMemberId() {
		CreateCommentService service = new CreateCommentService(
				new FakePostRepositoryPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.createComment(new CreateCommentCommand(1L, "comment", 0L))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsMissingPost() {
		CreateCommentService service = new CreateCommentService(
				new FakePostRepositoryPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.createComment(new CreateCommentCommand(999L, "comment", 1L))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
	}

	@Test
	void rejectsDeletedPost() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z"),
				Instant.parse("2026-05-20T01:00:00Z")
		));
		CreateCommentService service = new CreateCommentService(postRepositoryPort, new FakeCommentRepositoryPort());

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.createComment(new CreateCommentCommand(1L, "comment", 1L))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
	}

	@Test
	void rejectsBlankContent() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		CreateCommentService service = new CreateCommentService(postRepositoryPort, new FakeCommentRepositoryPort());

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.createComment(new CreateCommentCommand(1L, " ", 1L))
		);

		assertEquals(BoardErrorCode.COMMENT_CONTENT_REQUIRED, exception.errorCode());
	}

	@Test
	void rejectsTooLongContent() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		CreateCommentService service = new CreateCommentService(postRepositoryPort, new FakeCommentRepositoryPort());

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.createComment(new CreateCommentCommand(1L, "a".repeat(1_001), 1L))
		);

		assertEquals(BoardErrorCode.COMMENT_CONTENT_TOO_LONG, exception.errorCode());
	}

	private static class FakePostRepositoryPort implements PostRepositoryPort {

		private Optional<Post> post = Optional.empty();

		@Override
		public Post save(Post post) {
			return post;
		}

		@Override
		public Optional<Post> findById(Long id) {
			return post;
		}

		@Override
		public PostPageResult findAllOrderByCreatedAtDesc(int page, int size) {
			return new PostPageResult(List.of(), page, size, 0, 0, true, true);
		}

		@Override
		public PostPageResult searchByKeywordOrderByCreatedAtDesc(String keyword, int page, int size) {
			return new PostPageResult(List.of(), page, size, 0, 0, true, true);
		}
	}

	private static class FakeCommentRepositoryPort implements CommentRepositoryPort {

		private Comment savedComment;

		@Override
		public Comment save(Comment comment) {
			savedComment = comment;
			return Comment.rehydrate(
					1L,
					comment.getPostId(),
					comment.getAuthorMemberId(),
					comment.getContent(),
					comment.getCreatedAt()
			);
		}

		@Override
		public Optional<Comment> findById(Long id) {
			return Optional.empty();
		}

		@Override
		public CommentPageResult findAllByPostIdOrderByCreatedAtDesc(Long postId, int page, int size) {
			return new CommentPageResult(List.of(), page, size, 0, 0, true, true);
		}

		@Override
		public Map<Long, Long> countByPostIds(Set<Long> postIds) {
			return Map.of();
		}

		@Override
		public void deleteAllByPostId(Long postId) {
		}

		@Override
		public void deleteById(Long id) {
		}
	}
}
