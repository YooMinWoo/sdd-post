package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.application.port.in.UpdateCommentCommand;
import com.example.post.board.application.port.in.UpdateCommentResult;
import com.example.post.board.application.port.out.AuthorMemberPort;
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

class UpdateCommentServiceTest {

	@Test
	void updatesCommentByAuthor() {
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
		UpdateCommentService service = new UpdateCommentService(
				postRepositoryPort,
				commentRepositoryPort,
				new FakeAuthorMemberPort()
		);

		UpdateCommentResult result = service.updateComment(new UpdateCommentCommand(1L, 10L, " updated ", 3L));

		assertEquals(10L, result.id());
		assertEquals(3L, result.authorMemberId());
		assertEquals("jane", result.author());
		assertEquals("updated", result.content());
		assertEquals("updated", commentRepositoryPort.savedComment.getContent());
	}

	@Test
	void rejectsInvalidCommentIdWithoutSaving() {
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		UpdateCommentService service = new UpdateCommentService(
				new FakePostRepositoryPort(),
				commentRepositoryPort,
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updateComment(new UpdateCommentCommand(1L, 0L, "content", 3L))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
		assertEquals(null, commentRepositoryPort.savedComment);
	}

	@Test
	void rejectsMissingPostWithoutReadingComment() {
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		UpdateCommentService service = new UpdateCommentService(
				new FakePostRepositoryPort(),
				commentRepositoryPort,
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updateComment(new UpdateCommentCommand(1L, 10L, "content", 3L))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
		assertEquals(0, commentRepositoryPort.findCallCount);
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
		UpdateCommentService service = new UpdateCommentService(
				postRepositoryPort,
				commentRepositoryPort,
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updateComment(new UpdateCommentCommand(1L, 10L, "content", 3L))
		);

		assertEquals(BoardErrorCode.COMMENT_NOT_FOUND, exception.errorCode());
		assertEquals(null, commentRepositoryPort.savedComment);
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
		UpdateCommentService service = new UpdateCommentService(
				postRepositoryPort,
				commentRepositoryPort,
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updateComment(new UpdateCommentCommand(1L, 10L, "content", 3L))
		);

		assertEquals(BoardErrorCode.COMMENT_NOT_FOUND, exception.errorCode());
		assertEquals(null, commentRepositoryPort.savedComment);
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
		UpdateCommentService service = new UpdateCommentService(
				postRepositoryPort,
				commentRepositoryPort,
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updateComment(new UpdateCommentCommand(1L, 10L, "content", 4L))
		);

		assertEquals(BoardErrorCode.COMMENT_UPDATE_FORBIDDEN, exception.errorCode());
		assertEquals(null, commentRepositoryPort.savedComment);
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
		private Comment savedComment;
		private int findCallCount;

		@Override
		public Comment save(Comment comment) {
			savedComment = comment;
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
		}
	}

	private static class FakeAuthorMemberPort implements AuthorMemberPort {

		@Override
		public String getNicknameById(Long memberId) {
			return "jane";
		}

		@Override
		public Map<Long, String> getNicknamesByIds(Set<Long> memberIds) {
			return Map.of(3L, "jane");
		}
	}
}
