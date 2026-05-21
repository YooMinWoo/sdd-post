package com.example.post.board.application.port.out;

import com.example.post.board.domain.model.Comment;

public interface CommentRepositoryPort {

	Comment save(Comment comment);
}
