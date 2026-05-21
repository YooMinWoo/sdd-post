package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.application.port.in.ReadPostQuery;
import com.example.post.board.application.port.in.ReadPostResult;
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

class ReadPostServiceTest {

	@Test
	void readsPostWithCurrentAuthorNicknameAndCommentCount() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		commentRepositoryPort.commentCounts = Map.of(1L, 3L);
		ReadPostService service = new ReadPostService(
				postRepositoryPort,
				new FakeAuthorMemberPort(),
				commentRepositoryPort
		);

		ReadPostResult result = service.readPost(new ReadPostQuery(1L));

		assertEquals(1L, result.id());
		assertEquals("title", result.title());
		assertEquals("content", result.content());
		assertEquals(2L, result.authorMemberId());
		assertEquals("minu", result.author());
		assertEquals(Instant.parse("2026-05-20T00:00:00Z"), result.createdAt());
		assertEquals(3L, result.commentCount());
		assertEquals(1, commentRepositoryPort.countLookupCount);
		assertEquals(0, commentRepositoryPort.pageLookupCount);
	}

	@Test
	void readsPostWithZeroCommentCountWhenMissingCountRow() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		ReadPostService service = new ReadPostService(
				postRepositoryPort,
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		ReadPostResult result = service.readPost(new ReadPostQuery(1L));

		assertEquals(0L, result.commentCount());
	}

	@Test
	void rejectsInvalidPostId() {
		ReadPostService service = new ReadPostService(
				new FakePostRepositoryPort(),
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.readPost(new ReadPostQuery(0L))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsMissingPostWithoutReadingCommentCount() {
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		ReadPostService service = new ReadPostService(
				new FakePostRepositoryPort(),
				new FakeAuthorMemberPort(),
				commentRepositoryPort
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.readPost(new ReadPostQuery(999L))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
		assertEquals(0, commentRepositoryPort.countLookupCount);
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

		@Override
		public PostPageResult searchByKeywordOrderByCreatedAtDesc(String keyword, int page, int size) {
			return new PostPageResult(List.of(), page, size, 0, 0, true, true);
		}
	}

	private static class FakeCommentRepositoryPort implements CommentRepositoryPort {

		private Map<Long, Long> commentCounts = Map.of();
		private int countLookupCount;
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
			return new CommentPageResult(List.of(), page, size, 0, 0, true, true);
		}

		@Override
		public Map<Long, Long> countByPostIds(Set<Long> postIds) {
			countLookupCount++;
			return commentCounts;
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
			return "minu";
		}

		@Override
		public Map<Long, String> getNicknamesByIds(Set<Long> memberIds) {
			return Map.of(1L, "minu");
		}
	}
}
