package com.example.post.board.application.service;

import com.example.post.board.application.port.in.DeleteCommentCommand;
import com.example.post.board.application.port.in.DeleteCommentUseCase;
import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Comment;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCommentService implements DeleteCommentUseCase {

	private final PostRepositoryPort postRepositoryPort;
	private final CommentRepositoryPort commentRepositoryPort;

	public DeleteCommentService(PostRepositoryPort postRepositoryPort, CommentRepositoryPort commentRepositoryPort) {
		this.postRepositoryPort = postRepositoryPort;
		this.commentRepositoryPort = commentRepositoryPort;
	}

	@Override
	@Transactional
	public void deleteComment(DeleteCommentCommand command) {
		Long postId = validateId(command.postId());
		Long commentId = validateId(command.commentId());
		Long requesterMemberId = validateId(command.requesterMemberId());

		postRepositoryPort.findById(postId)
				.orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));
		Comment comment = commentRepositoryPort.findById(commentId)
				.orElseThrow(() -> new BusinessException(BoardErrorCode.COMMENT_NOT_FOUND));

		comment.deleteBy(postId, requesterMemberId);
		commentRepositoryPort.deleteById(commentId);
	}

	private static Long validateId(Long id) {
		if (id == null || id <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return id;
	}
}
