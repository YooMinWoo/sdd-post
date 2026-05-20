package com.example.post.global.security;

import com.example.post.global.exception.BusinessException;
import com.example.post.global.web.ApiResponse;
import com.example.post.member.application.port.out.AccessTokenMemberClaims;
import com.example.post.member.application.port.out.TokenProviderPort;
import com.example.post.member.exception.MemberErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";

	private final TokenProviderPort tokenProviderPort;
	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	public JwtAuthenticationFilter(TokenProviderPort tokenProviderPort) {
		this.tokenProviderPort = tokenProviderPort;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorization == null || authorization.isBlank()) {
			filterChain.doFilter(request, response);
			return;
		}
		if (!authorization.startsWith(BEARER_PREFIX)) {
			writeInvalidAccessTokenResponse(request, response);
			return;
		}

		try {
			AccessTokenMemberClaims claims = tokenProviderPort.extractAccessTokenMember(
					authorization.substring(BEARER_PREFIX.length())
			);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					new AuthenticatedMemberPrincipal(claims.memberId(), claims.email(), claims.nickname()),
					null,
					List.of()
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		}
		catch (BusinessException exception) {
			SecurityContextHolder.clearContext();
			writeInvalidAccessTokenResponse(request, response);
		}
	}

	private void writeInvalidAccessTokenResponse(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(
				response.getWriter(),
				ApiResponse.error(
						MemberErrorCode.INVALID_ACCESS_TOKEN.code(),
						MemberErrorCode.INVALID_ACCESS_TOKEN.description(),
						request.getRequestURI(),
						Instant.now()
				)
		);
	}
}
