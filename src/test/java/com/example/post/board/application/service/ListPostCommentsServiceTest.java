package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.application.port.in.ListPostCommentsQuery;
import com.example.post.board.application.port.in.ListPostCommentsResult;
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

class ListPostCommentsServiceTest {

	@Test
	void listsCommentsByLatestOrderWithAuthors() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		commentRepositoryPort.pageResult = new CommentPageResult(
				List.of(
						Comment.rehydrate(2L, 1L, 4L, "second", Instant.parse("2026-05-21T02:00:00Z")),
						Comment.rehydrate(1L, 1L, 3L, "first", Instant.parse("2026-05-21T01:00:00Z"))
				),
				0,
				10,
				2,
				1,
				true,
				true
		);
		FakeAuthorMemberPort authorMemberPort = new FakeAuthorMemberPort();
		ListPostCommentsService service = new ListPostCommentsService(
				postRepositoryPort,
				commentRepositoryPort,
				authorMemberPort
		);

		ListPostCommentsResult result = service.listPostComments(new ListPostCommentsQuery(1L, 0, 10));

		assertEquals(2, result.items().size());
		assertEquals(2L, result.items().get(0).id());
		assertEquals(4L, result.items().get(0).authorMemberId());
		assertEquals("jane", result.items().get(0).author());
		assertEquals("second", result.items().get(0).content());
		assertEquals(0, result.page());
		assertEquals(10, result.size());
		assertEquals(2, result.totalElements());
		assertEquals(1, result.totalPages());
		assertEquals(true, result.first());
		assertEquals(true, result.last());
		assertEquals(1, authorMemberPort.batchLookupCount);
		assertEquals(Set.of(3L, 4L), authorMemberPort.requestedMemberIds);
		assertEquals(1, commentRepositoryPort.pageLookupCount);
	}

	@Test
	void listsEmptyCommentsWithoutAuthorLookup() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		FakeAuthorMemberPort authorMemberPort = new FakeAuthorMemberPort();
		ListPostCommentsService service = new ListPostCommentsService(
				postRepositoryPort,
				new FakeCommentRepositoryPort(),
				authorMemberPort
		);

		ListPostCommentsResult result = service.listPostComments(new ListPostCommentsQuery(1L, 0, 10));

		assertEquals(List.of(), result.items());
		assertEquals(0, result.totalElements());
		assertEquals(0, authorMemberPort.batchLookupCount);
	}

	@Test
	void rejectsInvalidPostId() {
		ListPostCommentsService service = new ListPostCommentsService(
				new FakePostRepositoryPort(),
				new FakeCommentRepositoryPort(),
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.listPostComments(new ListPostCommentsQuery(0L, 0, 10))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsInvalidPage() {
		ListPostCommentsService service = new ListPostCommentsService(
				new FakePostRepositoryPort(),
				new FakeCommentRepositoryPort(),
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.listPostComments(new ListPostCommentsQuery(1L, -1, 10))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsInvalidSize() {
		ListPostCommentsService service = new ListPostCommentsService(
				new FakePostRepositoryPort(),
				new FakeCommentRepositoryPort(),
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.listPostComments(new ListPostCommentsQuery(1L, 0, 101))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsMissingPostWithoutReadingComments() {
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		ListPostCommentsService service = new ListPostCommentsService(
				new FakePostRepositoryPort(),
				commentRepositoryPort,
				new FakeAuthorMemberPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.listPostComments(new ListPostCommentsQuery(999L, 0, 10))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
		assertEquals(0, commentRepositoryPort.pageLookupCount);
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
	}

	private static class FakeCommentRepositoryPort implements CommentRepositoryPort {

		private CommentPageResult pageResult = new CommentPageResult(List.of(), 0, 10, 0, 0, true, true);
		private int pageLookupCount;

		@Override
		public Comment save(Comment comment) {
			return comment;
		}

		@Override
		public Optional<Comment> findById(Long id) {
			return Optional.empty();
		}

		@Override
		public CommentPageResult findAllByPostIdOrderByCreatedAtDesc(Long postId, int page, int size) {
			pageLookupCount++;
			return pageResult;
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

		private int batchLookupCount;
		private Set<Long> requestedMemberIds = Set.of();

		@Override
		public String getNicknameById(Long memberId) {
			return "minu";
		}

		@Override
		public Map<Long, String> getNicknamesByIds(Set<Long> memberIds) {
			batchLookupCount++;
			requestedMemberIds = Set.copyOf(memberIds);
			return Map.of(3L, "kim", 4L, "jane");
		}
	}
}
