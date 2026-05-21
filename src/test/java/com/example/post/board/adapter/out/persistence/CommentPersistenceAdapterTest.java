package com.example.post.board.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.post.board.domain.model.Comment;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class CommentPersistenceAdapterTest {

	@Autowired
	private CommentJpaRepository commentJpaRepository;

	@Test
	void savesComment() {
		CommentPersistenceAdapter adapter = new CommentPersistenceAdapter(commentJpaRepository);
		Comment comment = Comment.create(1L, 2L, " content ", Instant.parse("2026-05-21T00:00:00Z"));

		Comment savedComment = adapter.save(comment);

		assertNotNull(savedComment.getId());
		assertEquals(1L, savedComment.getPostId());
		assertEquals(2L, savedComment.getAuthorMemberId());
		assertEquals("content", savedComment.getContent());
		assertEquals(Instant.parse("2026-05-21T00:00:00Z"), savedComment.getCreatedAt());
	}
}
