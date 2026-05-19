package com.example.post.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class PostTest {

	@Test
	void createsPostWithNormalizedValues() {
		Instant createdAt = Instant.parse("2026-05-20T00:00:00Z");

		Post post = Post.create(" title ", " content ", " author ", createdAt);

		assertEquals("title", post.getTitle());
		assertEquals("content", post.getContent());
		assertEquals("author", post.getAuthor());
		assertEquals(createdAt, post.getCreatedAt());
		assertNotNull(post.getCreatedAt());
	}

	@Test
	void rejectsBlankTitle() {
		assertThrows(IllegalArgumentException.class, () -> Post.create(" ", "content", "author"));
	}

	@Test
	void rejectsTooLongTitle() {
		assertThrows(IllegalArgumentException.class, () -> Post.create("a".repeat(101), "content", "author"));
	}

	@Test
	void rejectsBlankContent() {
		assertThrows(IllegalArgumentException.class, () -> Post.create("title", " ", "author"));
	}

	@Test
	void rejectsTooLongContent() {
		assertThrows(IllegalArgumentException.class, () -> Post.create("title", "a".repeat(5_001), "author"));
	}

	@Test
	void rejectsBlankAuthor() {
		assertThrows(IllegalArgumentException.class, () -> Post.create("title", "content", " "));
	}

	@Test
	void rejectsTooLongAuthor() {
		assertThrows(IllegalArgumentException.class, () -> Post.create("title", "content", "a".repeat(51)));
	}
}
