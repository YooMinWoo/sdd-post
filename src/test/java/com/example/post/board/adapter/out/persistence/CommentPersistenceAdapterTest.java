package com.example.post.board.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.post.board.application.port.out.CommentPageResult;
import com.example.post.board.domain.model.Comment;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
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

	@Test
	void findsCommentsByPostIdOrderByCreatedAtDesc() {
		CommentPersistenceAdapter adapter = new CommentPersistenceAdapter(commentJpaRepository);
		adapter.save(Comment.create(1L, 2L, "old", Instant.parse("2026-05-21T01:00:00Z")));
		adapter.save(Comment.create(1L, 3L, "new", Instant.parse("2026-05-21T02:00:00Z")));
		adapter.save(Comment.create(2L, 4L, "other post", Instant.parse("2026-05-21T03:00:00Z")));

		CommentPageResult result = adapter.findAllByPostIdOrderByCreatedAtDesc(1L, 0, 10);

		assertEquals(2, result.comments().size());
		assertEquals("new", result.comments().get(0).getContent());
		assertEquals("old", result.comments().get(1).getContent());
		assertEquals(0, result.page());
		assertEquals(10, result.size());
		assertEquals(2, result.totalElements());
		assertEquals(1, result.totalPages());
		assertEquals(true, result.first());
		assertEquals(true, result.last());
	}

	@Test
	void countsCommentsByPostIds() {
		CommentPersistenceAdapter adapter = new CommentPersistenceAdapter(commentJpaRepository);
		adapter.save(Comment.create(1L, 2L, "one", Instant.parse("2026-05-21T01:00:00Z")));
		adapter.save(Comment.create(1L, 3L, "two", Instant.parse("2026-05-21T02:00:00Z")));
		adapter.save(Comment.create(2L, 4L, "three", Instant.parse("2026-05-21T03:00:00Z")));

		Map<Long, Long> counts = adapter.countByPostIds(Set.of(1L, 2L, 3L));

		assertEquals(2L, counts.get(1L));
		assertEquals(1L, counts.get(2L));
		assertEquals(false, counts.containsKey(3L));
	}
}
