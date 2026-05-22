package com.example.post.global.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class SecurityConfigTest {

	@Test
	void exposesConfiguredFrontendOriginForCorsRequests() {
		CorsConfigurationSource source = new SecurityConfig()
				.corsConfigurationSource("http://localhost:5173,https://front.example.com");

		CorsConfiguration configuration = source.getCorsConfiguration(new MockHttpServletRequest("OPTIONS", "/posts"));

		assertNotNull(configuration);
		assertEquals(
				java.util.List.of("http://localhost:5173", "https://front.example.com"),
				configuration.getAllowedOrigins()
		);
		assertEquals(
				java.util.List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"),
				configuration.getAllowedMethods()
		);
		assertEquals(
				java.util.List.of("Authorization", "Content-Type"),
				configuration.getAllowedHeaders()
		);
	}
}
