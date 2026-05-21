package com.example.post.board.application.service;

import com.example.post.board.application.port.in.ReadPostResult;
import com.example.post.board.application.port.in.ReadPostQuery;
import com.example.post.board.application.port.in.ReadPostUseCase;
import com.example.post.board.application.port.out.AuthorMemberPort;
import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReadPostService implements ReadPostUseCase {

	private final PostRepositoryPort postRepositoryPort;
	private final AuthorMemberPort authorMemberPort;
	private final CommentRepositoryPort commentRepositoryPort;

	public ReadPostService(
			PostRepositoryPort postRepositoryPort,
			AuthorMemberPort authorMemberPort,
			CommentRepositoryPort commentRepositoryPort
	) {
		this.postRepositoryPort = postRepositoryPort;
		this.authorMemberPort = authorMemberPort;
		this.commentRepositoryPort = commentRepositoryPort;
	}

	@Override
	@Transactional(readOnly = true)
	public ReadPostResult readPost(ReadPostQuery query) {
		Long postId = validatePostId(query.postId());
		Post post = postRepositoryPort.findById(postId)
				.orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));
		String author = authorMemberPort.getNicknameById(post.getAuthorMemberId());
		long commentCount = getCommentCount(postId);

		return new ReadPostResult(
				post.getId(),
				post.getTitle(),
				post.getContent(),
				post.getAuthorMemberId(),
				author,
				post.getCreatedAt(),
				commentCount
		);
	}

	private long getCommentCount(Long postId) {
		Map<Long, Long> counts = commentRepositoryPort.countByPostIds(Set.of(postId));
		return counts.getOrDefault(postId, 0L);
	}

	private static Long validatePostId(Long postId) {
		if (postId == null || postId <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return postId;
	}
}
