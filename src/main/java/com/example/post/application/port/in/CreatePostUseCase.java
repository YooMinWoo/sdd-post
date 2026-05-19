package com.example.post.application.port.in;

public interface CreatePostUseCase {

	CreatePostResult createPost(CreatePostCommand command);
}
