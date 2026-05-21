package com.example.post.board.adapter.out.persistence;

import com.example.post.board.application.port.out.PostPageResult;
import com.example.post.board.application.port.out.PostRepositoryPort;
import com.example.post.board.domain.model.Post;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
		return postJpaRepository.findByIdAndDeletedAtIsNull(id)
				.map(PostJpaEntity::toDomain);
	}

	@Override
	public PostPageResult findAllOrderByCreatedAtDesc(int page, int size) {
		Page<PostJpaEntity> result = postJpaRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(
				PageRequest.of(page, size)
		);
		return toPageResult(result);
	}

	@Override
	public PostPageResult searchByKeywordOrderByCreatedAtDesc(String keyword, int page, int size) {
		Page<PostJpaEntity> result = postJpaRepository
				.findAllByDeletedAtIsNullAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
						keyword,
						keyword,
						PageRequest.of(page, size)
				);
		return toPageResult(result);
	}

	private static PostPageResult toPageResult(Page<PostJpaEntity> result) {
		return new PostPageResult(
				result.getContent().stream()
						.map(PostJpaEntity::toDomain)
						.toList(),
				result.getNumber(),
				result.getSize(),
				result.getTotalElements(),
				result.getTotalPages(),
				result.isFirst(),
				result.isLast()
		);
	}
}
