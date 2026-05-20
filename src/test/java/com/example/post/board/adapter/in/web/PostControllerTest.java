package com.example.post.board.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.post.board.application.port.in.CreatePostCommand;
import com.example.post.board.application.port.in.CreatePostResult;
import com.example.post.board.application.port.in.CreatePostUseCase;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.ErrorCode;
import com.example.post.global.security.AuthenticatedMemberPrincipal;
import com.example.post.global.web.GlobalExceptionHandler;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PostControllerTest {

	private final FakeCreatePostUseCase createPostUseCase = new FakeCreatePostUseCase();
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new PostController(createPostUseCase))
			.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
			.setControllerAdvice(new GlobalExceptionHandler(
					Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC)
			))
			.build();

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void returnsCreatedPost() throws Exception {
		mockMvc.perform(post("/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "Hello",
								  "content": "First post"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.code").doesNotExist())
				.andExpect(jsonPath("$.message").value("게시글이 생성되었습니다."))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.title").value("Hello"))
				.andExpect(jsonPath("$.data.content").value("First post"))
				.andExpect(jsonPath("$.data.authorMemberId").value(1))
				.andExpect(jsonPath("$.data.author").value("minu"))
				.andExpect(jsonPath("$.data.createdAt").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.errors").isArray());
	}

	@Test
	void returnsUnauthorizedForMissingAuthentication() throws Exception {
		mockMvc.perform(post("/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "Hello",
								  "content": "First post"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
				.andExpect(jsonPath("$.message").value("로그인이 필요합니다."))
				.andExpect(jsonPath("$.path").value("/posts"));
	}

	@Test
	void returnsBadRequestForInvalidInput() throws Exception {
		createPostUseCase.errorCode = BoardErrorCode.POST_TITLE_REQUIRED;

		mockMvc.perform(post("/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "",
								  "content": "First post"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_TITLE_REQUIRED"))
				.andExpect(jsonPath("$.message").value("게시글 제목은 필수입니다."))
				.andExpect(jsonPath("$.data").doesNotExist())
				.andExpect(jsonPath("$.path").value("/posts"))
				.andExpect(jsonPath("$.timestamp").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.errors").isArray());
	}

	@Test
	void returnsBadRequestForIllegalArgumentFallback() throws Exception {
		createPostUseCase.throwIllegalArgumentException = true;

		mockMvc.perform(post("/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "",
								  "content": "First post"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.message").value("요청 값이 올바르지 않습니다."))
				.andExpect(jsonPath("$.path").value("/posts"));
	}

	@Test
	void returnsBadRequestForMalformedJson() throws Exception {
		mockMvc.perform(post("/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("MALFORMED_JSON"))
				.andExpect(jsonPath("$.message").value("요청 본문 형식이 올바르지 않습니다."))
				.andExpect(jsonPath("$.data").doesNotExist())
				.andExpect(jsonPath("$.path").value("/posts"))
				.andExpect(jsonPath("$.timestamp").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.errors").isArray());
	}

	private static RequestPostProcessor authenticatedMember() {
		return request -> {
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
				new AuthenticatedMemberPrincipal(1L, "minu@example.com", "minu"),
				null
			));
			SecurityContextHolder.setContext(securityContext);
			return request;
		};
	}

	private static class FakeCreatePostUseCase implements CreatePostUseCase {

		private boolean throwIllegalArgumentException;
		private ErrorCode errorCode;

		@Override
		public CreatePostResult createPost(CreatePostCommand command) {
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
			if (throwIllegalArgumentException) {
				throw new IllegalArgumentException("title is required");
			}
			return new CreatePostResult(
					1L,
					command.title(),
					command.content(),
					command.authorMemberId(),
					"minu",
					Instant.parse("2026-05-20T00:00:00Z")
			);
		}
	}
}
