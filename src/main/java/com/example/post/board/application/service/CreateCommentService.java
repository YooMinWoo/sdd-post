package com.example.post.board.application.service;

import com.example.post.board.application.port.in.CreateCommentCommand;
import com.example.post.board.application.port.in.CreateCommentResult;
import com.example.post.board.application.port.in.CreateCommentUseCase;
import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Comment;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCommentService implements CreateCommentUseCase {

	private final PostRepositoryPort postRepositoryPort;
	private final CommentRepositoryPort commentRepositoryPort;

	public CreateCommentService(
			PostRepositoryPort postRepositoryPort,
			CommentRepositoryPort commentRepositoryPort
	) {
		this.postRepositoryPort = postRepositoryPort;
		this.commentRepositoryPort = commentRepositoryPort;
	}

	@Override
	@Transactional
	public CreateCommentResult createComment(CreateCommentCommand command) {
		Long postId = validateId(command.postId());
		Long authorMemberId = validateId(command.authorMemberId());
		postRepositoryPort.findById(postId)
				.filter(post -> !post.isDeleted())
				.orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));

		Comment comment = Comment.create(postId, authorMemberId, command.content());
		Comment savedComment = commentRepositoryPort.save(comment);

		return new CreateCommentResult(savedComment.getId());
	}

	private static Long validateId(Long id) {
		if (id == null || id <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return id;
	}
}
