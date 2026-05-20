package com.example.post.member.adapter.out.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class RefreshTokenRedisAdapterTest {

	@Test
	void savesRefreshTokenWithTtl() {
		StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
		@SuppressWarnings("unchecked")
		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		RefreshTokenRedisAdapter adapter = new RefreshTokenRedisAdapter(redisTemplate);

		adapter.save(1L, "refresh-token", Duration.ofDays(14));

		verify(valueOperations).set("refresh-token:1", "refresh-token", Duration.ofDays(14));
	}

	@Test
	void findsRefreshToken() {
		StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
		@SuppressWarnings("unchecked")
		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("refresh-token:1")).thenReturn("refresh-token");
		RefreshTokenRedisAdapter adapter = new RefreshTokenRedisAdapter(redisTemplate);

		assertEquals("refresh-token", adapter.findByMemberId(1L).orElseThrow());
	}

	@Test
	void deletesRefreshToken() {
		StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
		RefreshTokenRedisAdapter adapter = new RefreshTokenRedisAdapter(redisTemplate);

		adapter.deleteByMemberId(1L);

		verify(redisTemplate).delete("refresh-token:1");
	}

	@Test
	void returnsEmptyWhenRefreshTokenDoesNotExist() {
		StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
		@SuppressWarnings("unchecked")
		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		RefreshTokenRedisAdapter adapter = new RefreshTokenRedisAdapter(redisTemplate);

		assertTrue(adapter.findByMemberId(1L).isEmpty());
	}
}
