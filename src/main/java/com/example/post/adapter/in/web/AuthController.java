package com.example.post.adapter.in.web;

import com.example.post.application.port.in.SignupCommand;
import com.example.post.application.port.in.SignupResult;
import com.example.post.application.port.in.SignupUseCase;
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
@RequestMapping("/auth")
@Tag(name = "인증/회원", description = "회원가입 API")
public class AuthController {

	private final SignupUseCase signupUseCase;

	public AuthController(SignupUseCase signupUseCase) {
		this.signupUseCase = signupUseCase;
	}

	@PostMapping("/signup")
	@Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 새 회원을 생성합니다.")
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "회원가입 성공",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = SignupResponse.class),
							examples = @ExampleObject(value = """
									{
									  "id": 1,
									  "email": "minu@example.com",
									  "nickname": "minu",
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
							schema = @Schema(implementation = ErrorResponse.class)
					)
			),
			@ApiResponse(
					responseCode = "409",
					description = "이미 가입된 이메일",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{
									  "code": "DUPLICATE_EMAIL",
									  "message": "이미 가입된 이메일입니다.",
									  "path": "/auth/signup",
									  "timestamp": "2026-05-20T00:00:00Z",
									  "errors": []
									}
									""")
					)
			)
	})
	public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
		SignupResult result = signupUseCase.signup(
				new SignupCommand(request.email(), request.password(), request.nickname())
		);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new SignupResponse(
						result.id(),
						result.email(),
						result.nickname(),
						result.createdAt()
				));
	}
}
