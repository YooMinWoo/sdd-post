package com.example.post.member.adapter.out.persistence;

import com.example.post.member.application.port.out.MemberRepositoryPort;
import com.example.post.member.domain.model.Member;
import java.util.Optional;
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
	public Optional<Member> findByEmail(String email) {
		return memberJpaRepository.findByEmail(email)
				.map(MemberJpaEntity::toDomain);
	}

	@Override
	public Optional<Member> findById(Long id) {
		return memberJpaRepository.findById(id)
				.map(MemberJpaEntity::toDomain);
	}

	@Override
	public Member save(Member member) {
		return memberJpaRepository.save(MemberJpaEntity.from(member)).toDomain();
	}
}
