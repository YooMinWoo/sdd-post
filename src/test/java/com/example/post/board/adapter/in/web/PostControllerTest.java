package com.example.post.board.adapter.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.post.board.application.port.in.CommentSummaryResult;
import com.example.post.board.application.port.in.CreateCommentCommand;
import com.example.post.board.application.port.in.CreateCommentResult;
import com.example.post.board.application.port.in.CreateCommentUseCase;
import com.example.post.board.application.port.in.CreatePostCommand;
import com.example.post.board.application.port.in.CreatePostResult;
import com.example.post.board.application.port.in.CreatePostUseCase;
import com.example.post.board.application.port.in.DeleteCommentCommand;
import com.example.post.board.application.port.in.DeleteCommentUseCase;
import com.example.post.board.application.port.in.DeletePostCommand;
import com.example.post.board.application.port.in.DeletePostUseCase;
import com.example.post.board.application.port.in.ListPostCommentsQuery;
import com.example.post.board.application.port.in.ListPostCommentsResult;
import com.example.post.board.application.port.in.ListPostCommentsUseCase;
import com.example.post.board.application.port.in.ListPostsQuery;
import com.example.post.board.application.port.in.ListPostsResult;
import com.example.post.board.application.port.in.ListPostsUseCase;
import com.example.post.board.application.port.in.PostSummaryResult;
import com.example.post.board.application.port.in.ReadPostQuery;
import com.example.post.board.application.port.in.ReadPostResult;
import com.example.post.board.application.port.in.ReadPostUseCase;
import com.example.post.board.application.port.in.UpdatePostCommand;
import com.example.post.board.application.port.in.UpdatePostResult;
import com.example.post.board.application.port.in.UpdatePostUseCase;
import com.example.post.board.application.port.in.UpdateCommentCommand;
import com.example.post.board.application.port.in.UpdateCommentResult;
import com.example.post.board.application.port.in.UpdateCommentUseCase;
import com.example.post.board.exception.BoardErrorCode;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.exception.ErrorCode;
import com.example.post.global.security.AuthenticatedMemberPrincipal;
import com.example.post.global.web.GlobalExceptionHandler;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
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
	private final FakeCreateCommentUseCase createCommentUseCase = new FakeCreateCommentUseCase();
	private final FakeReadPostUseCase readPostUseCase = new FakeReadPostUseCase();
	private final FakeListPostsUseCase listPostsUseCase = new FakeListPostsUseCase();
	private final FakeListPostCommentsUseCase listPostCommentsUseCase = new FakeListPostCommentsUseCase();
	private final FakeUpdatePostUseCase updatePostUseCase = new FakeUpdatePostUseCase();
	private final FakeUpdateCommentUseCase updateCommentUseCase = new FakeUpdateCommentUseCase();
	private final FakeDeleteCommentUseCase deleteCommentUseCase = new FakeDeleteCommentUseCase();
	private final FakeDeletePostUseCase deletePostUseCase = new FakeDeletePostUseCase();
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new PostController(
					createPostUseCase,
					createCommentUseCase,
					readPostUseCase,
					listPostsUseCase,
					listPostCommentsUseCase,
					updatePostUseCase,
					updateCommentUseCase,
					deleteCommentUseCase,
					deletePostUseCase
			))
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
				.andExpect(jsonPath("$.message").value("게시글이 생성되었습니다."))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.title").doesNotExist())
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
				.andExpect(jsonPath("$.path").value("/posts"));
	}

	@Test
	void returnsCreatedComment() throws Exception {
		mockMvc.perform(post("/posts/{postId}/comments", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": "Good post"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("댓글이 생성되었습니다."))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.content").doesNotExist());

		assertEquals(1L, createCommentUseCase.command.postId());
		assertEquals("Good post", createCommentUseCase.command.content());
		assertEquals(1L, createCommentUseCase.command.authorMemberId());
	}

	@Test
	void returnsUnauthorizedForCreateCommentWithoutAuthentication() throws Exception {
		mockMvc.perform(post("/posts/{postId}/comments", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": "Good post"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
				.andExpect(jsonPath("$.message").value("로그인이 필요합니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments"));
	}

	@Test
	void returnsNotFoundForCreateCommentMissingPost() throws Exception {
		createCommentUseCase.errorCode = BoardErrorCode.POST_NOT_FOUND;

		mockMvc.perform(post("/posts/{postId}/comments", 999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": "Good post"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/999/comments"));
	}

	@Test
	void returnsBadRequestForCreateCommentBlankContent() throws Exception {
		createCommentUseCase.errorCode = BoardErrorCode.COMMENT_CONTENT_REQUIRED;

		mockMvc.perform(post("/posts/{postId}/comments", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": " "
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMENT_CONTENT_REQUIRED"))
				.andExpect(jsonPath("$.message").value("댓글 본문은 필수입니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments"));
	}

	@Test
	void returnsReadPostWithCommentCount() throws Exception {
		mockMvc.perform(get("/posts/{postId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("게시글을 조회했습니다."))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.title").value("Hello"))
				.andExpect(jsonPath("$.data.content").value("First post"))
				.andExpect(jsonPath("$.data.authorMemberId").value(2))
				.andExpect(jsonPath("$.data.author").value("minu"))
				.andExpect(jsonPath("$.data.createdAt").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.data.commentCount").value(3))
				.andExpect(jsonPath("$.data.comments").doesNotExist())
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.errors").isArray());

		assertEquals(1L, readPostUseCase.query.postId());
	}

	@Test
	void returnsNotFoundForMissingPost() throws Exception {
		readPostUseCase.errorCode = BoardErrorCode.POST_NOT_FOUND;

		mockMvc.perform(get("/posts/{postId}", 999L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/999"));
	}

	@Test
	void returnsBadRequestForInvalidPostId() throws Exception {
		readPostUseCase.errorCode = com.example.post.global.exception.GlobalErrorCode.INVALID_REQUEST;

		mockMvc.perform(get("/posts/{postId}", 0L))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.message").value("요청 값이 올바르지 않습니다."))
				.andExpect(jsonPath("$.path").value("/posts/0"));
	}

	@Test
	void returnsPostComments() throws Exception {
		mockMvc.perform(get("/posts/{postId}/comments", 1L)
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("댓글 목록을 조회했습니다."))
				.andExpect(jsonPath("$.data.items[0].id").value(2))
				.andExpect(jsonPath("$.data.items[0].authorMemberId").value(3))
				.andExpect(jsonPath("$.data.items[0].author").value("jane"))
				.andExpect(jsonPath("$.data.items[0].content").value("Second comment"))
				.andExpect(jsonPath("$.data.items[0].createdAt").value("2026-05-21T02:00:00Z"))
				.andExpect(jsonPath("$.data.page").value(0))
				.andExpect(jsonPath("$.data.size").value(10))
				.andExpect(jsonPath("$.data.totalElements").value(2))
				.andExpect(jsonPath("$.data.totalPages").value(1))
				.andExpect(jsonPath("$.data.first").value(true))
				.andExpect(jsonPath("$.data.last").value(true));

		assertEquals(1L, listPostCommentsUseCase.query.postId());
		assertEquals(0, listPostCommentsUseCase.query.page());
		assertEquals(10, listPostCommentsUseCase.query.size());
	}

	@Test
	void returnsBadRequestForInvalidPostCommentsPage() throws Exception {
		listPostCommentsUseCase.errorCode = com.example.post.global.exception.GlobalErrorCode.INVALID_REQUEST;

		mockMvc.perform(get("/posts/{postId}/comments", 1L)
						.param("page", "-1")
						.param("size", "10"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.message").value("요청 값이 올바르지 않습니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments"));
	}

	@Test
	void returnsNotFoundForPostCommentsMissingPost() throws Exception {
		listPostCommentsUseCase.errorCode = BoardErrorCode.POST_NOT_FOUND;

		mockMvc.perform(get("/posts/{postId}/comments", 999L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/999/comments"));
	}

	@Test
	void returnsPostListWithoutContentAndWithCommentCount() throws Exception {
		mockMvc.perform(get("/posts")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("게시글 목록을 조회했습니다."))
				.andExpect(jsonPath("$.data.posts[0].id").value(2))
				.andExpect(jsonPath("$.data.posts[0].title").value("Second"))
				.andExpect(jsonPath("$.data.posts[0].content").doesNotExist())
				.andExpect(jsonPath("$.data.posts[0].authorMemberId").value(1))
				.andExpect(jsonPath("$.data.posts[0].author").value("minu"))
				.andExpect(jsonPath("$.data.posts[0].createdAt").value("2026-05-20T01:00:00Z"))
				.andExpect(jsonPath("$.data.posts[0].commentCount").value(3))
				.andExpect(jsonPath("$.data.page").value(0))
				.andExpect(jsonPath("$.data.size").value(10))
				.andExpect(jsonPath("$.data.totalElements").value(2))
				.andExpect(jsonPath("$.data.totalPages").value(1))
				.andExpect(jsonPath("$.data.first").value(true))
				.andExpect(jsonPath("$.data.last").value(true));
	}

	@Test
	void returnsBadRequestForInvalidPageRequest() throws Exception {
		listPostsUseCase.errorCode = com.example.post.global.exception.GlobalErrorCode.INVALID_REQUEST;

		mockMvc.perform(get("/posts")
						.param("page", "-1")
						.param("size", "10"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.message").value("요청 값이 올바르지 않습니다."))
				.andExpect(jsonPath("$.path").value("/posts"));
	}

	@Test
	void returnsUpdatedPost() throws Exception {
		mockMvc.perform(patch("/posts/{postId}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "Updated",
								  "content": "Updated content"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("게시글이 수정되었습니다."))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.title").value("Updated"))
				.andExpect(jsonPath("$.data.content").value("Updated content"))
				.andExpect(jsonPath("$.data.authorMemberId").value(1))
				.andExpect(jsonPath("$.data.author").value("minu"))
				.andExpect(jsonPath("$.data.createdAt").value("2026-05-20T00:00:00Z"))
				.andExpect(jsonPath("$.data.commentCount").value(2));

		assertEquals(1L, updatePostUseCase.command.postId());
		assertEquals("Updated", updatePostUseCase.command.title());
		assertEquals("Updated content", updatePostUseCase.command.content());
		assertEquals(1L, updatePostUseCase.command.requesterMemberId());
	}

	@Test
	void returnsUnauthorizedForUpdatePostWithoutAuthentication() throws Exception {
		mockMvc.perform(patch("/posts/{postId}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "Updated",
								  "content": "Updated content"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
				.andExpect(jsonPath("$.message").value("로그인이 필요합니다."))
				.andExpect(jsonPath("$.path").value("/posts/1"));
	}

	@Test
	void returnsNotFoundForUpdatePostMissingPost() throws Exception {
		updatePostUseCase.errorCode = BoardErrorCode.POST_NOT_FOUND;

		mockMvc.perform(patch("/posts/{postId}", 999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "Updated",
								  "content": "Updated content"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/999"));
	}

	@Test
	void returnsForbiddenForUpdatePostByNonAuthor() throws Exception {
		updatePostUseCase.errorCode = BoardErrorCode.POST_UPDATE_FORBIDDEN;

		mockMvc.perform(patch("/posts/{postId}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "Updated",
								  "content": "Updated content"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_UPDATE_FORBIDDEN"))
				.andExpect(jsonPath("$.message").value("게시글 수정 권한이 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/1"));
	}

	@Test
	void returnsBadRequestForUpdatePostInvalidInput() throws Exception {
		updatePostUseCase.errorCode = BoardErrorCode.POST_TITLE_REQUIRED;

		mockMvc.perform(patch("/posts/{postId}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "",
								  "content": "Updated content"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_TITLE_REQUIRED"))
				.andExpect(jsonPath("$.message").value("게시글 제목은 필수입니다."))
				.andExpect(jsonPath("$.path").value("/posts/1"));
	}

	@Test
	void returnsUpdatedComment() throws Exception {
		mockMvc.perform(patch("/posts/{postId}/comments/{commentId}", 1L, 10L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": "Updated comment"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("댓글이 수정되었습니다."))
				.andExpect(jsonPath("$.data.id").value(10))
				.andExpect(jsonPath("$.data.authorMemberId").value(1))
				.andExpect(jsonPath("$.data.author").value("minu"))
				.andExpect(jsonPath("$.data.content").value("Updated comment"))
				.andExpect(jsonPath("$.data.createdAt").value("2026-05-21T00:00:00Z"));

		assertEquals(1L, updateCommentUseCase.command.postId());
		assertEquals(10L, updateCommentUseCase.command.commentId());
		assertEquals("Updated comment", updateCommentUseCase.command.content());
		assertEquals(1L, updateCommentUseCase.command.requesterMemberId());
	}

	@Test
	void returnsUnauthorizedForUpdateCommentWithoutAuthentication() throws Exception {
		mockMvc.perform(patch("/posts/{postId}/comments/{commentId}", 1L, 10L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": "Updated comment"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
				.andExpect(jsonPath("$.message").value("로그인이 필요합니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments/10"));
	}

	@Test
	void returnsNotFoundForUpdateCommentMissingComment() throws Exception {
		updateCommentUseCase.errorCode = BoardErrorCode.COMMENT_NOT_FOUND;

		mockMvc.perform(patch("/posts/{postId}/comments/{commentId}", 1L, 999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": "Updated comment"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMENT_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("댓글을 찾을 수 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments/999"));
	}

	@Test
	void returnsForbiddenForUpdateCommentByNonAuthor() throws Exception {
		updateCommentUseCase.errorCode = BoardErrorCode.COMMENT_UPDATE_FORBIDDEN;

		mockMvc.perform(patch("/posts/{postId}/comments/{commentId}", 1L, 10L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": "Updated comment"
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMENT_UPDATE_FORBIDDEN"))
				.andExpect(jsonPath("$.message").value("댓글 수정 권한이 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments/10"));
	}

	@Test
	void returnsBadRequestForUpdateCommentInvalidContent() throws Exception {
		updateCommentUseCase.errorCode = BoardErrorCode.COMMENT_CONTENT_REQUIRED;

		mockMvc.perform(patch("/posts/{postId}/comments/{commentId}", 1L, 10L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "content": " "
								}
								""")
						.with(authenticatedMember()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMENT_CONTENT_REQUIRED"))
				.andExpect(jsonPath("$.message").value("댓글 본문은 필수입니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments/10"));
	}

	@Test
	void returnsNoContentWhenDeletingComment() throws Exception {
		mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 1L, 10L)
						.with(authenticatedMember()))
				.andExpect(status().isNoContent())
				.andExpect(content().string(""));

		assertEquals(1L, deleteCommentUseCase.command.postId());
		assertEquals(10L, deleteCommentUseCase.command.commentId());
		assertEquals(1L, deleteCommentUseCase.command.requesterMemberId());
	}

	@Test
	void returnsUnauthorizedForDeleteCommentWithoutAuthentication() throws Exception {
		mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 1L, 10L))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
				.andExpect(jsonPath("$.message").value("로그인이 필요합니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments/10"));
	}

	@Test
	void returnsNotFoundForDeleteCommentMissingPost() throws Exception {
		deleteCommentUseCase.errorCode = BoardErrorCode.POST_NOT_FOUND;

		mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 999L, 10L)
						.with(authenticatedMember()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/999/comments/10"));
	}

	@Test
	void returnsNotFoundForDeleteCommentMissingComment() throws Exception {
		deleteCommentUseCase.errorCode = BoardErrorCode.COMMENT_NOT_FOUND;

		mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 1L, 999L)
						.with(authenticatedMember()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMENT_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("댓글을 찾을 수 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments/999"));
	}

	@Test
	void returnsForbiddenForDeleteCommentByNonAuthor() throws Exception {
		deleteCommentUseCase.errorCode = BoardErrorCode.COMMENT_DELETE_FORBIDDEN;

		mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 1L, 10L)
						.with(authenticatedMember()))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMENT_DELETE_FORBIDDEN"))
				.andExpect(jsonPath("$.message").value("댓글 삭제 권한이 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/1/comments/10"));
	}

	@Test
	void returnsBadRequestForDeleteCommentInvalidRequest() throws Exception {
		deleteCommentUseCase.errorCode = com.example.post.global.exception.GlobalErrorCode.INVALID_REQUEST;

		mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 0L, 10L)
						.with(authenticatedMember()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.message").value("요청 값이 올바르지 않습니다."))
				.andExpect(jsonPath("$.path").value("/posts/0/comments/10"));
	}

	@Test
	void returnsNoContentWhenDeletingPost() throws Exception {
		mockMvc.perform(delete("/posts/{postId}", 1L)
						.with(authenticatedMember()))
				.andExpect(status().isNoContent())
				.andExpect(content().string(""));

		assertEquals(1L, deletePostUseCase.command.postId());
		assertEquals(1L, deletePostUseCase.command.requesterMemberId());
	}

	@Test
	void returnsUnauthorizedForDeleteWithoutAuthentication() throws Exception {
		mockMvc.perform(delete("/posts/{postId}", 1L))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
				.andExpect(jsonPath("$.message").value("로그인이 필요합니다."))
				.andExpect(jsonPath("$.path").value("/posts/1"));
	}

	@Test
	void returnsForbiddenForDeleteByNonAuthor() throws Exception {
		deletePostUseCase.errorCode = BoardErrorCode.POST_DELETE_FORBIDDEN;

		mockMvc.perform(delete("/posts/{postId}", 1L)
						.with(authenticatedMember()))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("POST_DELETE_FORBIDDEN"))
				.andExpect(jsonPath("$.message").value("게시글 삭제 권한이 없습니다."))
				.andExpect(jsonPath("$.path").value("/posts/1"));
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

		private ErrorCode errorCode;

		@Override
		public CreatePostResult createPost(CreatePostCommand command) {
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
			return new CreatePostResult(1L);
		}
	}

	private static class FakeCreateCommentUseCase implements CreateCommentUseCase {

		private CreateCommentCommand command;
		private ErrorCode errorCode;

		@Override
		public CreateCommentResult createComment(CreateCommentCommand command) {
			this.command = command;
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
			return new CreateCommentResult(1L);
		}
	}

	private static class FakeReadPostUseCase implements ReadPostUseCase {

		private ReadPostQuery query;
		private ErrorCode errorCode;

		@Override
		public ReadPostResult readPost(ReadPostQuery query) {
			this.query = query;
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
			return new ReadPostResult(
					query.postId(),
					"Hello",
					"First post",
					2L,
					"minu",
					Instant.parse("2026-05-20T00:00:00Z"),
					3
			);
		}
	}

	private static class FakeListPostCommentsUseCase implements ListPostCommentsUseCase {

		private ListPostCommentsQuery query;
		private ErrorCode errorCode;

		@Override
		public ListPostCommentsResult listPostComments(ListPostCommentsQuery query) {
			this.query = query;
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
			return new ListPostCommentsResult(
					List.of(
							new CommentSummaryResult(
									2L,
									3L,
									"jane",
									"Second comment",
									Instant.parse("2026-05-21T02:00:00Z")
							),
							new CommentSummaryResult(
									1L,
									4L,
									"kim",
									"First comment",
									Instant.parse("2026-05-21T01:00:00Z")
							)
					),
					query.page(),
					query.size(),
					2,
					1,
					true,
					true
			);
		}
	}

	private static class FakeListPostsUseCase implements ListPostsUseCase {

		private ErrorCode errorCode;

		@Override
		public ListPostsResult listPosts(ListPostsQuery query) {
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
			return new ListPostsResult(
					List.of(
							new PostSummaryResult(
									2L,
									"Second",
									1L,
									"minu",
									Instant.parse("2026-05-20T01:00:00Z"),
									3
							),
							new PostSummaryResult(
									1L,
									"First",
									1L,
									"minu",
									Instant.parse("2026-05-20T00:00:00Z"),
									0
							)
					),
					query.page(),
					query.size(),
					2,
					1,
					true,
					true
			);
		}
	}

	private static class FakeUpdatePostUseCase implements UpdatePostUseCase {

		private UpdatePostCommand command;
		private ErrorCode errorCode;

		@Override
		public UpdatePostResult updatePost(UpdatePostCommand command) {
			this.command = command;
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
			return new UpdatePostResult(
					command.postId(),
					command.title(),
					command.content(),
					command.requesterMemberId(),
					"minu",
					Instant.parse("2026-05-20T00:00:00Z"),
					2
			);
		}
	}

	private static class FakeUpdateCommentUseCase implements UpdateCommentUseCase {

		private UpdateCommentCommand command;
		private ErrorCode errorCode;

		@Override
		public UpdateCommentResult updateComment(UpdateCommentCommand command) {
			this.command = command;
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
			return new UpdateCommentResult(
					command.commentId(),
					command.requesterMemberId(),
					"minu",
					command.content(),
					Instant.parse("2026-05-21T00:00:00Z")
			);
		}
	}

	private static class FakeDeleteCommentUseCase implements DeleteCommentUseCase {

		private DeleteCommentCommand command;
		private ErrorCode errorCode;

		@Override
		public void deleteComment(DeleteCommentCommand command) {
			this.command = command;
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
		}
	}

	private static class FakeDeletePostUseCase implements DeletePostUseCase {

		private DeletePostCommand command;
		private ErrorCode errorCode;

		@Override
		public void deletePost(DeletePostCommand command) {
			this.command = command;
			if (errorCode != null) {
				throw new BusinessException(errorCode);
			}
		}
	}
}
