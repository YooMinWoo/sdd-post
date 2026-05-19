package com.example.post.board.application.port.out;

import com.example.post.board.domain.model.Post;

public interface PostRepositoryPort {

	Post save(Post post);
}
