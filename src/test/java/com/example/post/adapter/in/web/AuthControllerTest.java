package com.example.post.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.post.application.exception.DuplicateEmailException;
import com.example.post.application.port.in.SignupCommand;
import com.example.post.application.port.in.SignupResult;
import com.example.post.application.port.in.SignupUseCase;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerTest {

	private final FakeSignupUseCase signupUseCase = new FakeSignupUseCase();
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new AuthController(signupUseCase))
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
}
