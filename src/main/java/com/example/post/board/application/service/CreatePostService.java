package com.example.post.board.application.service;

import com.example.post.board.application.port.in.CreatePostCommand;
import com.example.post.board.application.port.in.CreatePostResult;
import com.example.post.board.application.port.in.CreatePostUseCase;
import com.example.post.board.application.port.out.AuthorMemberPort;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePostService implements CreatePostUseCase {

	private final PostRepositoryPort postRepositoryPort;
	private final AuthorMemberPort authorMemberPort;

	public CreatePostService(PostRepositoryPort postRepositoryPort, AuthorMemberPort authorMemberPort) {
		this.postRepositoryPort = postRepositoryPort;
		this.authorMemberPort = authorMemberPort;
	}

	@Override
	@Transactional
	public CreatePostResult createPost(CreatePostCommand command) {
		Post post = Post.create(command.title(), command.content(), command.authorMemberId());
		Post savedPost = postRepositoryPort.save(post);
		String author = authorMemberPort.getNicknameById(savedPost.getAuthorMemberId());

		return new CreatePostResult(
				savedPost.getId(),
				savedPost.getTitle(),
				savedPost.getContent(),
				savedPost.getAuthorMemberId(),
				author,
				savedPost.getCreatedAt()
		);
	}
}
