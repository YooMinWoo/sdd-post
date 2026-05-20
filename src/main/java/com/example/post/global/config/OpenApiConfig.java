package com.example.post.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI openAPI() {
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes(
								"bearerAuth",
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
						))
				.info(new Info()
						.title("게시판 API")
						.description("Spring Boot 기반 게시판 프로젝트의 REST API 문서입니다.")
						.version("0.0.1"))
				.servers(List.of(new Server()
						.url("/")
						.description("현재 서버")))
				.tags(List.of(
						new Tag()
								.name("게시글")
								.description("게시글 작성 API"),
						new Tag()
								.name("인증/회원")
								.description("회원가입 API")
				));
	}
}
