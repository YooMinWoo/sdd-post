package com.example.post.application.service;

import com.example.post.application.port.in.CreatePostCommand;
import com.example.post.application.port.in.CreatePostResult;
import com.example.post.application.port.in.CreatePostUseCase;
import com.example.post.application.port.out.PostRepositoryPort;
import com.example.post.domain.model.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePostService implements CreatePostUseCase {

	private final PostRepositoryPort postRepositoryPort;

	public CreatePostService(PostRepositoryPort postRepositoryPort) {
		this.postRepositoryPort = postRepositoryPort;
	}

	@Override
	@Transactional
	public CreatePostResult createPost(CreatePostCommand command) {
		Post post = Post.create(command.title(), command.content(), command.author());
		Post savedPost = postRepositoryPort.save(post);

		return new CreatePostResult(
				savedPost.getId(),
				savedPost.getTitle(),
				savedPost.getContent(),
				savedPost.getAuthor(),
				savedPost.getCreatedAt()
		);
	}
}
