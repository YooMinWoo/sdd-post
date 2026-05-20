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
import com.example.post.global.web.ApiResponse;
import com.example.post.global.web.swagger.LoginApiDocs;
import com.example.post.global.web.swagger.LogoutApiDocs;
import com.example.post.global.web.swagger.RefreshTokenApiDocs;
import com.example.post.global.web.swagger.SignupApiDocs;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
	@SignupApiDocs
	public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody SignupRequest request) {
		SignupResult result = signupUseCase.signup(
				new SignupCommand(request.email(), request.password(), request.nickname())
		);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(
						"회원가입이 완료되었습니다.",
						new SignupResponse(
								result.id(),
								result.email(),
								result.nickname(),
								result.createdAt()
						)
				));
	}

	@PostMapping("/login")
	@LoginApiDocs
	public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(ApiResponse.success(
				"로그인되었습니다.",
				toTokenResponse(loginUseCase.login(new LoginCommand(request.email(), request.password())))
		));
	}

	@PostMapping("/refresh")
	@RefreshTokenApiDocs
	public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshTokenRequest request) {
		return ResponseEntity.ok(ApiResponse.success(
				"토큰이 재발급되었습니다.",
				toTokenResponse(refreshTokenUseCase.refresh(new RefreshTokenCommand(request.refreshToken())))
		));
	}

	@PostMapping("/logout")
	@LogoutApiDocs
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
