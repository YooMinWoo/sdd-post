package com.example.post.member.application.port.out;

import com.example.post.member.domain.model.Member;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepositoryPort {

	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	Optional<Member> findById(Long id);

	List<Member> findAllById(Set<Long> ids);

	Member save(Member member);
}
