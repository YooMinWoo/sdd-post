package com.example.post.member.application.port.out;

import com.example.post.member.domain.model.Member;

public interface MemberRepositoryPort {

	boolean existsByEmail(String email);

	Member save(Member member);
}
