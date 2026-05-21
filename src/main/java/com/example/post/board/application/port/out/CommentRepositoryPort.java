package com.example.post.board.application.port.out;

import com.example.post.board.domain.model.Comment;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CommentRepositoryPort {

	Comment save(Comment comment);

	Optional<Comment> findById(Long id);

	CommentPageResult findAllByPostIdOrderByCreatedAtDesc(Long postId, int page, int size);

	Map<Long, Long> countByPostIds(Set<Long> postIds);

	void deleteAllByPostId(Long postId);

	void deleteById(Long id);
}
