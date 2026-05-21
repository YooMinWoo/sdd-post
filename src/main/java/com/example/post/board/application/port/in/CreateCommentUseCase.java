package com.example.post.board.application.port.in;

public interface CreateCommentUseCase {

	CreateCommentResult createComment(CreateCommentCommand command);
}
