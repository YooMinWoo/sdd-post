package com.example.post.member.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.post.member.domain.model.Member;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class MemberPersistenceAdapterTest {

	@Autowired
	private MemberJpaRepository memberJpaRepository;

	@Test
	void savesMember() {
		MemberPersistenceAdapter adapter = new MemberPersistenceAdapter(memberJpaRepository);
		Member member = Member.create(
				"minu@example.com",
				"$2a$10$abcdefghijklmnopqrstuvabcdefghiabcdefghiabcdefghiabcdef",
				"minu",
				Instant.parse("2026-05-20T00:00:00Z")
		);

		Member savedMember = adapter.save(member);

		assertNotNull(savedMember.getId());
		assertEquals("minu@example.com", savedMember.getEmail());
		assertEquals("$2a$10$abcdefghijklmnopqrstuvabcdefghiabcdefghiabcdefghiabcdef", savedMember.getPasswordHash());
		assertEquals("minu", savedMember.getNickname());
		assertEquals(Instant.parse("2026-05-20T00:00:00Z"), savedMember.getCreatedAt());
	}

	@Test
	void checksEmailExists() {
		MemberPersistenceAdapter adapter = new MemberPersistenceAdapter(memberJpaRepository);
		adapter.save(Member.create(
				"minu@example.com",
				"$2a$10$abcdefghijklmnopqrstuvabcdefghiabcdefghiabcdefghiabcdef",
				"minu",
				Instant.parse("2026-05-20T00:00:00Z")
		));

		assertTrue(adapter.existsByEmail("minu@example.com"));
	}
}
