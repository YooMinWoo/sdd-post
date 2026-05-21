package com.example.post.board.adapter.out.persistence;

import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.CommentPageResult;
import com.example.post.board.domain.model.Comment;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	@Override
	public Optional<Comment> findById(Long id) {
		return commentJpaRepository.findById(id)
				.map(CommentJpaEntity::toDomain);
	}

	@Override
	public CommentPageResult findAllByPostIdOrderByCreatedAtDesc(Long postId, int page, int size) {
		Page<CommentJpaEntity> result = commentJpaRepository.findAllByPostIdOrderByCreatedAtDesc(
				postId,
				PageRequest.of(page, size)
		);
		return new CommentPageResult(
				result.getContent().stream()
						.map(CommentJpaEntity::toDomain)
						.toList(),
				result.getNumber(),
				result.getSize(),
				result.getTotalElements(),
				result.getTotalPages(),
				result.isFirst(),
				result.isLast()
		);
	}

	@Override
	public Map<Long, Long> countByPostIds(Set<Long> postIds) {
		if (postIds.isEmpty()) {
			return Map.of();
		}
		return commentJpaRepository.countByPostIdInGroupByPostId(postIds).stream()
				.collect(Collectors.toMap(
						CommentCountProjection::getPostId,
						CommentCountProjection::getCommentCount
				));
	}

	@Override
	public void deleteAllByPostId(Long postId) {
		commentJpaRepository.deleteByPostId(postId);
	}

	@Override
	public void deleteById(Long id) {
		commentJpaRepository.deleteById(id);
	}
}
