package com.example.post.board.adapter.in.web;

import com.example.post.board.application.port.in.CreatePostCommand;
import com.example.post.board.application.port.in.CreatePostResult;
import com.example.post.board.application.port.in.CreatePostUseCase;
import com.example.post.board.application.port.in.CreateCommentCommand;
import com.example.post.board.application.port.in.CreateCommentResult;
import com.example.post.board.application.port.in.CreateCommentUseCase;
import com.example.post.board.application.port.in.CommentSummaryResult;
import com.example.post.board.application.port.in.DeleteCommentCommand;
import com.example.post.board.application.port.in.DeleteCommentUseCase;
import com.example.post.board.application.port.in.DeletePostCommand;
import com.example.post.board.application.port.in.DeletePostUseCase;
import com.example.post.board.application.port.in.ListPostsQuery;
import com.example.post.board.application.port.in.ListPostsResult;
import com.example.post.board.application.port.in.ListPostsUseCase;
import com.example.post.board.application.port.in.ListPostCommentsQuery;
import com.example.post.board.application.port.in.ListPostCommentsResult;
import com.example.post.board.application.port.in.ListPostCommentsUseCase;
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
import com.example.post.global.exception.BusinessException;
import com.example.post.global.security.AuthenticatedMemberPrincipal;
import com.example.post.global.web.ApiResponse;
import com.example.post.global.web.swagger.CreateCommentApiDocs;
import com.example.post.global.web.swagger.CreatePostApiDocs;
import com.example.post.global.web.swagger.DeleteCommentApiDocs;
import com.example.post.global.web.swagger.DeletePostApiDocs;
import com.example.post.global.web.swagger.ListPostCommentsApiDocs;
import com.example.post.global.web.swagger.ListPostsApiDocs;
import com.example.post.global.web.swagger.ReadPostApiDocs;
import com.example.post.global.web.swagger.UpdatePostApiDocs;
import com.example.post.global.web.swagger.UpdateCommentApiDocs;
import com.example.post.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "게시글", description = "게시글과 댓글 API")
public class PostController {

	private final CreatePostUseCase createPostUseCase;
	private final CreateCommentUseCase createCommentUseCase;
	private final ReadPostUseCase readPostUseCase;
	private final ListPostsUseCase listPostsUseCase;
	private final ListPostCommentsUseCase listPostCommentsUseCase;
	private final UpdatePostUseCase updatePostUseCase;
	private final UpdateCommentUseCase updateCommentUseCase;
	private final DeleteCommentUseCase deleteCommentUseCase;
	private final DeletePostUseCase deletePostUseCase;

