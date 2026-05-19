package com.example.post.board.application.port.in;

public interface CreatePostUseCase {

	CreatePostResult createPost(CreatePostCommand command);
}
