package com.example.post.board.application.port.out;

import com.example.post.board.domain.model.Post;
import java.util.Optional;

public interface PostRepositoryPort {

	Post save(Post post);

	Optional<Post> findById(Long id);

	PostPageResult findAllOrderByCreatedAtDesc(int page, int size);

	PostPageResult searchByKeywordOrderByCreatedAtDesc(String keyword, int page, int size);
}
