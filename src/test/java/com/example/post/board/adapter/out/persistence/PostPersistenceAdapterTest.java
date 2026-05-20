package com.example.post.board.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
