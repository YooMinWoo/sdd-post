package com.example.post.board.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.post.board.application.port.in.CreatePostCommand;
import com.example.post.board.application.port.in.CreatePostResult;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import org.junit.jupiter.api.Test;

class CreatePostServiceTest {

	@Test
	void createsAndSavesPost() {
		FakePostRepositoryPort repositoryPort = new FakePostRepositoryPort();
		CreatePostService service = new CreatePostService(repositoryPort);

		CreatePostResult result = service.createPost(new CreatePostCommand("title", "content", "author"));

		assertEquals(1L, result.id());
		assertEquals("title", result.title());
		assertEquals("content", result.content());
		assertEquals("author", result.author());
		assertNotNull(result.createdAt());
		assertEquals("title", repositoryPort.savedPost.getTitle());
	}

	private static class FakePostRepositoryPort implements PostRepositoryPort {

		private Post savedPost;

		@Override
		public Post save(Post post) {
			this.savedPost = post;
			return Post.rehydrate(1L, post.getTitle(), post.getContent(), post.getAuthor(), post.getCreatedAt());
		}
	}
}
