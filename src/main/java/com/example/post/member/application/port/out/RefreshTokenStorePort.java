package com.example.post.member.application.port.out;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenStorePort {

	void save(Long memberId, String refreshToken, Duration ttl);

	Optional<String> findByMemberId(Long memberId);

	void deleteByMemberId(Long memberId);
}
