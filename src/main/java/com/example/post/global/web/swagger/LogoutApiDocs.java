package com.example.post.global.web.swagger;

import io.swagger.v3.oas.annotations.Operation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "로그아웃", description = "Redis에 저장된 refreshToken을 삭제합니다.")
@io.swagger.v3.oas.annotations.responses.ApiResponse(
		responseCode = "204",
		description = "로그아웃 성공"
)
public @interface LogoutApiDocs {
}
