package com.example.post.application.port.out;

import com.example.post.domain.model.Member;

public interface MemberRepositoryPort {

	boolean existsByEmail(String email);

	Member save(Member member);
}
