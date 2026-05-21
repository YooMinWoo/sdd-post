package com.example.post.board.adapter.out.persistence;

import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.domain.model.Comment;
import org.springframework.stereotype.Repository;

@Repository
public class CommentPersistenceAdapter implements CommentRepositoryPort {

	private final CommentJpaRepository commentJpaRepository;

	public CommentPersistenceAdapter(CommentJpaRepository commentJpaRepository) {
		this.commentJpaRepository = commentJpaRepository;
	}

	@Override
	public Comment save(Comment comment) {
		return commentJpaRepository.save(CommentJpaEntity.from(comment)).toDomain();
	}
}
