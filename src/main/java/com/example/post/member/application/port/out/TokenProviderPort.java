package com.example.post.member.application.port.out;

import com.example.post.member.application.port.in.TokenResult;
import com.example.post.member.domain.model.Member;
import java.time.Duration;

public interface TokenProviderPort {

	TokenResult issue(Member member);

	Long extractRefreshTokenMemberId(String refreshToken);

	Duration refreshTokenTtl();
}
