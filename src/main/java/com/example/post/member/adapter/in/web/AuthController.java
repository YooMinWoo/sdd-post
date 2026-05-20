package com.example.post.member.adapter.in.web;

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
import com.example.post.global.web.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증/회원", description = "회원가입과 토큰 기반 인증 API")
public class AuthController {

	private final SignupUseCase signupUseCase;
	private final LoginUseCase loginUseCase;
	private final RefreshTokenUseCase refreshTokenUseCase;
	private final LogoutUseCase logoutUseCase;

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

	@PostMapping("/login")
	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 accessToken과 refreshToken을 발급합니다.")
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "로그인 성공",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = TokenResponse.class),
							examples = @ExampleObject(value = """
									{
									  "tokenType": "Bearer",
									  "accessToken": "access.jwt.token",
									  "refreshToken": "refresh.jwt.token",
									  "expiresIn": 900
									}
									""")
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "이메일 또는 비밀번호 오류",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ErrorResponse.class)
					)
			)
	})
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(toTokenResponse(
				loginUseCase.login(new LoginCommand(request.email(), request.password()))
		));
	}

	@PostMapping("/refresh")
	@Operation(summary = "토큰 재발급", description = "refreshToken을 검증하고 accessToken과 refreshToken을 새로 발급합니다.")
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "토큰 재발급 성공",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = TokenResponse.class)
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "유효하지 않은 refreshToken",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ErrorResponse.class)
					)
			)
	})
	public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
		return ResponseEntity.ok(toTokenResponse(
				refreshTokenUseCase.refresh(new RefreshTokenCommand(request.refreshToken()))
		));
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "Redis에 저장된 refreshToken을 삭제합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "로그아웃 성공")
	})
	public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
		logoutUseCase.logout(new LogoutCommand(request.refreshToken()));
		return ResponseEntity.noContent().build();
	}

	private static TokenResponse toTokenResponse(TokenResult result) {
		return new TokenResponse(
				result.tokenType(),
				result.accessToken(),
				result.refreshToken(),
				result.expiresIn()
		);
	}
}
