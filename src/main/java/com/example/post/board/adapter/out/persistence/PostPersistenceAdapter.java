package com.example.post.board.adapter.out.persistence;

import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PostPersistenceAdapter implements PostRepositoryPort {

	private final PostJpaRepository postJpaRepository;

	public PostPersistenceAdapter(PostJpaRepository postJpaRepository) {
		this.postJpaRepository = postJpaRepository;
	}

	@Override
	public Post save(Post post) {
		return postJpaRepository.save(PostJpaEntity.from(post)).toDomain();
	}

	@Override
	public Optional<Post> findById(Long id) {
		return postJpaRepository.findById(id)
				.map(PostJpaEntity::toDomain);
	}
}
