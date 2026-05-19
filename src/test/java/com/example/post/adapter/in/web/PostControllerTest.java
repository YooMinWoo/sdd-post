package com.example.post.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.post.application.port.in.CreatePostCommand;
import com.example.post.application.port.in.CreatePostResult;
import com.example.post.application.port.in.CreatePostUseCase;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PostControllerTest {

	private final FakeCreatePostUseCase createPostUseCase = new FakeCreatePostUseCase();
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new PostController(createPostUseCase))
			.setControllerAdvice(new GlobalExceptionHandler(
					Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC)
			))
			.build();

	@Test
	void returnsCreatedPost() throws Exception {
		mockMvc.perform(post("/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "Hello",
								  "content": "First post",
								  "author": "minu"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Hello"))
				.andExpect(jsonPath("$.content").value("First post"))
				.andExpect(jsonPath("$.author").value("minu"))
				.andExpect(jsonPath("$.createdAt").value("2026-05-20T00:00:00Z"));
	}

	@Test
	void returnsBadRequestForInvalidInput() throws Exception {
		createPostUseCase.throwIllegalArgumentException = true;

		mockMvc.perform(post("/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "",
								  "content": "First post",
								  "author": "minu"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.message").value("title is required"))
				.andExpect(jsonPath("$.path").value("/posts"))
				.andExpect(jsonPath("$.timestamp").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.errors").isArray());
	}

	@Test
	void returnsBadRequestForMalformedJson() throws Exception {
		mockMvc.perform(post("/posts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("MALFORMED_JSON"))
				.andExpect(jsonPath("$.message").value("요청 본문을 읽을 수 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts"))
				.andExpect(jsonPath("$.timestamp").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.errors").isArray());
	}

	private static class FakeCreatePostUseCase implements CreatePostUseCase {

		private boolean throwIllegalArgumentException;

		@Override
		public CreatePostResult createPost(CreatePostCommand command) {
			if (throwIllegalArgumentException) {
				throw new IllegalArgumentException("title is required");
			}
			return new CreatePostResult(
					1L,
					command.title(),
					command.content(),
					command.author(),
					Instant.parse("2026-05-20T00:00:00Z")
			);
		}
	}
}
