package com.example.post.board.adapter.out.persistence;

import com.example.post.board.domain.model.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "comments")
class CommentJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long postId;

	@Column(nullable = false)
	private Long authorMemberId;

	@Column(nullable = false, length = 1_000)
	private String content;

	@Column(nullable = false)
	private Instant createdAt;

	protected CommentJpaEntity() {
	}

	private CommentJpaEntity(Long id, Long postId, Long authorMemberId, String content, Instant createdAt) {
		this.id = id;
		this.postId = postId;
		this.authorMemberId = authorMemberId;
		this.content = content;
		this.createdAt = createdAt;
	}

	static CommentJpaEntity from(Comment comment) {
		return new CommentJpaEntity(
				comment.getId(),
				comment.getPostId(),
				comment.getAuthorMemberId(),
				comment.getContent(),
				comment.getCreatedAt()
		);
	}

	Comment toDomain() {
		return Comment.rehydrate(id, postId, authorMemberId, content, createdAt);
	}
}
