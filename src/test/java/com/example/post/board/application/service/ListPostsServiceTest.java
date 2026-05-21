package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.application.port.in.ListPostsQuery;
import com.example.post.board.application.port.in.ListPostsResult;
import com.example.post.board.application.port.out.AuthorMemberPort;
import com.example.post.board.application.port.out.CommentPageResult;
import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.PostPageResult;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Comment;
import com.example.post.board.domain.model.Post;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ListPostsServiceTest {

	@Test
	void listsPostsWithCurrentAuthorNicknameAndCommentCount() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		FakeAuthorMemberPort authorMemberPort = new FakeAuthorMemberPort();
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		commentRepositoryPort.commentCounts = Map.of(2L, 3L);
		postRepositoryPort.pageResult = new PostPageResult(
				List.of(
						Post.rehydrate(2L, "second", "content 2", 1L, Instant.parse("2026-05-20T01:00:00Z")),
						Post.rehydrate(1L, "first", "content 1", 1L, Instant.parse("2026-05-20T00:00:00Z"))
				),
				0,
				10,
				2,
				1,
				true,
				true
		);
		ListPostsService service = new ListPostsService(postRepositoryPort, authorMemberPort, commentRepositoryPort);

		ListPostsResult result = service.listPosts(new ListPostsQuery(0, 10));

		assertEquals(2, result.posts().size());
		assertEquals(2L, result.posts().get(0).id());
		assertEquals("second", result.posts().get(0).title());
		assertEquals(1L, result.posts().get(0).authorMemberId());
		assertEquals("minu", result.posts().get(0).author());
		assertEquals(Instant.parse("2026-05-20T01:00:00Z"), result.posts().get(0).createdAt());
		assertEquals(3L, result.posts().get(0).commentCount());
		assertEquals(0L, result.posts().get(1).commentCount());
		assertEquals(0, result.page());
		assertEquals(10, result.size());
		assertEquals(2, result.totalElements());
		assertEquals(1, result.totalPages());
		assertEquals(true, result.first());
		assertEquals(true, result.last());
		assertEquals(1, authorMemberPort.batchLookupCount);
		assertEquals(0, authorMemberPort.singleLookupCount);
		assertEquals(Set.of(1L), authorMemberPort.requestedMemberIds);
		assertEquals(1, commentRepositoryPort.countLookupCount);
		assertEquals(Set.of(1L, 2L), commentRepositoryPort.requestedPostIds);
	}

	@Test
	void searchesPostsWithKeywordAndCommentCount() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		commentRepositoryPort.commentCounts = Map.of(2L, 1L);
		postRepositoryPort.searchResult = new PostPageResult(
				List.of(
						Post.rehydrate(2L, "spring", "content", 1L, Instant.parse("2026-05-20T01:00:00Z"))
				),
				0,
				10,
				1,
				1,
				true,
				true
		);
		ListPostsService service = new ListPostsService(
				postRepositoryPort,
				new FakeAuthorMemberPort(),
				commentRepositoryPort
		);

		ListPostsResult result = service.listPosts(new ListPostsQuery(0, 10, " spring "));

		assertEquals(1, result.posts().size());
		assertEquals("spring", result.posts().get(0).title());
		assertEquals(1L, result.posts().get(0).commentCount());
		assertEquals(0, postRepositoryPort.listLookupCount);
		assertEquals(1, postRepositoryPort.searchLookupCount);
		assertEquals("spring", postRepositoryPort.requestedKeyword);
	}

	@Test
	void treatsBlankKeywordAsNormalList() {
		FakePostRepositoryPort postRepositoryPort = new FakePostRepositoryPort();
		ListPostsService service = new ListPostsService(
				postRepositoryPort,
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		service.listPosts(new ListPostsQuery(0, 10, " "));

		assertEquals(1, postRepositoryPort.listLookupCount);
		assertEquals(0, postRepositoryPort.searchLookupCount);
	}

	@Test
	void returnsEmptyListWithoutAuthorOrCommentCountLookup() {
		FakeAuthorMemberPort authorMemberPort = new FakeAuthorMemberPort();
		FakeCommentRepositoryPort commentRepositoryPort = new FakeCommentRepositoryPort();
		ListPostsService service = new ListPostsService(
				new FakePostRepositoryPort(),
				authorMemberPort,
				commentRepositoryPort
		);

		ListPostsResult result = service.listPosts(new ListPostsQuery(0, 10));

		assertEquals(List.of(), result.posts());
		assertEquals(0, authorMemberPort.batchLookupCount);
		assertEquals(0, commentRepositoryPort.countLookupCount);
	}

	@Test
	void rejectsNegativePage() {
		ListPostsService service = new ListPostsService(
				new FakePostRepositoryPort(),
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.listPosts(new ListPostsQuery(-1, 10))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsTooLargeSize() {
		ListPostsService service = new ListPostsService(
				new FakePostRepositoryPort(),
				new FakeAuthorMemberPort(),
				new FakeCommentRepositoryPort()
		);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.listPosts(new ListPostsQuery(0, 101))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	private static class FakePostRepositoryPort implements PostRepositoryPort {

		private PostPageResult pageResult = new PostPageResult(List.of(), 0, 10, 0, 0, true, true);
		private PostPageResult searchResult = new PostPageResult(List.of(), 0, 10, 0, 0, true, true);
		private String requestedKeyword;
		private int listLookupCount;
		private int searchLookupCount;

		@Override
		public Post save(Post post) {
			return post;
		}

		@Override
		public Optional<Post> findById(Long id) {
			return Optional.empty();
		}

		@Override
		public PostPageResult findAllOrderByCreatedAtDesc(int page, int size) {
			listLookupCount++;
			return pageResult;
		}

		@Override
		public PostPageResult searchByKeywordOrderByCreatedAtDesc(String keyword, int page, int size) {
			searchLookupCount++;
			requestedKeyword = keyword;
			return searchResult;
		}
	}

	private static class FakeCommentRepositoryPort implements CommentRepositoryPort {

		private Map<Long, Long> commentCounts = Map.of();
		private Set<Long> requestedPostIds = Set.of();
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
			requestedPostIds = Set.copyOf(postIds);
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

		private int singleLookupCount;
		private int batchLookupCount;
		private Set<Long> requestedMemberIds = Set.of();

		@Override
		public String getNicknameById(Long memberId) {
			singleLookupCount++;
			return "minu";
		}

		@Override
		public Map<Long, String> getNicknamesByIds(Set<Long> memberIds) {
			batchLookupCount++;
			requestedMemberIds = Set.copyOf(memberIds);
			return Map.of(1L, "minu");
		}
	}
}
