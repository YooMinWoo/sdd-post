package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.application.port.in.DeleteCommentCommand;
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

class DeleteCommentServiceTest {

	@Test
	void deletesCommentByAuthor() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		commentRepositoryPort.comment = Optional.of(Comment.rehydrate(
				10L,
				1L,
				3L,
				"comment",
				Instant.parse("2026-05-21T00:00:00Z")
		));
		DeleteCommentService service = new DeleteCommentService(postRepositoryPort, commentRepositoryPort);

		service.deleteComment(new DeleteCommentCommand(1L, 10L, 3L));

		assertEquals(1, commentRepositoryPort.deleteCallCount);
		assertEquals(10L, commentRepositoryPort.deletedCommentId);
	}

	@Test
	void rejectsInvalidPostIdWithoutDeletingComment() {
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		DeleteCommentService service = new DeleteCommentService(new FakePostRepositoryPort(), commentRepositoryPort);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deleteComment(new DeleteCommentCommand(0L, 10L, 3L))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
		assertEquals(0, commentRepositoryPort.deleteCallCount);
	}

	@Test
	void rejectsMissingPostWithoutReadingComment() {
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		DeleteCommentService service = new DeleteCommentService(new FakePostRepositoryPort(), commentRepositoryPort);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deleteComment(new DeleteCommentCommand(1L, 10L, 3L))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
		assertEquals(0, commentRepositoryPort.findCallCount);
		assertEquals(0, commentRepositoryPort.deleteCallCount);
	}

	@Test
	void rejectsMissingComment() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		DeleteCommentService service = new DeleteCommentService(postRepositoryPort, commentRepositoryPort);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deleteComment(new DeleteCommentCommand(1L, 10L, 3L))
		);

		assertEquals(BoardErrorCode.COMMENT_NOT_FOUND, exception.errorCode());
		assertEquals(1, commentRepositoryPort.findCallCount);
		assertEquals(0, commentRepositoryPort.deleteCallCount);
	}

	@Test
	void rejectsCommentFromOtherPost() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		commentRepositoryPort.comment = Optional.of(Comment.rehydrate(
				10L,
				2L,
				3L,
				"comment",
				Instant.parse("2026-05-21T00:00:00Z")
		));
		DeleteCommentService service = new DeleteCommentService(postRepositoryPort, commentRepositoryPort);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deleteComment(new DeleteCommentCommand(1L, 10L, 3L))
		);

		assertEquals(BoardErrorCode.COMMENT_NOT_FOUND, exception.errorCode());
		assertEquals(0, commentRepositoryPort.deleteCallCount);
	}

	@Test
	void rejectsNonAuthor() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		commentRepositoryPort.comment = Optional.of(Comment.rehydrate(
				10L,
				1L,
				3L,
				"comment",
				Instant.parse("2026-05-21T00:00:00Z")
		));
		DeleteCommentService service = new DeleteCommentService(postRepositoryPort, commentRepositoryPort);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deleteComment(new DeleteCommentCommand(1L, 10L, 4L))
		);

		assertEquals(BoardErrorCode.COMMENT_DELETE_FORBIDDEN, exception.errorCode());
		assertEquals(0, commentRepositoryPort.deleteCallCount);
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

		private Optional<Comment> comment = Optional.empty();
		private Long deletedCommentId;
		private int findCallCount;
		private int deleteCallCount;

		@Override
		public Comment save(Comment comment) {
			return comment;
		}

		@Override
		public Optional<Comment> findById(Long id) {
			findCallCount++;
			return comment;
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
			deletedCommentId = id;
			deleteCallCount++;
		}
	}
}
