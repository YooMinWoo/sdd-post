package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.post.board.application.port.in.DeletePostCommand;
import com.example.post.board.application.port.out.PostPageResult;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DeletePostServiceTest {

	@Test
	void deletesPostByAuthor() {
		FakePostRepositoryPort repositoryPort = new FakePostRepositoryPort();
		repositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		DeletePostService service = new DeletePostService(repositoryPort);

		service.deletePost(new DeletePostCommand(1L, 2L));

		assertNotNull(repositoryPort.savedPost);
		assertEquals(true, repositoryPort.savedPost.isDeleted());
		assertNotNull(repositoryPort.savedPost.getDeletedAt());
	}

	@Test
	void rejectsInvalidPostId() {
		DeletePostService service = new DeletePostService(new FakePostRepositoryPort());

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deletePost(new DeletePostCommand(0L, 1L))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsInvalidRequesterMemberId() {
		DeletePostService service = new DeletePostService(new FakePostRepositoryPort());

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deletePost(new DeletePostCommand(1L, 0L))
		);

		assertEquals(GlobalErrorCode.INVALID_REQUEST, exception.errorCode());
	}

	@Test
	void rejectsMissingPost() {
		DeletePostService service = new DeletePostService(new FakePostRepositoryPort());

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deletePost(new DeletePostCommand(999L, 1L))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
	}

	@Test
	void rejectsAlreadyDeletedPost() {
		FakePostRepositoryPort repositoryPort = new FakePostRepositoryPort();
		repositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z"),
				Instant.parse("2026-05-20T01:00:00Z")
		));
		DeletePostService service = new DeletePostService(repositoryPort);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deletePost(new DeletePostCommand(1L, 2L))
		);

		assertEquals(BoardErrorCode.POST_NOT_FOUND, exception.errorCode());
	}

	@Test
	void rejectsNonAuthor() {
		FakePostRepositoryPort repositoryPort = new FakePostRepositoryPort();
		repositoryPort.post = Optional.of(Post.rehydrate(
				1L,
				"title",
				"content",
				2L,
				Instant.parse("2026-05-20T00:00:00Z")
		));
		DeletePostService service = new DeletePostService(repositoryPort);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> service.deletePost(new DeletePostCommand(1L, 3L))
		);

		assertEquals(BoardErrorCode.POST_DELETE_FORBIDDEN, exception.errorCode());
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
	}
}
