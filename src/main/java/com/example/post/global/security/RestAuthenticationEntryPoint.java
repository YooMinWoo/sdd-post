package com.example.post.global.security;

import com.example.post.global.web.ApiResponse;
import com.example.post.member.exception.MemberErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException
	) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(
				response.getWriter(),
				ApiResponse.error(
						MemberErrorCode.UNAUTHORIZED.code(),
						MemberErrorCode.UNAUTHORIZED.description(),
						request.getRequestURI(),
						Instant.now()
				)
		);
	}
}
