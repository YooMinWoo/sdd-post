package com.example.post.member.application.service;

import com.example.post.member.application.exception.InvalidCredentialsException;
import com.example.post.member.application.port.in.LoginCommand;
import com.example.post.member.application.port.in.LoginUseCase;
import com.example.post.member.application.port.in.TokenResult;
import com.example.post.member.application.port.out.MemberRepositoryPort;
import com.example.post.member.application.port.out.PasswordMatcherPort;
import com.example.post.member.application.port.out.RefreshTokenStorePort;
import com.example.post.member.application.port.out.TokenProviderPort;
import com.example.post.member.domain.model.Member;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService implements LoginUseCase {

	private final MemberRepositoryPort memberRepositoryPort;
	private final PasswordMatcherPort passwordMatcherPort;
	private final TokenProviderPort tokenProviderPort;
	private final RefreshTokenStorePort refreshTokenStorePort;

	public LoginService(
			MemberRepositoryPort memberRepositoryPort,
			PasswordMatcherPort passwordMatcherPort,
			TokenProviderPort tokenProviderPort,
			RefreshTokenStorePort refreshTokenStorePort
	) {
		this.memberRepositoryPort = memberRepositoryPort;
		this.passwordMatcherPort = passwordMatcherPort;
		this.tokenProviderPort = tokenProviderPort;
		this.refreshTokenStorePort = refreshTokenStorePort;
	}

	@Override
	@Transactional(readOnly = true)
	public TokenResult login(LoginCommand command) {
		String email = normalizeEmail(command.email());
		Member member = memberRepositoryPort.findByEmail(email)
				.orElseThrow(InvalidCredentialsException::new);

		if (!passwordMatcherPort.matches(command.password(), member.getPasswordHash())) {
			throw new InvalidCredentialsException();
		}

		TokenResult tokenResult = tokenProviderPort.issue(member);
		refreshTokenStorePort.save(member.getId(), tokenResult.refreshToken(), tokenProviderPort.refreshTokenTtl());
		return tokenResult;
	}

	private static String normalizeEmail(String email) {
		if (email == null) {
			throw new InvalidCredentialsException();
		}
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