	@PostMapping
	@CreatePostApiDocs
	public ResponseEntity<ApiResponse<CreatePostResponse>> createPost(
			@RequestBody CreatePostRequest request,
			@AuthenticationPrincipal AuthenticatedMemberPrincipal principal
	) {
		if (principal == null) {
			throw new BusinessException(MemberErrorCode.UNAUTHORIZED);
		}
		CreatePostResult result = createPostUseCase.createPost(
				new CreatePostCommand(request.title(), request.content(), principal.id())
		);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(
						"게시글이 생성되었습니다.",
						new CreatePostResponse(
								result.id()
						)
				));
	}

	@PostMapping("/{postId}/comments")
	@CreateCommentApiDocs
	public ResponseEntity<ApiResponse<CreateCommentResponse>> createComment(
			@PathVariable Long postId,
			@RequestBody CreateCommentRequest request,
			@AuthenticationPrincipal AuthenticatedMemberPrincipal principal
	) {
		if (principal == null) {
			throw new BusinessException(MemberErrorCode.UNAUTHORIZED);
		}
		CreateCommentResult result = createCommentUseCase.createComment(
				new CreateCommentCommand(postId, request.content(), principal.id())
		);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(
						"댓글이 생성되었습니다.",
						new CreateCommentResponse(result.id())
				));
	}

	@GetMapping("/{postId}")
	@ReadPostApiDocs
	public ResponseEntity<ApiResponse<ReadPostResponse>> readPost(@PathVariable Long postId) {
		ReadPostResult result = readPostUseCase.readPost(new ReadPostQuery(postId));

		return ResponseEntity.ok(ApiResponse.success(
				"게시글을 조회했습니다.",
				new ReadPostResponse(
						result.id(),
						result.title(),
						result.content(),
						result.authorMemberId(),
						result.author(),
						result.createdAt(),
						result.commentCount()
				)
		));
	}

	@GetMapping("/{postId}/comments")
	@ListPostCommentsApiDocs
	public ResponseEntity<ApiResponse<CommentPageResponse>> listPostComments(
			@PathVariable Long postId,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size
	) {
		ListPostCommentsResult result = listPostCommentsUseCase.listPostComments(
				new ListPostCommentsQuery(postId, page, size)
		);

		return ResponseEntity.ok(ApiResponse.success(
				"댓글 목록을 조회했습니다.",
				toCommentPageResponse(result)
		));
	}

	@GetMapping
	@ListPostsApiDocs
	public ResponseEntity<ApiResponse<ListPostsResponse>> listPosts(
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size
	) {
		ListPostsResult result = listPostsUseCase.listPosts(new ListPostsQuery(page, size));

		return ResponseEntity.ok(ApiResponse.success(
				"게시글 목록을 조회했습니다.",
				new ListPostsResponse(
						toPostSummaryResponses(result.posts()),
						result.page(),
						result.size(),
						result.totalElements(),
						result.totalPages(),
						result.first(),
						result.last()
				)
		));
	}

	@PatchMapping("/{postId}")
	@UpdatePostApiDocs
	public ResponseEntity<ApiResponse<UpdatePostResponse>> updatePost(
			@PathVariable Long postId,
			@RequestBody UpdatePostRequest request,
			@AuthenticationPrincipal AuthenticatedMemberPrincipal principal
	) {
		if (principal == null) {
			throw new BusinessException(MemberErrorCode.UNAUTHORIZED);
		}
		UpdatePostResult result = updatePostUseCase.updatePost(
				new UpdatePostCommand(postId, request.title(), request.content(), principal.id())
		);

		return ResponseEntity.ok(ApiResponse.success(
				"게시글이 수정되었습니다.",
				new UpdatePostResponse(
						result.id(),
						result.title(),
						result.content(),
						result.authorMemberId(),
						result.author(),
						result.createdAt(),
						result.commentCount()
				)
		));
	}

	@DeleteMapping("/{postId}/comments/{commentId}")
	@DeleteCommentApiDocs
	public ResponseEntity<Void> deleteComment(
			@PathVariable Long postId,
			@PathVariable Long commentId,
			@AuthenticationPrincipal AuthenticatedMemberPrincipal principal
	) {
		if (principal == null) {
			throw new BusinessException(MemberErrorCode.UNAUTHORIZED);
		}
		deleteCommentUseCase.deleteComment(new DeleteCommentCommand(postId, commentId, principal.id()));

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{postId}/comments/{commentId}")
	@UpdateCommentApiDocs
	public ResponseEntity<ApiResponse<UpdateCommentResponse>> updateComment(
			@PathVariable Long postId,
			@PathVariable Long commentId,
			@RequestBody UpdateCommentRequest request,
			@AuthenticationPrincipal AuthenticatedMemberPrincipal principal
	) {
		if (principal == null) {
			throw new BusinessException(MemberErrorCode.UNAUTHORIZED);
		}
		UpdateCommentResult result = updateCommentUseCase.updateComment(
				new UpdateCommentCommand(postId, commentId, request.content(), principal.id())
		);

		return ResponseEntity.ok(ApiResponse.success(
				"댓글이 수정되었습니다.",
				new UpdateCommentResponse(
						result.id(),
						result.authorMemberId(),
						result.author(),
						result.content(),
						result.createdAt()
				)
		));
	}

	@DeleteMapping("/{postId}")
	@DeletePostApiDocs
	public ResponseEntity<Void> deletePost(
			@PathVariable Long postId,
			@AuthenticationPrincipal AuthenticatedMemberPrincipal principal
	) {
		if (principal == null) {
			throw new BusinessException(MemberErrorCode.UNAUTHORIZED);
		}
		deletePostUseCase.deletePost(new DeletePostCommand(postId, principal.id()));

		return ResponseEntity.noContent().build();
	}

	private static List<PostSummaryResponse> toPostSummaryResponses(List<PostSummaryResult> posts) {
		return posts.stream()
				.map(post -> new PostSummaryResponse(
						post.id(),
						post.title(),
						post.authorMemberId(),
						post.author(),
						post.createdAt(),
						post.commentCount()
				))
				.toList();
	}

	private static CommentPageResponse toCommentPageResponse(ListPostCommentsResult comments) {
		return new CommentPageResponse(
				toCommentSummaryResponses(comments.items()),
				comments.page(),
				comments.size(),
				comments.totalElements(),
				comments.totalPages(),
				comments.first(),
				comments.last()
		);
	}

	private static List<CommentSummaryResponse> toCommentSummaryResponses(List<CommentSummaryResult> comments) {
		return comments.stream()
				.map(comment -> new CommentSummaryResponse(
						comment.id(),
						comment.authorMemberId(),
						comment.author(),
						comment.content(),
						comment.createdAt()
				))
				.toList();
	}
}
