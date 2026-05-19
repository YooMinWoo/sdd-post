package com.example.post.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Post {

	private static final int MAX_TITLE_LENGTH = 100;
	private static final int MAX_CONTENT_LENGTH = 5_000;
	private static final int MAX_AUTHOR_LENGTH = 50;

	private final Long id;
	private final String title;
	private final String content;
	private final String author;
	private final Instant createdAt;

	private Post(Long id, String title, String content, String author, Instant createdAt) {
		this.id = id;
		this.title = validateRequired("title", title, MAX_TITLE_LENGTH);
		this.content = validateRequired("content", content, MAX_CONTENT_LENGTH);
		this.author = validateRequired("author", author, MAX_AUTHOR_LENGTH);
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
	}

	public static Post create(String title, String content, String author) {
		return create(title, content, author, Instant.now());
	}

	public static Post create(String title, String content, String author, Instant createdAt) {
		return new Post(null, title, content, author, createdAt);
	}

	public static Post rehydrate(Long id, String title, String content, String author, Instant createdAt) {
		if (id == null) {
			throw new IllegalArgumentException("id must not be null");
		}
		return new Post(id, title, content, author, createdAt);
	}

	private static String validateRequired(String fieldName, String value, int maxLength) {
		if (value == null) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		String normalized = value.trim();
		if (normalized.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}
		if (normalized.length() > maxLength) {
			throw new IllegalArgumentException(fieldName + " must be at most " + maxLength + " characters");
		}
		return normalized;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getAuthor() {
		return author;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
