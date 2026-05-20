package com.example.post.global.web.swagger;

import com.example.post.global.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.MediaType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "게시글 상세 조회", description = "게시글 식별자로 게시글 1건을 조회합니다.")
@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "게시글 조회 성공",
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
				responseCode = "404",
				description = "게시글 없음",
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = ApiResponse.class)
				)
		)
})
public @interface ReadPostApiDocs {
}
