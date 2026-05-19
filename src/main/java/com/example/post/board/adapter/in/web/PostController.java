package com.example.post.board.adapter.in.web;

import com.example.post.board.application.port.in.CreatePostCommand;
import com.example.post.board.application.port.in.CreatePostResult;
import com.example.post.board.application.port.in.CreatePostUseCase;
import com.example.post.global.web.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@Tag(name = "게시글", description = "게시글 작성 API")
public class PostController {

	private final CreatePostUseCase createPostUseCase;

	public PostController(CreatePostUseCase createPostUseCase) {
		this.createPostUseCase = createPostUseCase;
	}

	@PostMapping
	@Operation(summary = "게시글 작성", description = "제목, 본문, 작성자를 입력받아 새 게시글을 작성합니다.")
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "게시글 작성 성공",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = CreatePostResponse.class),
							examples = @ExampleObject(value = """
									{
									  "id": 1,
									  "title": "안녕하세요",
									  "content": "첫 번째 게시글입니다.",
									  "author": "minu",
									  "createdAt": "2026-05-20T00:00:00Z"
									}
									""")
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "요청 값 오류",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{
									  "code": "INVALID_REQUEST",
									  "message": "title is required",
									  "path": "/posts",
									  "timestamp": "2026-05-20T00:00:00Z",
									  "errors": []
									}
									""")
					)
			)
	})
	public ResponseEntity<CreatePostResponse> createPost(@RequestBody CreatePostRequest request) {
		CreatePostResult result = createPostUseCase.createPost(
				new CreatePostCommand(request.title(), request.content(), request.author())
		);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new CreatePostResponse(
						result.id(),
						result.title(),
						result.content(),
						result.author(),
						result.createdAt()
				));
	}
}
