package com.example.post.board.application.service;

import com.example.post.board.application.port.in.UpdatePostCommand;
import com.example.post.board.application.port.in.UpdatePostResult;
import com.example.post.board.application.port.in.UpdatePostUseCase;
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
public class UpdatePostService implements UpdatePostUseCase {

	private final PostRepositoryPort postRepositoryPort;
	private final AuthorMemberPort authorMemberPort;
	private final CommentRepositoryPort commentRepositoryPort;

	public UpdatePostService(
			PostRepositoryPort postRepositoryPort,
			AuthorMemberPort authorMemberPort,
			CommentRepositoryPort commentRepositoryPort
	) {
		this.postRepositoryPort = postRepositoryPort;
		this.authorMemberPort = authorMemberPort;
		this.commentRepositoryPort = commentRepositoryPort;
	}

	@Override
	@Transactional
	public UpdatePostResult updatePost(UpdatePostCommand command) {
		Long postId = validateId(command.postId());
		Long requesterMemberId = validateId(command.requesterMemberId());
		Post post = postRepositoryPort.findById(postId)
				.orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));

		post.updateBy(requesterMemberId, command.title(), command.content());
		Post savedPost = postRepositoryPort.save(post);
		String author = authorMemberPort.getNicknameById(savedPost.getAuthorMemberId());
		long commentCount = getCommentCount(savedPost.getId());

		return new UpdatePostResult(
				savedPost.getId(),
				savedPost.getTitle(),
				savedPost.getContent(),
				savedPost.getAuthorMemberId(),
				author,
				savedPost.getCreatedAt(),
				commentCount
		);
	}

	private long getCommentCount(Long postId) {
		Map<Long, Long> counts = commentRepositoryPort.countByPostIds(Set.of(postId));
		return counts.getOrDefault(postId, 0L);
	}

	private static Long validateId(Long id) {
		if (id == null || id <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return id;
	}
}
