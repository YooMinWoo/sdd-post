package com.example.post.board.adapter.out.persistence;

import com.example.post.board.domain.model.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "posts")
class PostJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 5_000)
	private String content;

	@Column(nullable = false)
	private Long authorMemberId;

	@Column(nullable = false)
	private Instant createdAt;

	protected PostJpaEntity() {
	}

	private PostJpaEntity(Long id, String title, String content, Long authorMemberId, Instant createdAt) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.authorMemberId = authorMemberId;
		this.createdAt = createdAt;
	}

	static PostJpaEntity from(Post post) {
		return new PostJpaEntity(
				post.getId(),
				post.getTitle(),
				post.getContent(),
				post.getAuthorMemberId(),
				post.getCreatedAt()
		);
	}

	Post toDomain() {
		return Post.rehydrate(id, title, content, authorMemberId, createdAt);
	}
}
