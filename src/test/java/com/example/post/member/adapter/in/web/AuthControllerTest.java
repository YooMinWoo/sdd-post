package com.example.post.member.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.ErrorCode;
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
import com.example.post.member.exception.MemberErrorCode;
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
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.code").doesNotExist())
				.andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.email").value("minu@example.com"))
				.andExpect(jsonPath("$.data.nickname").value("minu"))
				.andExpect(jsonPath("$.data.createdAt").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.data.password").doesNotExist())
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.errors").isArray());
	}

	@Test
	void returnsBadRequestForInvalidInput() throws Exception {
		signupUseCase.errorCode = MemberErrorCode.INVALID_EMAIL;

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
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").doesNotExist())
				.andExpect(jsonPath("$.message").value("INVALID_EMAIL"))
				.andExpect(jsonPath("$.data").doesNotExist())
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
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.code").doesNotExist())
				.andExpect(jsonPath("$.message").value("로그인되었습니다."))
				.andExpect(jsonPath("$.data.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.data.accessToken").value("access-token"))
				.andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
				.andExpect(jsonPath("$.data.expiresIn").value(900))
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.errors").isArray());
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
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").doesNotExist())
				.andExpect(jsonPath("$.message").value("INVALID_CREDENTIALS"))
				.andExpect(jsonPath("$.data").doesNotExist())
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
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.code").doesNotExist())
				.andExpect(jsonPath("$.message").value("토큰이 재발급되었습니다."))
				.andExpect(jsonPath("$.data.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
				.andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"))
				.andExpect(jsonPath("$.data.expiresIn").value(900))
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.errors").isArray());
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
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").doesNotExist())
				.andExpect(jsonPath("$.message").value("INVALID_REFRESH_TOKEN"))
				.andExpect(jsonPath("$.data").doesNotExist())
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
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").doesNotExist())
				.andExpect(jsonPath("$.message").value("DUPLICATE_EMAIL"))
				.andExpect(jsonPath("$.data").doesNotExist())
				.andExpect(jsonPath("$.path").value("/auth/signup"))
				.andExpect(jsonPath("$.timestamp").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.errors").isArray());
	}

	private static class FakeSignupUseCase implements SignupUseCase {

		private boolean throwIllegalArgumentException;
		private boolean throwDuplicateEmailException;
		private ErrorCode errorCode;

		@Override
		public SignupResult signup(SignupCommand command) {
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
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
