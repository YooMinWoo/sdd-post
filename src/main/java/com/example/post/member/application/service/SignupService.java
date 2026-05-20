package com.example.post.member.application.service;

import com.example.post.global.exception.BusinessException;
import com.example.post.member.application.exception.DuplicateEmailException;
import com.example.post.member.application.port.in.SignupCommand;
import com.example.post.member.application.port.in.SignupResult;
import com.example.post.member.application.port.in.SignupUseCase;
import com.example.post.member.application.port.out.MemberRepositoryPort;
import com.example.post.member.application.port.out.PasswordEncoderPort;
import com.example.post.member.domain.model.Member;
import com.example.post.member.exception.MemberErrorCode;
import java.time.Instant;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupService implements SignupUseCase {

	private static final int MIN_PASSWORD_LENGTH = 8;

	private final MemberRepositoryPort memberRepositoryPort;
	private final PasswordEncoderPort passwordEncoderPort;

	public SignupService(MemberRepositoryPort memberRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
		this.memberRepositoryPort = memberRepositoryPort;
		this.passwordEncoderPort = passwordEncoderPort;
	}

	@Override
	@Transactional
	public SignupResult signup(SignupCommand command) {
		String normalizedEmail = normalizeEmail(command.email());
		validatePassword(command.password());
		if (memberRepositoryPort.existsByEmail(normalizedEmail)) {
			throw new DuplicateEmailException();
		}

		String passwordHash = passwordEncoderPort.encode(command.password());
		Member member = Member.create(normalizedEmail, passwordHash, command.nickname(), Instant.now());
		Member savedMember = memberRepositoryPort.save(member);

		return new SignupResult(
				savedMember.getId(),
				savedMember.getEmail(),
				savedMember.getNickname(),
				savedMember.getCreatedAt()
		);
	}

	private static String normalizeEmail(String email) {
		if (email == null || email.isBlank()) {
			throw new BusinessException(MemberErrorCode.INVALID_EMAIL);
		}
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private static void validatePassword(String password) {
		if (password == null || password.isBlank()) {
			throw new BusinessException(MemberErrorCode.PASSWORD_REQUIRED);
		}
		if (password.length() < MIN_PASSWORD_LENGTH) {
			throw new BusinessException(MemberErrorCode.PASSWORD_TOO_SHORT);
		}
	}
}
