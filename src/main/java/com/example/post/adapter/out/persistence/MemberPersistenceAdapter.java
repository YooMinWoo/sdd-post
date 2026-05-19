package com.example.post.adapter.out.persistence;

import com.example.post.application.port.out.MemberRepositoryPort;
import com.example.post.domain.model.Member;
import org.springframework.stereotype.Repository;

@Repository
public class MemberPersistenceAdapter implements MemberRepositoryPort {

	private final MemberJpaRepository memberJpaRepository;

	public MemberPersistenceAdapter(MemberJpaRepository memberJpaRepository) {
		this.memberJpaRepository = memberJpaRepository;
	}

	@Override
	public boolean existsByEmail(String email) {
		return memberJpaRepository.existsByEmail(email);
	}

	@Override
	public Member save(Member member) {
		return memberJpaRepository.save(MemberJpaEntity.from(member)).toDomain();
	}
}
