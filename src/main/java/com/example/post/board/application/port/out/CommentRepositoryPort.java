package com.example.post.board.application.port.out;

import com.example.post.board.domain.model.Comment;
import java.util.Map;
import java.util.Set;

public interface CommentRepositoryPort {

	Comment save(Comment comment);

	CommentPageResult findAllByPostIdOrderByCreatedAtDesc(Long postId, int page, int size);

	Map<Long, Long> countByPostIds(Set<Long> postIds);
}
