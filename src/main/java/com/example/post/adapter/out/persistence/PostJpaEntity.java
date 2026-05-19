package com.example.post.adapter.out.persistence;

import com.example.post.domain.model.Post;
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

	@Column(nullable = false, length = 50)
	private String author;

	@Column(nullable = false)
	private Instant createdAt;

	protected PostJpaEntity() {
	}

	private PostJpaEntity(Long id, String title, String content, String author, Instant createdAt) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
	}

	static PostJpaEntity from(Post post) {
		return new PostJpaEntity(
				post.getId(),
				post.getTitle(),
				post.getContent(),
				post.getAuthor(),
				post.getCreatedAt()
		);
	}

	Post toDomain() {
		return Post.rehydrate(id, title, content, author, createdAt);
	}
}
