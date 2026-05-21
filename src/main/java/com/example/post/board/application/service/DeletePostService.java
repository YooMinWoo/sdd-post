package com.example.post.board.application.service;

import com.example.post.board.application.port.in.DeletePostCommand;
import com.example.post.board.application.port.in.DeletePostUseCase;
import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeletePostService implements DeletePostUseCase {

	private final PostRepositoryPort postRepositoryPort;
	private final CommentRepositoryPort commentRepositoryPort;

	public DeletePostService(PostRepositoryPort postRepositoryPort, CommentRepositoryPort commentRepositoryPort) {
		this.postRepositoryPort = postRepositoryPort;
		this.commentRepositoryPort = commentRepositoryPort;
	}

	@Override
	@Transactional
	public void deletePost(DeletePostCommand command) {
		Long postId = validateId(command.postId());
		Long requesterMemberId = validateId(command.requesterMemberId());
		Post post = postRepositoryPort.findById(postId)
				.orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));

		post.deleteBy(requesterMemberId, Instant.now());
		commentRepositoryPort.deleteAllByPostId(postId);
		postRepositoryPort.save(post);
	}

	private static Long validateId(Long id) {
		if (id == null || id <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return id;
	}
}
