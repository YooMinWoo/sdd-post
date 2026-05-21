package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.application.port.in.UpdatePostCommand;
import com.example.post.board.application.port.in.UpdatePostResult;
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

class UpdatePostServiceTest {

	@Test
	void updatesPostByAuthorWithAuthorAndCommentCount() {
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
		UpdatePostService service = new UpdatePostService(
				postRepositoryPort,
				new FakeAuthorMemberPort(),
				commentRepositoryPort
		);

		UpdatePostResult result = service.updatePost(new UpdatePostCommand(1L, " updated ", " changed ", 2L));

		assertEquals(1L, result.id());
		assertEquals("updated", result.title());
		assertEquals("changed", result.content());
		assertEquals(2L, result.authorMemberId());
		assertEquals("minu", result.author());
		assertEquals(3L, result.commentCount());
		assertEquals("updated", postRepositoryPort.savedPost.getTitle());
		assertEquals("changed", postRepositoryPort.savedPost.getContent());
		assertEquals(1, commentRepositoryPort.countLookupCount);
	}

	@Test
	void rejectsInvalidPostId() {
		UpdatePostService service = new UpdatePostService(
				new FakePostRepositoryPort(),
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updatePost(new UpdatePostCommand(0L, "title", "content", 1L))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsMissingPostWithoutSaving() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		UpdatePostService service = new UpdatePostService(
				postRepositoryPort,
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updatePost(new UpdatePostCommand(999L, "title", "content", 1L))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
		assertEquals(null, postRepositoryPort.savedPost);
	}

	@Test
	void rejectsNonAuthorWithoutSaving() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		UpdatePostService service = new UpdatePostService(
				postRepositoryPort,
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updatePost(new UpdatePostCommand(1L, "title", "content", 3L))
		);

		assertEquals(BoardErrorCode.POST_UPDATE_FORBIDDEN, exception.errorCode());
		assertEquals(null, postRepositoryPort.savedPost);
	}

	@Test
	void rejectsInvalidTitle() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		postRepositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		UpdatePostService service = new UpdatePostService(
				postRepositoryPort,
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.updatePost(new UpdatePostCommand(1L, " ", "content", 2L))
		);

		assertEquals(BoardErrorCode.POST_TITLE_REQUIRED, exception.errorCode());
		assertEquals(null, postRepositoryPort.savedPost);
	}

	private static class FakePostRepositoryPort implements PostRepositoryPort {

		private Optional<Post> post = Optional.empty();
		private Post savedPost;

		@Override
		public Post save(Post post) {
			savedPost = post;
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
			return Map.of(2L, "minu");
		}
	}
}
