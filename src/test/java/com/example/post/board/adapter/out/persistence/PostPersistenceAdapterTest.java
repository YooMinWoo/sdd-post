package com.example.post.board.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.post.board.application.port.out.PostPageResult;
import com.example.post.board.domain.model.Post;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class PostPersistenceAdapterTest {

	@Autowired
	private PostJpaRepository postJpaRepository;

	@Test
	void savesPost() {
		PostPersistenceAdapter adapter = new PostPersistenceAdapter(postJpaRepository);
		Post post = Post.create("title", "content", 1L, Instant.parse("2026-05-20T00:00:00Z"));

		Post savedPost = adapter.save(post);

		assertNotNull(savedPost.getId());
		assertEquals("title", savedPost.getTitle());
		assertEquals("content", savedPost.getContent());
		assertEquals(1L, savedPost.getAuthorMemberId());
		assertEquals(Instant.parse("2026-05-20T00:00:00Z"), savedPost.getCreatedAt());
		assertNull(savedPost.getDeletedAt());
	}

	@Test
	void findsPostById() {
		PostPersistenceAdapter adapter = new PostPersistenceAdapter(postJpaRepository);
		Post savedPost = adapter.save(Post.create("title", "content", 1L, Instant.parse("2026-05-20T00:00:00Z")));

		Post foundPost = adapter.findById(savedPost.getId()).orElseThrow();

		assertEquals(savedPost.getId(), foundPost.getId());
		assertEquals("title", foundPost.getTitle());
		assertEquals(1L, foundPost.getAuthorMemberId());
	}

	@Test
	void returnsEmptyWhenPostNotFound() {
		PostPersistenceAdapter adapter = new PostPersistenceAdapter(postJpaRepository);

		assertTrue(adapter.findById(999L).isEmpty());
	}

	@Test
	void savesDeletedAtAndExcludesDeletedPostFromFindById() {
		PostPersistenceAdapter adapter = new PostPersistenceAdapter(postJpaRepository);
		Post savedPost = adapter.save(Post.create("title", "content", 1L, Instant.parse("2026-05-20T00:00:00Z")));

		savedPost.deleteBy(1L, Instant.parse("2026-05-20T01:00:00Z"));
		Post deletedPost = adapter.save(savedPost);

		assertEquals(Instant.parse("2026-05-20T01:00:00Z"), deletedPost.getDeletedAt());
		assertTrue(adapter.findById(savedPost.getId()).isEmpty());
	}

	@Test
	void findsPostsByPageOrderByCreatedAtDesc() {
		PostPersistenceAdapter adapter = new PostPersistenceAdapter(postJpaRepository);
		adapter.save(Post.create("old", "content", 1L, Instant.parse("2026-05-20T00:00:00Z")));
		adapter.save(Post.create("new", "content", 1L, Instant.parse("2026-05-20T01:00:00Z")));

		PostPageResult result = adapter.findAllOrderByCreatedAtDesc(0, 10);

		assertEquals(2, result.posts().size());
		assertEquals("new", result.posts().get(0).getTitle());
		assertEquals("old", result.posts().get(1).getTitle());
		assertEquals(0, result.page());
		assertEquals(10, result.size());
		assertEquals(2, result.totalElements());
		assertEquals(1, result.totalPages());
		assertTrue(result.first());
		assertTrue(result.last());
	}

	@Test
	void excludesDeletedPostsFromPage() {
		PostPersistenceAdapter adapter = new PostPersistenceAdapter(postJpaRepository);
		Post deletedPost = adapter.save(Post.create("deleted", "content", 1L, Instant.parse("2026-05-20T01:00:00Z")));
		deletedPost.deleteBy(1L, Instant.parse("2026-05-20T02:00:00Z"));
		adapter.save(deletedPost);
		adapter.save(Post.create("visible", "content", 1L, Instant.parse("2026-05-20T00:00:00Z")));

		PostPageResult result = adapter.findAllOrderByCreatedAtDesc(0, 10);

		assertEquals(1, result.posts().size());
		assertEquals("visible", result.posts().get(0).getTitle());
		assertEquals(1, result.totalElements());
	}
}
