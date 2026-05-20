package com.example.post.board.adapter.in.web;

import com.example.post.board.application.port.in.CreatePostCommand;
import com.example.post.board.application.port.in.CreatePostResult;
import com.example.post.board.application.port.in.CreatePostUseCase;
import com.example.post.global.exception.BusinessException;
import com.example.post.global.security.AuthenticatedMemberPrincipal;
import com.example.post.global.web.ApiResponse;
import com.example.post.global.web.swagger.CreatePostApiDocs;
import com.example.post.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "게시글", description = "게시글 작성 API")
public class PostController {

	private final CreatePostUseCase createPostUseCase;

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
								result.id(),
								result.title(),
								result.content(),
								result.authorMemberId(),
								result.author(),
								result.createdAt()
						)
				));
	}
}
