package com.example.post.board.adapter.out.persistence;

import com.example.post.board.application.port.out.AuthorMemberPort;
import com.example.post.member.application.exception.InvalidAccessTokenException;
import com.example.post.member.application.port.out.MemberRepositoryPort;
import com.example.post.member.domain.model.Member;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorMemberPersistenceAdapter implements AuthorMemberPort {

	private final MemberRepositoryPort memberRepositoryPort;

	public AuthorMemberPersistenceAdapter(MemberRepositoryPort memberRepositoryPort) {
		this.memberRepositoryPort = memberRepositoryPort;
	}

	@Override
	public String getNicknameById(Long memberId) {
		return memberRepositoryPort.findById(memberId)
				.orElseThrow(InvalidAccessTokenException::new)
				.getNickname();
	}

	@Override
	public Map<Long, String> getNicknamesByIds(Set<Long> memberIds) {
		return memberRepositoryPort.findAllById(memberIds).stream()
				.collect(Collectors.toMap(Member::getId, Member::getNickname));
	}
}
