package com.example.post.application.port.out;

import com.example.post.domain.model.Post;

public interface PostRepositoryPort {

	Post save(Post post);
}
