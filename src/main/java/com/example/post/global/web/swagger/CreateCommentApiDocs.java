package com.example.post.global.web.swagger;

import com.example.post.global.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.MediaType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "댓글 작성", description = "로그인한 회원이 삭제되지 않은 게시글에 댓글을 작성합니다.")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "201",
				description = "댓글 작성 성공",
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = ApiResponse.class)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "요청 값 오류",
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = ApiResponse.class)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "401",
				description = "인증 실패",
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = ApiResponse.class)
				)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "게시글 없음",
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = ApiResponse.class)
				)
		)
})
public @interface CreateCommentApiDocs {
}
