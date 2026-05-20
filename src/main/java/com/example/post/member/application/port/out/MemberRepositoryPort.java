package com.example.post.member.application.port.out;

import com.example.post.member.domain.model.Member;
import java.util.Optional;

public interface MemberRepositoryPort {

	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	Optional<Member> findById(Long id);

	Member save(Member member);
}
