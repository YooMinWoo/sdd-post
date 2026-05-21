package com.example.post.board.application.service;

import com.example.post.board.application.port.in.UpdateCommentCommand;
import com.example.post.board.application.port.in.UpdateCommentResult;
import com.example.post.board.application.port.in.UpdateCommentUseCase;
import com.example.post.board.application.port.out.AuthorMemberPort;
import com.example.post.board.application.port.out.CommentRepositoryPort;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Comment;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.GlobalErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCommentService implements UpdateCommentUseCase {

	private final PostRepositoryPort postRepositoryPort;
	private final CommentRepositoryPort commentRepositoryPort;
	private final AuthorMemberPort authorMemberPort;

	public UpdateCommentService(
			PostRepositoryPort postRepositoryPort,
			CommentRepositoryPort commentRepositoryPort,
			AuthorMemberPort authorMemberPort
	) {
		this.postRepositoryPort = postRepositoryPort;
		this.commentRepositoryPort = commentRepositoryPort;
		this.authorMemberPort = authorMemberPort;
	}

	@Override
	@Transactional
	public UpdateCommentResult updateComment(UpdateCommentCommand command) {
		Long postId = validateId(command.postId());
		Long commentId = validateId(command.commentId());
		Long requesterMemberId = validateId(command.requesterMemberId());

		postRepositoryPort.findById(postId)
				.orElseThrow(() -> new BusinessException(BoardErrorCode.POST_NOT_FOUND));
		Comment comment = commentRepositoryPort.findById(commentId)
				.orElseThrow(() -> new BusinessException(BoardErrorCode.COMMENT_NOT_FOUND));

		comment.updateBy(postId, requesterMemberId, command.content());
		Comment savedComment = commentRepositoryPort.save(comment);
		String author = authorMemberPort.getNicknameById(savedComment.getAuthorMemberId());

		return new UpdateCommentResult(
				savedComment.getId(),
				savedComment.getAuthorMemberId(),
				author,
				savedComment.getContent(),
				savedComment.getCreatedAt()
		);
	}

	private static Long validateId(Long id) {
		if (id == null || id <= 0) {
			throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
		}
		return id;
	}
}
