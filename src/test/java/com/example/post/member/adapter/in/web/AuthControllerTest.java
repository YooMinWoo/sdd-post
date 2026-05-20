package com.example.post.member.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.post.member.application.exception.DuplicateEmailException;
import com.example.post.member.application.exception.InvalidCredentialsException;
import com.example.post.member.application.exception.InvalidRefreshTokenException;
import com.example.post.member.application.port.in.LoginCommand;
import com.example.post.member.application.port.in.LoginUseCase;
import com.example.post.member.application.port.in.LogoutCommand;
import com.example.post.member.application.port.in.LogoutUseCase;
import com.example.post.member.application.port.in.RefreshTokenCommand;
import com.example.post.member.application.port.in.RefreshTokenUseCase;
import com.example.post.member.application.port.in.SignupCommand;
import com.example.post.member.application.port.in.SignupResult;
import com.example.post.member.application.port.in.SignupUseCase;
import com.example.post.member.application.port.in.TokenResult;
import com.example.post.global.web.GlobalExceptionHandler;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerTest {

	private final FakeSignupUseCase signupUseCase = new FakeSignupUseCase();
	private final FakeLoginUseCase loginUseCase = new FakeLoginUseCase();
	private final FakeRefreshTokenUseCase refreshTokenUseCase = new FakeRefreshTokenUseCase();
	private final FakeLogoutUseCase logoutUseCase = new FakeLogoutUseCase();
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new AuthController(signupUseCase, loginUseCase, refreshTokenUseCase, logoutUseCase))
			.setControllerAdvice(new GlobalExceptionHandler(
					Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC)
			))
			.build();

	@Test
	void returnsSignedUpMember() throws Exception {
		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "minu@example.com",
								  "password": "password123",
								  "nickname": "minu"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.email").value("minu@example.com"))
				.andExpect(jsonPath("$.nickname").value("minu"))
				.andExpect(jsonPath("$.createdAt").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void returnsBadRequestForInvalidInput() throws Exception {
		signupUseCase.throwIllegalArgumentException = true;

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "invalid-email",
								  "password": "password123",
								  "nickname": "minu"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.message").value("email must be valid"))
				.andExpect(jsonPath("$.path").value("/auth/signup"))
				.andExpect(jsonPath("$.timestamp").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.errors").isArray());
	}

	@Test
	void returnsLoginTokens() throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "minu@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.accessToken").value("access-token"))
				.andExpect(jsonPath("$.refreshToken").value("refresh-token"))
				.andExpect(jsonPath("$.expiresIn").value(900));
	}

	@Test
	void returnsUnauthorizedForInvalidCredentials() throws Exception {
		loginUseCase.throwInvalidCredentialsException = true;

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "minu@example.com",
								  "password": "wrong-password"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
				.andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."))
				.andExpect(jsonPath("$.path").value("/auth/login"));
	}

	@Test
	void returnsRefreshedTokens() throws Exception {
		mockMvc.perform(post("/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "refreshToken": "refresh-token"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.accessToken").value("new-access-token"))
				.andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
				.andExpect(jsonPath("$.expiresIn").value(900));
	}

	@Test
	void returnsUnauthorizedForInvalidRefreshToken() throws Exception {
		refreshTokenUseCase.throwInvalidRefreshTokenException = true;

		mockMvc.perform(post("/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "refreshToken": "invalid-refresh-token"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"))
				.andExpect(jsonPath("$.message").value("유효하지 않은 refreshToken입니다."))
				.andExpect(jsonPath("$.path").value("/auth/refresh"));
	}

	@Test
	void returnsNoContentForLogout() throws Exception {
		mockMvc.perform(post("/auth/logout")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "refreshToken": "refresh-token"
								}
								"""))
				.andExpect(status().isNoContent());
	}

	@Test
	void returnsConflictForDuplicateEmail() throws Exception {
		signupUseCase.throwDuplicateEmailException = true;

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "minu@example.com",
								  "password": "password123",
								  "nickname": "minu"
								}
								"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"))
				.andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."))
				.andExpect(jsonPath("$.path").value("/auth/signup"))
				.andExpect(jsonPath("$.timestamp").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.errors").isArray());
	}

	private static class FakeSignupUseCase implements SignupUseCase {

		private boolean throwIllegalArgumentException;
		private boolean throwDuplicateEmailException;

		@Override
		public SignupResult signup(SignupCommand command) {
			if (throwIllegalArgumentException) {
				throw new IllegalArgumentException("email must be valid");
			}
			if (throwDuplicateEmailException) {
				throw new DuplicateEmailException();
			}
			return new SignupResult(
					1L,
					command.email(),
					command.nickname(),
					Instant.parse("2026-05-20T00:00:00Z")
			);
		}
	}

	private static class FakeLoginUseCase implements LoginUseCase {

		private boolean throwInvalidCredentialsException;

		@Override
		public TokenResult login(LoginCommand command) {
			if (throwInvalidCredentialsException) {
				throw new InvalidCredentialsException();
			}
			return new TokenResult("Bearer", "access-token", "refresh-token", 900);
		}
	}

	private static class FakeRefreshTokenUseCase implements RefreshTokenUseCase {

		private boolean throwInvalidRefreshTokenException;

		@Override
		public TokenResult refresh(RefreshTokenCommand command) {
			if (throwInvalidRefreshTokenException) {
				throw new InvalidRefreshTokenException();
			}
			return new TokenResult("Bearer", "new-access-token", "new-refresh-token", 900);
		}
	}

	private static class FakeLogoutUseCase implements LogoutUseCase {

		@Override
		public void logout(LogoutCommand command) {
		}
	}
}
