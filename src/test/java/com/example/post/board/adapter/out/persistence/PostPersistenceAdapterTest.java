package com.example.post.board.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
		Post post = Post.create("title", "content", "author", Instant.parse("2026-05-20T00:00:00Z"));

		Post savedPost = adapter.save(post);

		assertNotNull(savedPost.getId());
		assertEquals("title", savedPost.getTitle());
		assertEquals("content", savedPost.getContent());
		assertEquals("author", savedPost.getAuthor());
		assertEquals(Instant.parse("2026-05-20T00:00:00Z"), savedPost.getCreatedAt());
	}
}
