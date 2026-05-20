package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.post.board.application.port.in.CreatePostCommand;
import com.example.post.board.application.port.in.CreatePostResult;
import com.example.post.board.application.port.out.PostPageResult;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreatePostServiceTest {

	@Test
	void createsAndSavesPost() {
		FakePostRepositoryPort repositoryPort = new FakePostRepositoryPort();
		CreatePostService service = new CreatePostService(repositoryPort);

		CreatePostResult result = service.createPost(new CreatePostCommand("title", "content", 1L));

		assertEquals(1L, result.id());
		assertEquals("title", repositoryPort.savedPost.getTitle());
		assertEquals(1L, repositoryPort.savedPost.getAuthorMemberId());
	}

	private static class FakePostRepositoryPort implements PostRepositoryPort {

		private Post savedPost;

		@Override
		public Post save(Post post) {
			this.savedPost = post;
			return Post.rehydrate(1L, post.getTitle(), post.getContent(), post.getAuthorMemberId(), post.getCreatedAt());
		}

		@Override
		public java.util.Optional<Post> findById(Long id) {
			return java.util.Optional.empty();
		}

		@Override
		public PostPageResult findAllOrderByCreatedAtDesc(int page, int size) {
			return new PostPageResult(List.of(), page, size, 0, 0, true, true);
		}
	}
}
