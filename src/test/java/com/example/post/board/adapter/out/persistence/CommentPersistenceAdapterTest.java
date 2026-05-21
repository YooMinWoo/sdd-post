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
	void findsCommentById() {
		CommentPersistenceAdapter adapter = new CommentPersistenceAdapter(commentJpaRepository);
		Comment savedComment = adapter.save(Comment.create(
				1L,
				2L,
				"comment",
				Instant.parse("2026-05-21T00:00:00Z")
		));

		Comment foundComment = adapter.findById(savedComment.getId()).orElseThrow();

		assertEquals(savedComment.getId(), foundComment.getId());
		assertEquals(1L, foundComment.getPostId());
		assertEquals(2L, foundComment.getAuthorMemberId());
	}

	@Test
	void savesUpdatedCommentContent() {
		CommentPersistenceAdapter adapter = new CommentPersistenceAdapter(commentJpaRepository);
		Comment savedComment = adapter.save(Comment.create(
				1L,
				2L,
				"comment",
				Instant.parse("2026-05-21T00:00:00Z")
		));

		savedComment.updateBy(1L, 2L, "updated");
		adapter.save(savedComment);

		Comment foundComment = adapter.findById(savedComment.getId()).orElseThrow();
		assertEquals("updated", foundComment.getContent());
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

	@Test
	void deletesCommentsByPostIdOnly() {
		CommentPersistenceAdapter adapter = new CommentPersistenceAdapter(commentJpaRepository);
		adapter.save(Comment.create(1L, 2L, "one", Instant.parse("2026-05-21T01:00:00Z")));
		adapter.save(Comment.create(1L, 3L, "two", Instant.parse("2026-05-21T02:00:00Z")));
		adapter.save(Comment.create(2L, 4L, "three", Instant.parse("2026-05-21T03:00:00Z")));

		adapter.deleteAllByPostId(1L);

		CommentPageResult deletedPostComments = adapter.findAllByPostIdOrderByCreatedAtDesc(1L, 0, 10);
		CommentPageResult otherPostComments = adapter.findAllByPostIdOrderByCreatedAtDesc(2L, 0, 10);
		Map<Long, Long> counts = adapter.countByPostIds(Set.of(1L, 2L));
		assertEquals(0, deletedPostComments.comments().size());
		assertEquals(1, otherPostComments.comments().size());
		assertEquals("three", otherPostComments.comments().get(0).getContent());
		assertEquals(false, counts.containsKey(1L));
		assertEquals(1L, counts.get(2L));
	}

	@Test
	void deletesCommentByIdOnly() {
		CommentPersistenceAdapter adapter = new CommentPersistenceAdapter(commentJpaRepository);
		Comment deletedTarget = adapter.save(Comment.create(1L, 2L, "delete", Instant.parse("2026-05-21T01:00:00Z")));
		Comment remaining = adapter.save(Comment.create(1L, 3L, "remain", Instant.parse("2026-05-21T02:00:00Z")));

		adapter.deleteById(deletedTarget.getId());

		assertEquals(true, adapter.findById(deletedTarget.getId()).isEmpty());
		assertEquals(remaining.getId(), adapter.findById(remaining.getId()).orElseThrow().getId());
	}
}
